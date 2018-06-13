package com.github.boot.test.utils;

import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.web.result.ResultJsonSerializer;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println(0.1 * 0.000004);
        map.setA(new BigDecimal("0.0000000001"));
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

    @Test
    public void testPage() throws IOException {
        ResultJsonSerializer serializer = new ResultJsonSerializer();
        List<Integer> content = new ArrayList();
        content.add(1);
        content.add(2);
        Pageable pageable = new PageRequest(1,2);

        PageImpl<Integer> page = new PageImpl<>(content, pageable, 123);
        String s = serializer.writeValueAsString(page);
        System.out.println(s);
        Page page1 = serializer.readValue(s, Page.class);
        System.out.println(page);
    }
}
