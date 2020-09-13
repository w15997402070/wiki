# SpringBoot 集成 ElasticSearch

pom文件中引入

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

`application.properties`配置

```properties
# elasticsearch设置
spring.data.elasticsearch.cluster-name=elasticsearch
spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
spring.data.elasticsearch.repositories.enabled=true
# 查询语句展示
logging.level.org.springframework.data.elasticsearch.core=DEBUG
```

