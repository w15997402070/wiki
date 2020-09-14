### 1.在pom文件添加插件
```java
    <build>
        <finalName>zsxt</finalName>
        <plugins>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
### 2.配置maven启动

   *D:\gitRepository\git\烂笔头\mybatis-generator\generotor.jpg*

     mybatis-generator:generate -e
### 3.创建配置文件
     generatorConfig.xml
### 4.运行
    D:\gitRepository\git\烂笔头\mybatis-generator\run.jpg
