package com.zrulin.dao.impl;

import com.zrulin.dao.UserDao;

import java.util.Arrays;
import java.util.List;

/**
 * @author zrulin
 * @create 2022-03-30 8:54
 */
public class UserDaoImpl1 implements UserDao {

    @Override
    public List<Integer> test() {
        return Arrays.asList(4,5,6);
    }
}
