package com.github.boot.test.form;

import com.github.boot.framework.web.form.Form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/
public class UserGetForm implements Form {

    @NotNull
    @Min(1)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
