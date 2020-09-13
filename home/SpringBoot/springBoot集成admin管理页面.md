# spring-boot-admin 管理

Spring Boot Admin是一个管理和监控Spring Boot应用程序的开源监控软件，针对spring-boot的actuator接口进行UI美化并封装，可以在管理界面中浏览所有被监控spring-boot项目的基本信息，详细的Health信息、内存信息、JVM信息、垃圾回收信息、各种配置信息（比如数据源、缓存列表和命中率）等，还可以直接修改logger的level，Spring Boot Admin提供的丰富详细的监控信息给Spring Boot应用的监控、维护和优化都带来了极大的便利

## 1. 创建服务端项目

创建一个maven项目,添加Spring Boot Admin依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server</artifactId>
    <version>2.2.1</version>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server-ui</artifactId>
    <version>2.2.1</version>
</dependency>
```

## 2. 添加配置

```yml
server:
  port: 8083
spring:
  application:
    name: monitor
```

## 3. 添加启动注解

在启动类中添加使用spring-boot-admin监控的`@EnableAdminServer`注解

```java
@SpringBootApplication
@EnableAdminServer //添加这个注解
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

}
```

## 4. 启动项目

访问`http://localhost:8083/`就可以看到监控页面

![image-20200101212312657](D:\data\notes\notes\springBoot\springBoot集成admin管理页面\image-20200101212312657.png)

## 5. 添加需要监控的项目

### 在需要被监控的项目中添加依赖

例如要监控 `admin`项目,就在`admin`项目的pom文件中添加

```xml
 <dependency>
     <groupId>de.codecentric</groupId>
     <artifactId>spring-boot-admin-starter-client</artifactId>
     <version>2.2.1</version>
</dependency>
```

### 添加配置

`url`为服务端也就是`monitor`项目的地址

```yml
spring:
  application:
    name: admin
  boot:
    admin:
      client:
        url: "http://localhost:8083" 
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

再启动`admin`项目就可以看到有一个项目在线了.可以点进去查看更多的信息

![image-20200101212929814](D:\data\notes\notes\springBoot\springBoot集成admin管理页面\image-20200101212929814.png)

