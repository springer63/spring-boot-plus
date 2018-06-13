package com.github.boot.framework.web.result;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

/**
 * PageWrap
 *
 * @author chenjianhui
 * @data 2018/06/13
 **/
public class PageWrap<T> extends PageImpl<T> {

    private static final long serialVersionUID = 1L;
    private int number;
    private int size;
    private long totalElements;
    private List<T> content;
    private Sort sort;

    public PageWrap() {
        super(Collections.EMPTY_LIST);
    }

    public PageWrap(List content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public PageImpl<T> page() {
        return new PageImpl<>(getContent(), new PageRequest(getNumber(), getSize(), getSort()), getTotalElements());
    }

}
