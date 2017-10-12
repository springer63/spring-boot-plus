package com.github.boot.framework.web.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by cjh on 2017/7/17.
 */
public class OpenForm implements Form {

    /**
     * 访问接口的AccessKey, 由平台分配
     */
    @NotBlank
    private String accessKey;

    /**
     * 访问接口签名
     */
    @NotBlank
    private String sign;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
