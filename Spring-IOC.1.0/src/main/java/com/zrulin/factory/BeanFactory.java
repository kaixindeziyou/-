package com.zrulin.factory;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.zrulin.dao.UserDao;
import com.zrulin.dao.impl.UserDaoImpl;
import com.zrulin.service.impl.UserServerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author zrulin
 * @create 2022-03-30 8:38
 */
public class BeanFactory {


    private static InputStream inputStream;
    private static Properties properties;
    private static Map<String, Object> cache = new HashMap<>();
    static {
        try {
            inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("dao.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Object getDao(String beanName){
        if(!cache.containsKey(beanName)){
            synchronized (BeanFactory.class){
                if(!cache.containsKey(beanName)){
                    try {
                        String value = properties.getProperty(beanName);
                        Class clazz = Class.forName(value);
                        Object object = clazz.getConstructor(null).newInstance(null);
                        cache.put(beanName,object);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return cache.get(beanName);
    }
}
