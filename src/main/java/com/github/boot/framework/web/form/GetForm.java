package com.github.boot.framework.web.form;

/**
 * GetForm
 *
 * @author chenjianhui
 * @create 2018/05/15
 **/
public class GetForm<T> implements Form {

    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
