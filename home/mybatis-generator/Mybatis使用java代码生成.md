# Mybatis使用java代码生成

## 1.使用maven引入包

```xml
<dependency>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-core</artifactId>
    <version>1.3.7</version>
</dependency>
```

## 2.将 `generatorConfig.xml`放入`resource`目录中

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <!-- mysql jar包 存放位置路径 -->
    <classPathEntry location="mysql\mysql-connector-java-5.1.46.jar" />

    <context id="MysqlTables" targetRuntime="MyBatis3">
        <commentGenerator type="org.mybatis.generator.internal.DefaultCommentGenerator">
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <!--*阻止**生成注释，默认为false-->
            <property name="suppressAllComments" value="true"/>
            <!-- 阻止**生成的注释包含时间戳，默认为false-->
            <property name="suppressDate" value="true"/>
        </commentGenerator>
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/test?useSSL=false&amp;nullNamePatternMatchesAll=true"
                        userId="root"
                        password="123456">
        </jdbcConnection>

        <javaTypeResolver >
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>

        <javaModelGenerator targetPackage="com.test.model" targetProject="\src\main\java">
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="false" />
        </javaModelGenerator>

        <sqlMapGenerator targetPackage="com.test.xml"  targetProject="\src\main\java">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>

        <javaClientGenerator  targetPackage="com.test.dao" type="XMLMAPPER"  targetProject="\src\main\java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>
         
        <table schema="Mysql" tableName="test" domainObjectName="Test"
               enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false"
               enableSelectByExample="false" selectByExampleQueryId="false">
        </table>
    </context>
</generatorConfiguration>
```

## 3.生成的java代码

```java
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GeneratorCodeUtil {
    /**
     * 生成代码的路径名
     */
    private static String TARGET_OBJECT_PATH = "D:\\src\\main\\java";
    private static String TABLE_NAME="test";

    public static void generatorCode() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile =  ResourceUtils.getFile("classpath:generatorConfig.xml");
       // File configFile = new File("generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = null;
        config = cp.parseConfiguration(configFile);
        //这里可以重新覆盖xml文件里的配置,这一段代码可以不要 --- start
        List<Context> contexts = config.getContexts();
        for (Context context : contexts) {
            //java Modal 这里可以重新配置生成代码的路径
            JavaModelGeneratorConfiguration javaModelConfiguration = context.getJavaModelGeneratorConfiguration();
            javaModelConfiguration.setTargetProject(TARGET_OBJECT_PATH);
            //xml路径
            SqlMapGeneratorConfiguration sqlMapConfiguration = context.getSqlMapGeneratorConfiguration();
            sqlMapConfiguration.setTargetProject(TARGET_OBJECT_PATH);
            //mapper文件配置
            JavaClientGeneratorConfiguration javaClientConfiguration = context.getJavaClientGeneratorConfiguration();
            javaClientConfiguration.setTargetProject(TARGET_OBJECT_PATH);
            //表生成配置
//            List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
//            for (TableConfiguration tableConfiguration : tableConfigurations) {
//                tableConfiguration.setTableName(TABLE_NAME);
//            }
        }
        // ---- end -----
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    public static void main (String [] args){
        try {
            generatorCode();
        } catch (IOException | SQLException | InterruptedException | XMLParserException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}

```

