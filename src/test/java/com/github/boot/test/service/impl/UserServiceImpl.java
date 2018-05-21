package com.github.boot.test.service.impl;

import com.github.boot.framework.service.impl.BaseServiceImpl;
import com.github.boot.test.entity.User;
import com.github.boot.test.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

}

