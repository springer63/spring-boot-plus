package com.github.boot.framework.web.result;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

/**
 * PageWrap
 *
 * @author chenjianhui
 * @data 2018/06/13
 **/
public class PageWrap<T> extends PageImpl<T> {

    private int number;

    private int size;

    private boolean first;

    private boolean last;

    private int numberOfElements;

    private int totalPages;

    private long totalElements;

    private List<T> content;

    private static Pageable DEFAULT_PAGEABLE = new PageRequest(0, 0);

    public PageWrap(){
        super(Collections.EMPTY_LIST, DEFAULT_PAGEABLE, 0);
    }

    public PageWrap(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @Override
    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    @Override
    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
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
    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public static <T> PageWrap<T> wrap(PageImpl<T> page){
        PageWrap wrap = new PageWrap();
        wrap.setContent(page.getContent());
        wrap.setFirst(page.isFirst());
        wrap.setLast(page.isLast());
        wrap.setNumberOfElements(page.getNumberOfElements());
        wrap.setSize(page.getSize());
        wrap.setNumber(page.getNumber());
        wrap.setTotalPages(page.getTotalPages());
        wrap.setTotalElements(page.getTotalElements());
        return wrap;
    }
}
