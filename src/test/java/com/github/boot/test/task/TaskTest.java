package com.github.boot.test.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TaskTest
 *
 * @author chenjianhui
 * @data 2018/06/06
 **/
@Component
public class TaskTest {

    @Scheduled(fixedRate = 4000)
    public void testTask(){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(1);
    }

    @Async
    public void testAsync(){
        try {
            Thread.sleep(5000);
            System.out.println("异步执行》》》》》》》》》》》》》》");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
