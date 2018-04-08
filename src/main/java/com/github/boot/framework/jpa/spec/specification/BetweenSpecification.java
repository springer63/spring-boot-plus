package com.github.boot.framework.jpa.spec.specification;

import org.springframework.data.domain.Range;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * BetweenSpecification
 *
 * @author cjh
 * @date 2017/3/12
 */
public class BetweenSpecification<T> extends AbstractSpecification<T> {
    private final String property;
    private final Range range;

    public BetweenSpecification(String property, Range range) {
        this.property = property;
        this.range = range;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = getRoot(property, root);
        String field = getProperty(property);
        return cb.between(from.get(field), range.getLowerBound(), range.getUpperBound());
    }
}
