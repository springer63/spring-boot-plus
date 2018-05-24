package com.github.boot.framework.web.form;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询表单
 *
 * @author chenjianhui
 * @date 2017/3/15
 */
public class PageForm<T> extends QueryForm<T> {

    /**
     * 页索引， 从0开始
     */
    @Min(0)
    private Integer page = 0;

    /**
     * 页大小, 默认10
     */
    @Min(1)
    @Max(200)
    private Integer size = 10;

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setSize(Integer size) {
        this.size = size;
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
