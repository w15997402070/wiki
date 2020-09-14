# Mybatis的Configuration配置

[toc]



使用mybatis的程序的基础模块 ： 

![1568609008578](D:\data\notes\notes\mybatis\Untitled.assets\1568609008578.png)

mybatis主要的源码包 ： 

![源码包](D:\data\notes\notes\mybatis\Untitled.assets\1568609200442.png)

* annotations : 注释包，其中有Mapper 的配置注释定义

* binding : 注释实现包，其中有Mapper注释实现代码

* builder ： 构建器代码

* cache : Mybatis的缓存实现代码

* cursor ： 结果集Cursor的接口代码

* datasource ：数据源及数据工厂代码

* exceptions ：异常定义的代码

* io : 反射工具类代码

* jdbc ：测试代码

* lang  : Java版本的注释代码

* logging : 日志功能代码

* mapping : SQL Mapping 代码

* parsing : XML解析工具

* plugin : 扩展点代码

* reflection : 类元数据,反射功能代码

* scripting : 动态sql实现代码

* session : 执行器代码

* transaction : 事务支持代码

* type : 类型处理器代码

## Mybatis 的基本构成

  * SqlSessionFactoryBuilder(构造器) : 它会根据配置信息或者代码来生成SqlSessionFactory(工厂接口)
  * SqlSessionFactory : 依靠工厂来生成SqlSession(会话)
  * SqlSession : 是一个即可以发送SQL去执行并返回结果,也可以获取Mapper 的接口
  * SQL Mapper：它是MyBaits新设计的组件，它是由一个Java接口和XML文件（或注解）构成的，需要给出对应的SQL和映射规则。它负责发送SQL去执行，并返回结果。

## Mybatis配置xml文件的层次结构

![configuration](D:\data\notes\notes\mybatis\Mybatis架构.assets\1568625873101.png)  

```xml
<configuration>
    
    <properties resource="org/mybatis/example/config.properties">
      <property name="username" value="dev_user"/>
      <property name="password" value="F2Fa3!33TYyg"/>
    </properties>
    
    <settings>
      <setting name="cacheEnabled" value="true"/>
      <setting name="lazyLoadingEnabled" value="true"/>
      <setting name="multipleResultSetsEnabled" value="true"/>
      <setting name="useColumnLabel" value="true"/>
      <setting name="useGeneratedKeys" value="false"/>
      <setting name="autoMappingBehavior" value="PARTIAL"/>
      <setting name="defaultExecutorType" value="SIMPLE"/>
      <setting name="defaultStatementTimeout" value="25"/>
      <setting name="safeRowBoundsEnabled" value="false"/>
      <setting name="mapUnderscoreToCamelCase" value="false"/>
      <setting name="localCacheScope" value="SESSION"/>
      <setting name="jdbcTypeForNull" value="OTHER"/>
      <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    </settings>
    
    <typeAliases>
      <typeAlias alias="Author" type="domain.blog.Author"/>
      <typeAlias alias="Blog" type="domain.blog.Blog"/>
      <typeAlias alias="Comment" type="domain.blog.Comment"/>
      <typeAlias alias="Post" type="domain.blog.Post"/>
      <typeAlias alias="Section" type="domain.blog.Section"/>
      <typeAlias alias="Tag" type="domain.blog.Tag"/>
   </typeAliases>
    
    <typeHandlers>
      <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
    </typeHandlers>
    
    <plugins>
       <plugin interceptor="org.mybatis.example.ExamplePlugin">
          <property name="someProperty" value="100"/>
       </plugin>
    </plugins>
    
    <objectFactory type="org.mybatis.example.ExampleObjectFactory">
      <property name="someProperty" value="100"/>
    </objectFactory>
    
    <environments default="development">
      <environment id="development">
        <transactionManager type="JDBC">
          <property name="..." value="..."/>
        </transactionManager>
        <dataSource type="POOLED">
          <property name="driver" value="${driver}"/>
          <property name="url" value="${url}"/>
          <property name="username" value="${username}"/>
          <property name="password" value="${password}"/>
        </dataSource>
      </environment>
	</environments>
    
    <databaseIdProvider type="DB_VENDOR">
      <property name="SQL Server" value="sqlserver"/>
      <property name="DB2" value="db2"/>        
      <property name="Oracle" value="oracle" />
    </databaseIdProvider>
    
    <mappers>
        <mapper resource="org/apache/ibatis/builder/AuthorMapper.xml"/>
        <mapper resource="org/apache/ibatis/builder/BlogMapper.xml"/>
        <mapper resource="org/apache/ibatis/builder/CachedAuthorMapper.xml"/>
        <mapper resource="org/apache/ibatis/builder/PostMapper.xml"/>
        <mapper resource="org/apache/ibatis/builder/NestedBlogMapper.xml"/>
    </mappers>
</configuration>
```

### properties元素

* property子元素

  ```xml
  <properties>
      <property name="driver"value="com.mysql.jdbc.Driver"/>
      <property name="ur1"value="jdbc:mysql://1ocalhost:3306/mybatis"/>
      <property name="username"value="root"/>
      <property name="password"value="learn"/>
  </properties>
  ```

  这样就能在上下文中使用已经配置好的属性

  ```xml
  <dataSource type="UNPOOLED">
          <property name="driver" value="${driver}"/>
          <property name="url" value="${url}"/>
          <property name="username" value="${username}"/>
          <property name="password"value="${password}"/>
  </dataSource>
  ```

* properties配置文件

  ```properties
  driver=org.apache.derby.jdbc.EmbeddedDriver
  url=jdbc:derby:ibderby;create=true
  username=
  password=
  ```

  ```xml
  <configuration>
    <properties resource="org/apache/ibatis/databases/blog/blog-derby.properties"/>
    <environments default="development">
      <environment id="development">
        <transactionManager type="JDBC">
          <property name="" value=""/>
        </transactionManager>
        <dataSource type="UNPOOLED">
          <property name="driver" value="${driver}"/>
          <property name="url" value="${url}"/>
          <property name="username" value="${username}"/>
          <property name="password" value="${password}"/>
        </dataSource>
      </environment>
    </environments>
  </configuration>
  ```
  
* 代码配置

  ```java
  Properties props = new Properties(); 
     props.setProperty("username", userName);
     props.setProperty("password", pwd);
  SqlSessionFactory factory = sqlSessionFactoryBuilder.build(reader, props);
  ```

* XMLConfigBuilder的propertiesElement()解析方法: 

  ```java
   private void propertiesElement(XNode context) throws Exception {
      if (context != null) {
          //解析<property>属性的name和value值
        Properties defaults = context.getChildrenAsProperties();
        String resource = context.getStringAttribute("resource");
        String url = context.getStringAttribute("url");
        if (resource != null && url != null) {
          throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
        }
        if (resource != null) {
          defaults.putAll(Resources.getResourceAsProperties(resource));
        } else if (url != null) {
          defaults.putAll(Resources.getUrlAsProperties(url));
        }
          //这里加载代码设置的属性
        Properties vars = configuration.getVariables();
        if (vars != null) {
          defaults.putAll(vars);
        }
        parser.setVariables(defaults);
        configuration.setVariables(defaults);
      }
    }
  ```

* properties加载顺序
  * 在 properties 元素体内指定的属性首先被读取。
  * 然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性。
  * 最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。

### settings元素

settings 节点下的配置是全局性的配置,他们会改变mybatis的运行时行为,在初始化时这些全局配置信息会被记录到Configuration对象中

``` xml
<settings>
      <setting name="cacheEnabled" value="true"/>
      <setting name="lazyLoadingEnabled" value="true"/>
      <setting name="multipleResultSetsEnabled" value="true"/>
      <setting name="useColumnLabel" value="true"/>
      <setting name="useGeneratedKeys" value="false"/>
      <setting name="autoMappingBehavior" value="PARTIAL"/>
      <setting name="defaultExecutorType" value="SIMPLE"/>
      <setting name="defaultStatementTimeout" value="25"/>
      <setting name="safeRowBoundsEnabled" value="false"/>
      <setting name="mapUnderscoreToCamelCase" value="false"/>
      <setting name="localCacheScope" value="SESSION"/>
      <setting name="jdbcTypeForNull" value="OTHER"/>
      <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
</settings>
```

* XMLConfigBuilder.settingsAsPropertiess()的解析方法

  ```java
  private Properties settingsAsPropertiess(XNode context) {
      if (context == null) {
        return new Properties();
      }
      Properties props = context.getChildrenAsProperties();
      // Check that all settings are known to the configuration class
      MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
      //检测Configuration中是否定义了 key 指定的相应的setter方法
      for (Object key : props.keySet()) {
        if (!metaConfig.hasSetter(String.valueOf(key))) {
          throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
        }
      }
      return props;
    }
  ```

### typeAliases元素

```xml
<typeAliases>
  <typeAlias alias="Author" type="domain.blog.Author"/>
  <typeAlias alias="Blog" type="domain.blog.Blog"/>
  <typeAlias alias="Comment" type="domain.blog.Comment"/>
  <typeAlias alias="Post" type="domain.blog.Post"/>
  <typeAlias alias="Section" type="domain.blog.Section"/>
  <typeAlias alias="Tag" type="domain.blog.Tag"/>
</typeAliases>

<!--也可以指定包名-->
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```

每一个在包 `domain.blog` 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。 比如 `domain.blog.Author` 的别名为 `author`；若有注解，则别名为其注解值。看下面的例子：

```java
    @Alias("author")
    public class Author {
        ...
    }
```

* XMLConfigBuilder.typeAliasesElement()的解析方法

```java
private void typeAliasesElement(XNode parent) {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
          //解析package标签
        if ("package".equals(child.getName())) {
          String typeAliasPackage = child.getStringAttribute("name");
            //TypeAliasRegistry.registerAliases(String packageName, Class<?> superType)
          configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
        } else {
          String alias = child.getStringAttribute("alias");
          String type = child.getStringAttribute("type");
          try {
            Class<?> clazz = Resources.classForName(type);
            if (alias == null) {
              typeAliasRegistry.registerAlias(clazz);
            } else {
              typeAliasRegistry.registerAlias(alias, clazz);
            }
          } catch (ClassNotFoundException e) {
            throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
          }
        }
      }
    }
  }
```

### typeHandlers元素

```xml
<typeHandlers>
  <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
</typeHandlers>
<!--也可以使用-->
<typeHandlers>
  <package name="org.mybatis.example"/>
</typeHandlers>
```

* XMLConfigBuilder.typeHandlerElement()的解析方法

```java
private void typeHandlerElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        if ("package".equals(child.getName())) {
          String typeHandlerPackage = child.getStringAttribute("name");
          typeHandlerRegistry.register(typeHandlerPackage);
        } else {
          String javaTypeName = child.getStringAttribute("javaType");
          String jdbcTypeName = child.getStringAttribute("jdbcType");
          String handlerTypeName = child.getStringAttribute("handler");
          Class<?> javaTypeClass = resolveClass(javaTypeName);
          JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
          Class<?> typeHandlerClass = resolveClass(handlerTypeName);
          if (javaTypeClass != null) {
            if (jdbcType == null) {
              typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
            } else {
              typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
            }
          } else {
            typeHandlerRegistry.register(typeHandlerClass);
          }
        }
      }
    }
  }
```

### plugins元素

```xml
<plugins>
  <plugin interceptor="org.mybatis.example.ExamplePlugin">
    <property name="someProperty" value="100"/>
  </plugin>
</plugins>
```

* XMLConfigBuilder.pluginElement()的解析方法

```java 
private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        String interceptor = child.getStringAttribute("interceptor");
        Properties properties = child.getChildrenAsProperties();
        Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
        interceptorInstance.setProperties(properties);
        configuration.addInterceptor(interceptorInstance);
      }
    }
  }
```

### objectFactory元素

mybaits 可以通过添加自定义的 Objectory实现类ObjectWrapperFactory以及ReflectorFactory对Mybatis进行扩展

```xml
<objectFactory type="org.mybatis.example.ExampleObjectFactory">
  <property name="someProperty" value="100"/>
</objectFactory>
```

```java
  private void objectFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties properties = context.getChildrenAsProperties();
      ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
      factory.setProperties(properties);
      configuration.setObjectFactory(factory);
    }
  }
```

### objectWrapperFactory元素

解析代码:

```java
private void objectWrapperFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
      configuration.setObjectWrapperFactory(factory);
    }
  }
```

### reflectionFactory元素

解析代码：

```java
private void reflectionFactoryElement(XNode context) throws Exception {
    if (context != null) {
       String type = context.getStringAttribute("type");
       ReflectorFactory factory = (ReflectorFactory) resolveClass(type).newInstance();
       configuration.setReflectorFactory(factory);
    }
  }
```

### environments元素

```xml
<environments default="development"><!--默认使用的环境的id-->
  <environment id="development"><!--每个environment环境的id-->
    <transactionManager type="JDBC"><!--事务管理器的配置-->
      <property name="..." value="..."/>
    </transactionManager>
    <dataSource type="POOLED"><!--数据源的配置-->
      <property name="driver" value="${driver}"/>
      <property name="url" value="${url}"/>
      <property name="username" value="${username}"/>
      <property name="password" value="${password}"/>
    </dataSource>
  </environment>
</environments>
```

解析代码 :

```java
private void environmentsElement(XNode context) throws Exception {
    if (context != null) {
      if (environment == null) {
        environment = context.getStringAttribute("default");
      }
      for (XNode child : context.getChildren()) {
        String id = child.getStringAttribute("id");
        if (isSpecifiedEnvironment(id)) {
          TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
          DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
          DataSource dataSource = dsFactory.getDataSource();
          Environment.Builder environmentBuilder = new Environment.Builder(id)
              .transactionFactory(txFactory)
              .dataSource(dataSource);
          configuration.setEnvironment(environmentBuilder.build());
        }
      }
    }
  }
```

### databaseIdProvider元素

```xml
<databaseIdProvider type="DB_VENDOR">
  <property name="SQL Server" value="sqlserver"/>
  <property name="DB2" value="db2"/>        
  <property name="Oracle" value="oracle" />
</databaseIdProvider>
```

解析代码:

```java 
 private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
      String type = context.getStringAttribute("type");
      // awful patch to keep backward compatibility
      if ("VENDOR".equals(type)) {
          type = "DB_VENDOR";
      }
      Properties properties = context.getChildrenAsProperties();
      databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
      databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
      String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
      configuration.setDatabaseId(databaseId);
    }
  }
```

### mappers节点

使用方式 : 

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```

```xml
<!-- 使用完全限定资源定位符（URL） -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
```

```xml
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
```

```xml
<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

解析代码:

```java
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        if ("package".equals(child.getName())) {
          String mapperPackage = child.getStringAttribute("name");
          configuration.addMappers(mapperPackage);
        } else {
          String resource = child.getStringAttribute("resource");
          String url = child.getStringAttribute("url");
          String mapperClass = child.getStringAttribute("class");
          if (resource != null && url == null && mapperClass == null) {
            ErrorContext.instance().resource(resource);
            InputStream inputStream = Resources.getResourceAsStream(resource);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url != null && mapperClass == null) {
            ErrorContext.instance().resource(url);
            InputStream inputStream = Resources.getUrlAsStream(url);
              //XMLMapperBuilder解析mapper文件
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url == null && mapperClass != null) {
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            configuration.addMapper(mapperInterface);
          } else {
            throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
          }
        }
      }
    }
  }
```

