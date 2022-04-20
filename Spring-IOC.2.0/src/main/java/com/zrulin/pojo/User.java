package com.zrulin.pojo;


/**
 * @author zrulin
 * @create 2022-03-30 18:50
 */

import com.zrulin.myspring.annotation.Autowired;
import com.zrulin.myspring.annotation.Component;
import com.zrulin.myspring.annotation.Qualifier;
import com.zrulin.myspring.annotation.Value;

@Component
public class User {
    @Value("张三")
    private String name;
    @Value("2352j5i4jo32")
    private String password;

    @Autowired
    @Qualifier("a")
    private Account account;



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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", account=" + account +
                '}';
    }
}
