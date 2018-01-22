package com.github.boot.framework.web.form;


import com.github.boot.framework.jpa.Criterion;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询表单
 *
 * @author chenjianhui
 * @date 2017/3/15
 */
public abstract class AbstractPageForm<T> implements Form, Criterion<T> {

    @Min(0)
    private int page = 0;

    @Max(100)
    private int size = 10;

    private String orderBy;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
