package com.github.boot.framework.util;

import com.github.boot.framework.jpa.Condition;
import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.jpa.Order;
import com.github.boot.framework.jpa.Orders;
import com.github.boot.framework.jpa.spec.PredicateBuilder;
import com.github.boot.framework.web.form.AbstractPageForm;
import com.github.boot.framework.jpa.spec.Specifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by cjh on 2017/6/21.
 */
public class SpecUtils {

    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     * 构建分页查询条件
     * @param form
     * @param <T>
     * @return
     */
    public static <T> Pageable pageable(AbstractPageForm<T> form){
        Sort sort = null;
        Order order = form.getClass().getAnnotation(Order.class);
        if(order != null){
            sort = new Sort(order.direction(), order.orderBy().split(","));
        }
        Orders orders = form.getClass().getAnnotation(Orders.class);
        if(orders != null){
            List<Sort.Order> orderList = new ArrayList<>(orders.value().length);
            for(Order o : orders.value()){
                orderList.add(new Sort.Order(o.direction(), o.orderBy()));
            }
            sort = new Sort(orderList);
        }
        if(ValidUtils.isValid(form.getOrderBy())){
            String[] orderStrs = form.getOrderBy().split(",");
            List<Sort.Order> orderList = new ArrayList<>(orderStrs.length);
            for (String or : orderStrs){
                String[] array = or.split(" ");
                orderList.add(new Sort.Order(Sort.Direction.fromString(array[1].trim()), array[0].trim()));
            }
            sort = new Sort(orderList);
        }
        if(sort == null){
            return new PageRequest(form.getPage(), form.getSize());
        }
        return new PageRequest(form.getPage(), form.getSize(), sort);
    }

    /**
     * 构建复合查询条件
     * @param criterion
     * @param <T>
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Specification<T> condition(Criterion<T> criterion){
        Field[] fields = criterion.getClass().getDeclaredFields();
        PredicateBuilder<T> builder = Specifications.<T>and();
        String property;
        Object value;
        Field upperField = null;
        Field lowerField = null;
        Condition condition;
        for (Field field : fields) {
            condition = field.getAnnotation(Condition.class);
            if(condition == null){
                continue;
            }
            value = ReflectionUtils.getFieldValue(criterion, field.getName());
            if(value == null){
                continue;
            }
            if(StringUtils.isEmpty(value.toString().trim())){
                continue;
            }
            property = ValidUtils.isValid(condition.property()) ? condition.property() : field.getName();
            switch (condition.operator()){
                case EQ:
                    builder.eq(property, value);
                    break;
                case GT:
                    if(value instanceof Date){
                        builder.between(property, new Range<>((Date) value, new Date(DateUtils.MAX_SECOND)));
                    }else{
                        builder.gt(property, (Comparable<?>) value);
                    }
                    break;
                case GE:
                    if(value instanceof Date){
                        builder.between(property, new Range<>((Date) value, new Date(DateUtils.MAX_SECOND)));
                    }else{
                        builder.ge(property, (Comparable<?>) value);
                    }
                    break;
                case LE:
                    if(value instanceof Date){
                        builder.between(property, new Range<>(new Date(DateUtils.MIN_SECOND), (Date) value));
                    }else{
                        builder.le(property, (Comparable<?>) value);
                    }
                    break;
                case LT:
                    if(value instanceof Date){
                        builder.between(property, new Range<>(new Date(DateUtils.MIN_SECOND), (Date) value));
                    }else{
                        builder.lt(property, (Comparable<?>) value);
                    }
                    break;
                case IN:
                    if(value instanceof Collection){
                        buildInSpec(builder, property, ((Collection<?>) value).toArray());
                    }else{
                        builder.in(property, value);
                    }
                    break;
                case NOT_IN:
                    if(value instanceof Collection){
                        buildNotInSpec(builder, property, ((Collection<?>) value).toArray());
                    }else{
                        builder.notIn(property, value);
                    }
                    break;
                case LIKE:
                    if(!StringUtils.isEmpty(value.toString())){
                        builder.like(property, "%" + value + "%");
                    }
                    break;
                case NQ:
                    builder.ne(property,value);
                    break;
                case BETWEEN_LOWER:
                    lowerField = field;
                    break;
                case BETWEEN_UPPER:
                    upperField = field;
                    break;
                default:
                    break;
            }
        }
        if(upperField != null || lowerField != null){
            if(upperField != null){
                condition = upperField.getAnnotation(Condition.class);
                property = ValidUtils.isValid(condition.property()) ? condition.property() : upperField.getName();
            }else {
                condition = lowerField.getAnnotation(Condition.class);
                property = ValidUtils.isValid(condition.property()) ? condition.property() : lowerField.getName();
            }
            Object lower = lowerField == null ? new Date(DateUtils.MIN_SECOND) : ReflectionUtils.getFieldValue(criterion, lowerField.getName());
            Object upper = upperField == null ? new Date(DateUtils.MAX_SECOND) : ReflectionUtils.getFieldValue(criterion, upperField.getName());
            builder.between(property, new Range((Comparable<?>)lower, (Comparable<?>)upper));
        }
        return builder.build();
    }

    private static <T> void buildInSpec(PredicateBuilder<T> builder, String property, Object[] args){
        Specifications.<T>and();
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("builder" , builder);
        evaluationContext.setVariable("property", property);
        evaluationContext.setVariable("args", args);
        StringBuffer buffer = new StringBuffer("#builder.in(#property");
        for (int i = 0; i < args.length; i++) {
            buffer.append(", #args[").append(i).append("]");
        }
        buffer.append(")");
        parser.parseExpression(buffer.toString()).getValue(evaluationContext);
    }

    private static <T> void buildNotInSpec(PredicateBuilder<T> builder, String property, Object[] args){
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("builder" , builder);
        evaluationContext.setVariable("property", property);
        StringBuffer buffer = new StringBuffer("#builder.notIn(#property");
        for (int i = 0; i < args.length; i++) {
            buffer.append(", #args[").append(i).append("]");
        }
        buffer.append(")");
        parser.parseExpression(buffer.toString()).getValue(evaluationContext);
    }

    public <T> Specification<T> or(Specification<T> thisSpec, Specification<T> otherSpec){
        return org.springframework.data.jpa.domain.Specifications.where(thisSpec).or(otherSpec);
    }

    public <T> Specification<T> and(Specification<T> thisSpec, Specification<T> otherSpec){
        return org.springframework.data.jpa.domain.Specifications.where(thisSpec).and(otherSpec);
    }


}
