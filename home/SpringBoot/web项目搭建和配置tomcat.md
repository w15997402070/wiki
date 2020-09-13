# spring-boot web项目搭建

## 1.启动类最好放在根目录中和controller平级

例如 :  
`DemoApplication` 在 `com.example.demo`包下时,controller最好也在`com.example.demo`包下.  
  因为 `@SpringBootApplication(scanBasePackages = "net.laoyeye.yyblog")` 如果不设置 `scanBasePackages`注解,springBoot默认扫描同级的controller包,如果controller包不在同级目录,假如在`com.example`包下那controller就全部扫描不到,也就访问不能成功.这时就需要配置扫描包的路径.  
*所以在创建项目时最好启动类放到包的根目录中并且配置扫描包的路径*

## 2. idea使用启动类启动和配置tomcat启动

1. 使用启动启动

```java
@SpringBootApplication
@ComponentScan(value = "com.example")
public class DemoApplication{
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

2. 配置tomcat启动

需要继承 `SpringBootServletInitializer` 并且覆盖 `configure`方法.

```java
@SpringBootApplication
@ComponentScan(value = "com.example")
public class DemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }
}
```
