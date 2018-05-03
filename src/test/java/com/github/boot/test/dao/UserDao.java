package com.github.boot.test.dao;

import com.github.boot.framework.jpa.dao.BaseDao;
import com.github.boot.test.entity.User;
import org.springframework.stereotype.Repository;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/
@Repository
public interface UserDao extends BaseDao<User, Long> {


}
