package com.github.boot.framework.web.form;


import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.jpa.SortDirection;
import com.github.boot.framework.jpa.SortProperty;
import com.github.boot.framework.support.validate.ValueSet;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 分页查询表单
 *
 * @author chenjianhui
 * @date 2017/3/15
 */
public class PageForm<T> implements Form, Criterion<T> {

    /**
     * 页索引， 从0开始
     */
    @Min(0)
    @NotNull
    private Integer page = 0;

    /**
     * 页大小, 默认10
     */
    @NotNull
    @Size(min = 1, max = 100)
    private Integer size = 10;

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

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

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

    @Override
    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public Integer getPage() {
        return page;
    }
}
