package com.github.boot.test.utils;

import com.github.boot.framework.util.JsonUtils;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * JsonUtils
 *
 * @author chenjianhui
 * @data 2018/06/07
 **/
public class JsonUtilsTest {

    @Test
    public void testJson(){
        Model map = new Model();
        System.out.println(0.1 * 0.004);
        map.setA(new BigDecimal(0.1 * 0.000004));
        map.setA(new BigDecimal(0.1 * 0.000004));
        String json = JsonUtils.toJson(map);
        System.out.println(json);
    }

    public static class Model {
        private BigDecimal a;

        public BigDecimal getA() {
            return a;
        }

        public void setA(BigDecimal a) {
            this.a = a;
        }
    }
}
