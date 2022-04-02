package com.zrulin.dao.impl;

import com.zrulin.dao.UserDao;

import java.util.Arrays;
import java.util.List;

/**
 * @author zrulin
 * @create 2022-03-30 8:14
 */
public class UserDaoImpl implements UserDao {

    @Override
    public List<Integer> test() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        return list;
    }
}
