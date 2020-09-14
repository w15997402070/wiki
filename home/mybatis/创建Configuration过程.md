# Mybatis创建Configuration过程解析

#### 1. 通过`SqlSessionFactoryBuilder.build(Reader reader, String environment, Properties properties)`方法解析`mybatis-config.xml`

```java
String resource = "org/mybatis/example/mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory =
  new SqlSessionFactoryBuilder().build(inputStream);

```

SqlSessionFactoryBuilder 的builder()方法为 : 

```java
 public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
        //创建 XMLConfigBuilder
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
        //解析xml文件并且返回 SqlSessionFactory这里会返回默认的SqlSessionFactory实现,DefaultSqlSessionFactory
      return build(parser.parse());//重点在parser.parse()方法中
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
```

#### 2. XMLConfigBuilder.parse()方法解析xml文件

```java
public class XMLConfigBuilder extends BaseBuilder {
    
  private boolean parsed;
  private XPathParser parser;
  private String environment;
  private ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public Configuration parse() {
        //判断是否已解析
        if (parsed) {
          throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;
        //读取 configuration 节点
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
      }

	private void parseConfiguration(XNode root) {
        try {
            //解析settings节点
          Properties settings = settingsAsPropertiess(root.evalNode("settings"));
          //issue #117 read properties first
            //解析 properties 节点
          propertiesElement(root.evalNode("properties"));
          loadCustomVfs(settings);
            //解析typeAliases节点
          typeAliasesElement(root.evalNode("typeAliases"));
            //解析plugins
          pluginElement(root.evalNode("plugins"));
            //解析objectFactory
          objectFactoryElement(root.evalNode("objectFactory"));
            //解析objectWrapperFactory
          objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            //解析reflectionFactory
          reflectionFactoryElement(root.evalNode("reflectionFactory"));
          settingsElement(settings);
          // read it after objectFactory and objectWrapperFactory issue #631
            //解析environments
          environmentsElement(root.evalNode("environments"));
            //解析databaseIdProvider
          databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            //解析typeHandlers
          typeHandlerElement(root.evalNode("typeHandlers"));
            //解析mappers
          mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
          throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
  }
  //...
}
```

