package com.zrulin.pojo;

import com.zrulin.myspring.annotation.Component;
import com.zrulin.myspring.annotation.Value;

/**
 * @author zrulin
 * @create 2022-04-20 16:44
 */

@Component("a")
public class Account {
    private String name;
    private String password;
    @Value("32")
    private Integer age;
    @Value("2425.3")
    private Double score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", score=" + score +
                '}';
    }
}
