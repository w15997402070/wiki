# Java计算对象大小

[toc]

## 使用BTraceUtils的sizeof方法计算java对象大小

使用Java VisualVM中的插件BTrace

打开Java VisualVM,安装BTrace插件

![image-20200824215919623](D:\data\notes\notes\java\java获取对象大小\image-20200824215919623.png)

启动tomcat项目

编写类和方法

```java
@Data
public class TestClassSize {

    private String id;

    private String name;

    private long address;
}


@RestController
@RequestMapping("/test")
public class TestSize {
   @RequestMapping("/getTestSize")
    public TestClassSize getTestSize(){
        TestClassSize note = new TestClassSize();
        return note;
    }
}

```

在启动的tomcat上右键

![image-20200824220239257](D:\data\notes\notes\java\java获取对象大小\image-20200824220239257.png)

编写代码

```java
/* BTrace Script Template */
import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

@BTrace
public class TracingScript {
	/* put your code here */
@OnMethod(
            clazz="com.wang.notepad.controller.TestSize",
            method="getTestSize", location = @Location(Kind.RETURN)
    )
    public static void nonew(@Return Object obj) {
        
        println(Strings.concat("size :", Strings.str(sizeof(obj))));
    }
}
```

点击start

![image-20200824220906486](D:\data\notes\notes\java\java获取对象大小\image-20200824220906486.png)

然后访问 `com.wang.notepad.controller.TestSize` 的 `getTestSize`方法,就会显示如上图所示的对象大小

![image-20200824220744331](D:\data\notes\notes\java\java获取对象大小\image-20200824220744331.png)

参考 : https://www.cnblogs.com/bobsha/p/6896552.html

## java对象大小和HashMap对象大小计算

https://blog.csdn.net/belongtocode/article/details/103377187