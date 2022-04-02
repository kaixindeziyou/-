# Simple-Spring-IOC

​	手写IOC框架，熟悉IOC源码



## IOC-1.0

做一个dao,service层的模拟代码编写。通过BeanFactory来构造bean。

相较于原来的在代码中使用关键字new来实例对象，放弃了创建对象的权限，将创建对象的方式交给了BeanFactory（**依赖反转**）

实现了从（**强依赖/紧耦合，编译之后无法修改，没有扩展性**）到（**弱依赖/松耦合，编译之后仍然可以修改，让程序具有更好的扩展性**）

```java
//dao层接口
public interface UserDao {
    public List<Integer> test();
}
//dao层具体实现类1
public class UserDaoImpl implements UserDao {

    @Override
    public List<Integer> test() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        return list;
    }
}
//dao层具体实现类2
public class UserDaoImpl1 implements UserDao {

    @Override
    public List<Integer> test() {
        return Arrays.asList(4,5,6);
    }
}
//service层接口
public interface UserService {
    public List<Integer> test();
}
//service实现类
public class UserServerImpl implements UserService {
    private UserDao userDao = (UserDao) BeanFactory.getDao("userDao");

    @Override
    public List<Integer> test() {
        List<Integer> test = userDao.test();

        return test;
    }
}
//模拟controller,这里注解输出在控制台了。没有使用servlet,部署toncat了.
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
```

通过读取配置文件获取实例的路径，在通过反射的方法获取对象实例。

cache保证实例的唯一性，在获取bean的时候加上synchronized锁来保证多线程下获得的bean唯一。

```java
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
```

配置文件dao.properties中的代码，通过修改配置文件，而不修改Java代码的方式来修改dao层实例的类型。

```properties
userDao=com.zrulin.dao.impl.UserDaoImpl1
```

体现了ioc的思想，但是只是简单的模拟，缺少很多功能，比如说写死了只能一个实例。

## IOC-2.0

