package com.github.boot.framework.web.authentication;

import java.util.Date;

/**
 * Authentication
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public class Authentication {

    /**
     * 授权用户id
     */
    private Object userId;

    /**
     * 授权用户信息
     */
    private Object userInfo;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 是否授权
     */
    private boolean authenticated;

    public Authentication() {
    }

    /**
     * 构造器
     * @param userId 用户ID
     * @param userInfo 用户信息
     */
    public Authentication(Object userId, Object userInfo) {
        this.userId = userId;
        this.userInfo = userInfo;
        this.authenticated = true;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Object userInfo) {
        this.userInfo = userInfo;
    }

    public Date getExpireTime() {
        if(expireTime == null){
            return new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
        }
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
