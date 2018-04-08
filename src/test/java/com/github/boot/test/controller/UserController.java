package com.github.boot.test.controller;

import com.github.boot.framework.web.result.Json;
import com.github.boot.framework.web.result.Result;
import com.github.boot.test.entity.User;
import com.github.boot.test.form.UserGetForm;
import com.github.boot.test.form.UserPageForm;
import com.github.boot.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @Json(type = User.class, excludes = "phone")
    public Result<User> get(UserGetForm form){
        Result result = Result.success();
        User user = userService.findOne(form.getId());
        result.setData(user);
        return result;
    }

    @RequestMapping("/page")
    @Json(type = User.class, excludes = "phone")
    public Result<Page<User>> page(UserPageForm form){
        Result result = Result.success();
        Page<User> users = userService.page(form);
        result.setData(users);
        return result;
    }
}
