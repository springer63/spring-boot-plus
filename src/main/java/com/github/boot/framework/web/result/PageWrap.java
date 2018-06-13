package com.github.boot.framework.web.result;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * PageWrap
 *
 * @author chenjianhui
 * @data 2018/06/13
 **/
public class PageWrap<T> {

    private int number;
    private int size;
    private long totalElements;
    private List<T> content;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageImpl<T> page() {
        return new PageImpl<>(getContent(), new PageRequest(getNumber(), getSize()), getTotalElements());
    }

}
