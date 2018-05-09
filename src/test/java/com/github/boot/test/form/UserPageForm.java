package com.github.boot.test.form;

import com.github.boot.framework.jpa.Condition;
import com.github.boot.framework.jpa.Operator;
import com.github.boot.framework.web.form.PageForm;
import com.github.boot.test.entity.User;


/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/
public class UserPageForm extends PageForm<User> {

    @Condition
    private Long id;

    @Condition(operator = Operator.LIKE)
    private String name;

    @Condition
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
