package com.github.boot.framework.web.form;

import javax.validation.constraints.NotNull;

/**
 * 根据ID查询实体数据表单
 *
 * @author chenjianhui
 * @create 2018/05/09
 **/
public class GetForm<ID> implements Form {

    @NotNull
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
