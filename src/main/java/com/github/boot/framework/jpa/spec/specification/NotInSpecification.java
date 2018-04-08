package com.github.boot.framework.jpa.spec.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * NotInSpecification
 *
 * @author cjh
 * @date 2017/3/12
 */
public class NotInSpecification<T> extends AbstractSpecification<T> {
    private String property;
    private Object[] values;

    public NotInSpecification(String property, Object[] values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = getRoot(property, root);
        String field = getProperty(property);
        return from.get(field).in(values).not();
    }
}
