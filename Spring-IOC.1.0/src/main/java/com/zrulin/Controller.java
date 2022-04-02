package com.zrulin;

import com.zrulin.service.UserService;
import com.zrulin.service.impl.UserServerImpl;
import org.junit.Test;

import java.util.List;

/**
 * @author zrulin
 * @create 2022-03-30 8:21
 */
public class Controller {

    private UserService userService = new UserServerImpl();

    @Test
    public void test(){
        List<Integer> test = userService.test();
        for (Integer data : test){
            System.out.println(data);
        }
    }
}
