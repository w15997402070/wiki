# 类加载器

类加载器ClassLoader就是加载其他类的类，它负责将字节码文件加载到内存，创建Class对象

1）启动类加载器（Bootstrap ClassLoader）：这个加载器是Java虚拟机实现的一部分，不是Java语言实现的，一般是C++实现的，它负责加载Java的基础类，主要是<JAVA_HOME>/lib/rt.jar，我们日常用的Java类库比如String、ArrayList等都位于该包内。

2）扩展类加载器（Extension ClassLoader）：这个加载器的实现类是sun.misc.Laun-cher$ExtClassLoader，它负责加载Java的一些扩展类，一般是<JAVA_HOME>/lib/ext目录中的jar包。	

3）应用程序类加载器（Application ClassLoader）：这个加载器的实现类是sun.misc.Launcher$AppClassLoader，它负责加载应用程序的类，包括自己写的和引入的第三方法类库，即所有在类路径中指定的类。

Application ClassLoader的父亲是Extension ClassLoader, Extension的父亲是Bootstrap ClassLoader

在子Class-Loader加载类时，一般会首先通过父ClassLoader加载，具体来说，在加载一个类时，基本过程是：1）判断是否已经加载过了，加载过了，直接返回Class对象，一个类只会被一个Class-Loader加载一次。2）如果没有被加载，先让父ClassLoader去加载，如果加载成功，返回得到的Class对象。3）在父ClassLoader没有加载成功的前提下，自己尝试加载类



ClassLoader的loadClass方法与Class的forName方法都可以加载类，它们有什么不同呢？基本是一样的，不过，ClassLoader的loadClass不会执行类的初始化代码

```java
public class ClassLoaderTest {

    public static void main(String[] args) {
        testLoadClass();
    }
    public static class Hello{
        static {
            System.out.println("hello");
        }
    }
    public static void testLoadClass(){
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        String name = ClassLoaderTest.class.getName()+"$Hello";
        try {
            //运行之后什么都没输出
//            Class<?> aClass = systemClassLoader.loadClass(name);

            //运行之后会打印出hello,说明执行了静态代码块
            Class<?> aClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

