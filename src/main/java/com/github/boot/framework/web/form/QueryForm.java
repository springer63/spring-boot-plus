package com.github.boot.framework.web.form;

import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.support.validate.ValueSet;

/**
 * QueryForm
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public abstract class QueryForm<T> implements Form, Criterion<T> {

    /**
     * 排序字段名称， 按什么字段排序
     */
    private String sortProperty;

    /**
     * 排序方向 ASC, DESC
     */
    @ValueSet({"ASC", "DESC"})
    private String sortDirection = "DESC";

    @Override
    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    @Override
    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
