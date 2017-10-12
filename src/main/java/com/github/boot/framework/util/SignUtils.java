package com.github.boot.framework.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * 接口签名工具
 * Created by cjh on 2017/7/14.
 */
public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    /**
     * 构建MD5签名
     * @param secretKey
     * @return
     */
    public static String buildSign(Object paramObj, String secretKey){
        String paramStr = buildParamStr(paramObj);
        paramStr = paramStr + "&key=" + secretKey;
        logger.info("加密签名DEEPLINK: " + paramStr);
        return DigestUtils.md5Hex(paramStr).toUpperCase();
    }

    /**
     * 校验MD5签名
     * @param secretKey
     * @return
     */
    public static boolean checkSign(Object paramObj, String sign, String secretKey){
        String targetSign = buildSign(paramObj, secretKey);
        if(targetSign.equals(sign)){
            return true;
        }
        logger.error("微信支付签名验证失败：原始签名：{}，生成签名：{}", sign, targetSign);
        return false;
    }

    /**
     * 构建DeepLink参数字符串
     * @param paramObj
     * @return
     */
    @SuppressWarnings("unchecked")
	private static String buildParamStr(Object paramObj){
        Map<String, Object> paramMap;
        if(paramObj instanceof Map){
            paramMap = (Map<String, Object>) paramObj;
        }else{
            paramMap = new HashMap<>();
            BeanWrapper beanWrapper = new BeanWrapperImpl(paramMap);
            PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                Object value = beanWrapper.getPropertyValue(pd.getName());
                if(value != null){
                    paramMap.put(pd.getName(), value);
                }
            }
        }
        List<String> keys = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        StringBuffer buffer = new StringBuffer();
        for (String key : keys){
            if("sign".equals(key)){
                continue;
            }
            Object value = paramMap.get(key);
            if(value == null){
                continue;
            }
            if("".equals(value.toString().trim())){
                continue;
            }
            buffer.append("&").append(key).append("=").append(value);
        }
        String deeplink = buffer.substring(1);
        logger.info("签名DEEPLINK: " + deeplink);
        return deeplink;
    }


}
