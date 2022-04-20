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
//模拟controller,这里注解输出在控制台了。没有使用servlet,部署tomcat了.
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

手写代码的思路：

1. 自定义一个MyAnnotationConfigApplicationContext，构造器中传入要扫描的包。

   

2. 获取这个包中所有的类。

   ```java
   public class MyTools {
   
       public static Set<Class<?>> getClasses(String pack) {
   
           // 第一个class类的集合
           Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
           // 是否循环迭代
           boolean recursive = true;
           // 获取包的名字 并进行替换
           String packageName = pack;
           String packageDirName = packageName.replace('.', '/');
           // 定义一个枚举的集合 并进行循环来处理这个目录下的things
           Enumeration<URL> dirs;
           try {
               dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
               // 循环迭代下去
               while (dirs.hasMoreElements()) {
                   // 获取下一个元素
                   URL url = dirs.nextElement();
                   // 得到协议的名称
                   String protocol = url.getProtocol();
                   // 如果是以文件的形式保存在服务器上
                   if ("file".equals(protocol)) {
                       // 获取包的物理路径
                       String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                       // 以文件的方式扫描整个包下的文件 并添加到集合中
                       findClassesInPackageByFile(packageName, filePath, recursive, classes);
                   } else if ("jar".equals(protocol)) {
                       // 如果是jar包文件
                       // 定义一个JarFile
                       System.out.println("jar类型的扫描");
                       JarFile jar ;
                       try {
                           // 获取jar
                           jar = ((JarURLConnection) url.openConnection()).getJarFile();
                           // 从此jar包 得到一个枚举类
                           Enumeration<JarEntry> entries = jar.entries();
                           findClassesInPackageByJar(packageName, entries, packageDirName, recursive, classes);
                       } catch (IOException e) {
                           // log.error("在扫描用户定义视图时从jar包获取文件出错");
                           e.printStackTrace();
                       }
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           return classes;
       }
   
       private static void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries, String packageDirName, final boolean recursive, Set<Class<?>> classes) {
           // 同样的进行循环迭代
           while (entries.hasMoreElements()) {
               // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
               JarEntry entry = entries.nextElement();
               String name = entry.getName();
               // 如果是以/开头的
               if (name.charAt(0) == '/') {
                   // 获取后面的字符串
                   name = name.substring(1);
               }
               // 如果前半部分和定义的包名相同
               if (name.startsWith(packageDirName)) {
                   int idx = name.lastIndexOf('/');
                   // 如果以"/"结尾 是一个包
                   if (idx != -1) {
                       // 获取包名 把"/"替换成"."
                       packageName = name.substring(0, idx).replace('/', '.');
                   }
                   // 如果可以迭代下去 并且是一个包
                   if ((idx != -1) || recursive) {
                       // 如果是一个.class文件 而且不是目录
                       if (name.endsWith(".class") && !entry.isDirectory()) {
                           // 去掉后面的".class" 获取真正的类名
                           String className = name.substring(packageName.length() + 1, name.length() - 6);
                           try {
                               // 添加到classes
                               classes.add(Class.forName(packageName + '.' + className));
                           } catch (ClassNotFoundException e) {
                               // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                               e.printStackTrace();
                           }
                       }
                   }
               }
           }
       }
   
       private static void findClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
           // 获取此包的目录 建立一个File
           File dir = new File(packagePath);
           // 如果不存在或者 也不是目录就直接返回
           if (!dir.exists() || !dir.isDirectory()) {
               // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
               return;
           }
           // 如果存在 就获取包下的所有文件 包括目录
           File[] dirfiles = dir.listFiles(new FileFilter() {
               // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
               @Override
               public boolean accept(File file) {
                   return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
               }
           });
           // 循环所有文件
           for (File file : dirfiles) {
               // 如果是目录 则继续扫描
               if (file.isDirectory()) {
                   findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
               } else {
                   // 如果是java类文件 去掉后面的.class 只留下类名
                   String className = file.getName().substring(0, file.getName().length() - 6);
                   try {
                       // 添加到集合中去
                       // classes.add(Class.forName(packageName + '.' +
                       // className));
                       // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                       classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                   } catch (ClassNotFoundException e) {
                       // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                       e.printStackTrace();
                   }
               }
           }
       }
   
   }
   ```

3. 遍历这些类，找出添加了@Component注解的类，获取它的Class和对应的beanName，封装成一个BeanDefinition，存入Set，这个集合是IOC自动装载的原材料。

   ```java
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
   ```

4. 遍历Set集合，通过反射机制创建对象，同时检测属性有没有添加@Value等注解，如果有还需要给属性赋值，再将这些动态创建的对象以k-v的形式存入缓存区。

   ```java
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
   ```

5. 提供getBean等方法，通过beanName取出对应的bean即可。

   ```java
   public Object getBean(String beanName){
       return ioc.get(beanName);
   }
   ```

6. 提供自动装载的方法，将类中的引用数据类型的成员变量自动装载。

   
