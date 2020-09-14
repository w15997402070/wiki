# 类解析

## SingletonList

Collections 类的静态内部类,返回一个只包含一个元素的List

```java
@Slf4j
public class SingletonListDemo {

    public static void main(String[] args) {
        //这个List只有一个元素
        List<String> singletonList = Collections.singletonList("SingletonList");
        log.info("结果 : {}",singletonList);
    }
}
```

