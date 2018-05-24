package com.github.boot.framework.web.authentication;

import javax.servlet.http.HttpServletResponse;

/**
 * TokenManager
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public interface TokenManager {

    /**
     * 解析TOKEN
     * @param token
     * @return
     */
    Authentication parseToken(String token);

    /**
     * 创建TOKEN
     * @param authentication
     * @return
     */
    String createToken(Authentication authentication);

    /**
     * 将TOKEN发送到客户端
     * @param token
     * @param response
     * @return
     */
    String sendToken(String token, HttpServletResponse response);

    /**
     * 获取TOKEN名称
     * @param tokenName
     * @return
     */
    void setTokenName(String tokenName);
}
