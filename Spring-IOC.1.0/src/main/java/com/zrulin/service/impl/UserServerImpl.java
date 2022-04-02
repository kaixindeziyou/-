package com.zrulin.service.impl;


import com.zrulin.dao.UserDao;
import com.zrulin.factory.BeanFactory;
import com.zrulin.service.UserService;

import java.util.List;

/**
 * @author zrulin
 * @create 2022-03-30 8:12
 */
public class UserServerImpl implements UserService {

    public UserServerImpl(){
        for (int i = 0 ; i< 10 ;i++){
            System.out.println(BeanFactory.getDao("userDao"));
        }
    }

    private UserDao userDao = (UserDao) BeanFactory.getDao("userDao");

    @Override
    public List<Integer> test() {
        List<Integer> test = userDao.test();

        return test;
    }
}
