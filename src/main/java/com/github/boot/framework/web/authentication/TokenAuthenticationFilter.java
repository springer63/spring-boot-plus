package com.github.boot.framework.web.authentication;

import com.github.boot.framework.util.ConstUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * TokenAuthenticationFilter
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private String tokenName;

    private TokenManager tokenManager;

    private AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = authenticationManager.getTokenManager();
        this.tokenName = authenticationManager.getApplication() + "_" + ConstUtils.TOKEN_NAME;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session.getAttribute(ConstUtils.SESSION_USER) != null){
            filterChain.doFilter(request, response);
            return;
        }
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0){
            filterChain.doFilter(request, response);
            return;
        }
        String token = null;
        for (Cookie c : cookies){
            if(c.getName().equals(this.tokenName)){
                token = c.getValue();
                break;
            }
        }
        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = tokenManager.parseToken(token);
        if(authentication.getExpireTime().getTime() < System.currentTimeMillis()){
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authenticate = authenticationManager.authenticate(authentication);
        session.setAttribute(ConstUtils.SESSION_USER_ID, authenticate.getUserId());
        session.setAttribute(ConstUtils.SESSION_USER, authenticate.getUserInfo());
        filterChain.doFilter(request, response);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
}
