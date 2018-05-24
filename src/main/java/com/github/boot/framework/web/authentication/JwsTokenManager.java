package com.github.boot.framework.web.authentication;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * JwsTokenManager
 *
 * @author chenjianhui
 * @create 2018/05/24
 **/
public class JwsTokenManager implements TokenManager {

    /**
     * 加密KEY
     */
    private Key secretKey;

    /**
     * token 名称
     */
    private String tokenName;

    /**
     * 签名算法
     */
    private SignatureAlgorithm signatureAlgorithm;

    /**
     * 加密字符串
     */
    private String encodedKey = "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==";

    public JwsTokenManager() {
        this.signatureAlgorithm = SignatureAlgorithm.HS512;
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        this.secretKey = new SecretKeySpec(decodedKey, this.signatureAlgorithm.getJcaName());
    }

    @Override
    public Authentication parseToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        String userId = claims.getSubject();
        Date expireDate = claims.getExpiration();
        Authentication authentication = new Authentication();
        authentication.setAuthenticated(true);
        authentication.setExpireTime(expireDate);
        authentication.setUserId(userId);
        return authentication;
    }

    @Override
    public String createToken(Authentication authentication) {
        String token = Jwts.builder().setSubject(authentication.getUserId().toString())
                .setExpiration(authentication.getExpireTime())
                .signWith(signatureAlgorithm, secretKey).compact();
        return token;
    }

    @Override
    public String sendToken(String token, HttpServletResponse response) {
        Cookie tokenCookie = new Cookie(tokenName, token);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(tokenCookie);
        return token;
    }

    @Override
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }


}
