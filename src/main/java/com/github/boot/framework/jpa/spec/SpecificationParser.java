package com.github.boot.framework.jpa.spec;

import com.github.boot.framework.jpa.Condition;
import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.jpa.SortProperty;
import com.github.boot.framework.util.DateUtils;
import com.github.boot.framework.util.ReflectionUtils;
import com.github.boot.framework.util.StringUtils;
import com.github.boot.framework.util.ValidUtils;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Specification 查询解析工具
 *
 * @author chenjianhui
 * @create 2018/05/09
 **/
public class SpecificationParser {

    /**
     * SPEL表达式解析器
     */
    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     * 构建分页查询条件
     * @param criterion
     * @param <T>
     * @return
     */
    public static <T> Pageable pageable(Criterion<T> criterion){
        Sort sort = sort(criterion);
        if(sort != null){
            return new PageRequest(criterion.getPage(), criterion.getSize(), sort);
        }
        return new PageRequest(criterion.getPage(), criterion.getSize());
    }

    /**
     * 构建排序查询条件
     * @param criterion
     * @param <T>
     * @return
     */
    public static <T> Sort sort(Criterion<T> criterion){
        String sortProperty = criterion.getSortProperty();
        if(sortProperty != null){
            Sort sort = new Sort(Sort.Direction.fromString(criterion.getSortDirection()), sortProperty);
            return sort;
        }
        SortProperty[] sps = criterion.getClass().getAnnotationsByType(SortProperty.class);
        if(sps != null){
            List<Sort.Order> orders = Arrays.stream(sps)
                    .map(e -> new Sort.Order(Sort.Direction.fromString(e.direction()), e.value()))
                    .collect(Collectors.toList());
            Sort sort = new Sort(orders);
            return sort;
        }
        return null;
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
        PredicateBuilder<T> builder = Specifications.and();
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
