package com.zrulin.myspring;

import com.zrulin.myspring.annotation.Autowired;
import com.zrulin.myspring.annotation.Component;
import com.zrulin.myspring.annotation.Qualifier;
import com.zrulin.myspring.annotation.Value;
import com.zrulin.util.MyTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zrulin
 * @create 2022-04-20 15:14
 */
public class MyAnnotationConfigApplicationContext {

    //装载对象的容器
    Map<String,Object> ioc = new HashMap<>();

    public MyAnnotationConfigApplicationContext(){}
    public MyAnnotationConfigApplicationContext(String pack){
        //遍历包，找到所有目标类。
        Set<BeanDefinition> beanDefinitions = findBeanDefinitions(pack);
        //根据原材料创建bean
        createObject(beanDefinitions);
        //自动装载
        autowireObject(beanDefinitions);
    }

    public Object getBean(String beanName){
        return ioc.get(beanName);
    }

    //寻找@Autowire给引用类型变量赋值
    public void autowireObject(Set<BeanDefinition> beanDefinitions){
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class clazz = beanDefinition.getBeanClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if(autowired != null){
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    if(qualifier != null){
                        //byName
                        try {
                            String beanName = qualifier.value();
                            Object bean = getBean(beanName);
                            String fieldName = field.getName();
                            String methodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                            Method method = clazz.getDeclaredMethod(methodName, field.getType());
                            Object object = getBean(beanDefinition.getBeanName());
                            method.invoke(object,bean);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //byType
                    }
                }
            }
        }
    }

    //寻找@Value，给属性赋值
    public void createObject(Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class clazz = beanDefinition.getBeanClass();
            String beanName = beanDefinition.getBeanName();
            try {
                //通过反射利用空参构造器创建对象
                Object obj = clazz.newInstance();
                //完成属性的赋值
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    Value valueAnnotation = field.getAnnotation(Value.class);
                    if(valueAnnotation != null){
                        String value = valueAnnotation.value();
                        String fieldName = field.getName();
                        String methodName  = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                        Method setMethod = clazz.getDeclaredMethod(methodName, field.getType());
                        //完成数据类型转换
                        Object var = null;
                        switch (field.getType().getName()){
                            case"java.lang.Double": var = Double.parseDouble(value);
                                break;
                            case"java.lang.Integer":var = Integer.parseInt(value);
                                break;
                            case"java.lang.String": var = value;
                                break;
                            case"java.lang.Float": var = Float.parseFloat(value);
                                break;
                            case"java.lang.Long": var = Long.parseLong(value);
                                break;
                        }
                        setMethod.invoke(obj,var);
                    }
                }
                //将创建好的对象装载到IOC容器
                ioc.put(beanName,obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //原材料
    public Set<BeanDefinition> findBeanDefinitions(String pack){
        //1、获取包下的所有类
        //2、遍历这些类找到添加了注解的类
        //3、将这些类封装成BeanDefinition，装载到集合中。
        Set<Class<?>> classes = MyTools.getClasses(pack);//返回一个集合，这个就和就是当前包下的所有类。
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for (Class<?> clazz : classes) {
            //返回一个annotatoin注解的对象。
            Component component = clazz.getAnnotation(Component.class);
            if(component != null){
                //获取Component注解的值
                String beanName = component.value();
                if("".equals(beanName)){
                    String className = clazz.getName().replace(clazz.getPackage().getName() + ".", "");
                    beanName = className.substring(0,1).toLowerCase() + className.substring(1);
                }
                //将这些类封装成BeanDefinition，装载到集合中。
                beanDefinitions.add(new BeanDefinition(beanName,clazz));
            }
        }
        return beanDefinitions;
    }
}
