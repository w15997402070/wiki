# Binder用法

Binder获取配置信息

```java
@Data
@Component
@ConfigurationProperties(prefix = "com.binder")
public class BinderTest {

    private int id;
    private String name;
    private List<String> phones;
}
```

配置文件中添加配置

```properties
com.binder.id=1
com.binder.name=test
# 下标不能跳号
com.binder.phones[0]=test1
com.binder.phones[1]=test2
```

测试

```java
public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SearchApplication.class, args);
        Binder binder = Binder.get(applicationContext.getEnvironment());
        BinderTest binderTest = binder.bind("com.binder", Bindable.of(BinderTest.class)).get();
        log.info(binderTest.toString());
    
    }
```

输出

```java
BinderTest(id=1, name=test, phones=[test1, test2])
```

