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
 * ID 用户ID类型
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
    private TokenManager tokenManager = new JwsTokenManager();

    /**
     * 用户授权
     * @param token
     * @return
     */
    public abstract Authentication authenticate(Authentication token);

    /**
     * 用户登录
     * @param authentication
     * @return
     */
    public Authentication login(Authentication authentication){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession();
        HttpServletResponse response = requestAttributes.getResponse();
        session.setAttribute(ConstUtils.SESSION_USER_ID, authentication.getUserId());
        session.setAttribute(ConstUtils.SESSION_USER, authentication.getUserInfo());
        tokenManager.sendToken(tokenManager.createToken(authentication), response);
        return authentication;
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
