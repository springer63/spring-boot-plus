package com.github.boot.framework.web.authentication;

import com.github.boot.framework.util.ConstUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AuthenticationManager
 *
 * T 用户类型
 * @author chenjianhui
 * @create 2018/05/24
 **/
public abstract class AuthenticationManager {

    /**
     * 应用名称
     */
    private String application = "";

    /**
     * Token 管理器
     */
    private TokenManager tokenManager = new JwtTokenManager();

    /**
     * 用户授权
     * @param userId
     * @return
     */
    public abstract Authentication authenticate(Object userId);

    /**
     * 用户登录
     * @param authentication
     * @return 返回TOKEN
     */
    public String login(Authentication authentication){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession();
        HttpServletResponse response = requestAttributes.getResponse();
        session.setAttribute(ConstUtils.SESSION_USER_ID, authentication.getUserId());
        session.setAttribute(ConstUtils.SESSION_USER, authentication.getUserInfo());
        String token = tokenManager.createToken(authentication);
        tokenManager.sendToken(token, response);
        return token;
    }

    /**
     * 获取授权用户信息
     * @return
     */
    public static Object getUser(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession();
        return session.getAttribute(ConstUtils.SESSION_USER);
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

}
