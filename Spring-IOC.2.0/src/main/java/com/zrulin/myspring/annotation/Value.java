package com.zrulin.myspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zrulin
 * @create 2022-04-20 16:45
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {//不像Component那样加上default，因为这里使用value就必须写值，不写就让他报错。
    String value();
}
