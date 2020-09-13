# springBoot 配置druid数据库连接池

## 1.添加依赖

```xml
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.17</version>
    </dependency>
```

## 2.添加数据库配置

```properties
spring.datasource.druid.url= # 或spring.datasource.url=
spring.datasource.druid.username= # 或spring.datasource.username=
spring.datasource.druid.password= # 或spring.datasource.password=
spring.datasource.druid.driver-class-name= #或 spring.datasource.driver-class-name=
```

## 3.连接池配置

```properties
spring.datasource.druid.initial-size=
spring.datasource.druid.max-active=
spring.datasource.druid.min-idle=
spring.datasource.druid.max-wait=
spring.datasource.druid.pool-prepared-statements=
spring.datasource.druid.max-pool-prepared-statement-per-connection-size= 
spring.datasource.druid.max-open-prepared-statements= #和上面的等价
spring.datasource.druid.validation-query=
spring.datasource.druid.validation-query-timeout=
spring.datasource.druid.test-on-borrow=
spring.datasource.druid.test-on-return=
spring.datasource.druid.test-while-idle=
spring.datasource.druid.time-between-eviction-runs-millis=
spring.datasource.druid.min-evictable-idle-time-millis=
spring.datasource.druid.max-evictable-idle-time-millis=
spring.datasource.druid.filters= #配置多个英文逗号分隔
```

## 创建数据源

```java
@Configuration
public class DataSourceConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid") // application.properteis中对应属性的前缀
    public DataSource dataSource1() {
        //注意这里
        return DruidDataSourceBuilder.create().build();
    }
    @Bean
    @Primary
    public PlatformTransactionManager prodTransactionManager(@Qualifier("dataSource") DataSource prodDataSource) {
        logger.info("dataSource : {}",prodDataSource);
        return new DataSourceTransactionManager(prodDataSource);
    }
}
```

[参考文档](https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter)
