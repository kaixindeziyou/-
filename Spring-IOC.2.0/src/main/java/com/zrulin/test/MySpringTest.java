package com.zrulin.test;

import com.zrulin.myspring.MyAnnotationConfigApplicationContext;
import org.junit.Test;

/**
 * @author zrulin
 * @create 2022-04-20 16:48
 */
public class MySpringTest {
    @Test
    public void test(){
        MyAnnotationConfigApplicationContext myAnnotationConfigApplicationContext = new MyAnnotationConfigApplicationContext("com.zrulin.pojo");
        System.out.println(myAnnotationConfigApplicationContext.getBean("user"));
    }
}
