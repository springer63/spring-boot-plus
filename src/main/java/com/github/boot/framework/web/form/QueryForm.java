package com.github.boot.framework.web.form;

import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.jpa.SortDirection;
import com.github.boot.framework.jpa.SortProperty;
import com.github.boot.framework.support.validate.ValueSet;

/**
 * QueryForm
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public abstract class QueryForm<T> implements Form, Criterion<T> {

    /**
     * 排序字段
     */
    @SortProperty
    private String sortProperty;

    /**
     * 排序方向 ASC, DESC
     */
    @SortDirection
    @ValueSet({"ASC", "DESC"})
    private String sortDirection = "DESC";

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
