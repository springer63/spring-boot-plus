package com.github.boot.framework.web.form;


import com.github.boot.framework.jpa.Criterion;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询表单
 * Created by dell on 2017/3/15.
 */
public abstract class PageForm<T> implements Form, Criterion<T> {

    @Min(0)
    private int page = 0;

    @Max(100)
    private int count = 10;

    @Min(1)
    private Long from;

    private String orderBy;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getFrom() {
        if(from == null){
            from = new Long(page * count);
        }
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
