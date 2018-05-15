package com.github.boot.framework.web.form;

/**
 * DeleteForm
 *
 * @author chenjianhui
 * @create 2018/05/15
 **/
public class DeleteForm<T> implements Form {

    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
