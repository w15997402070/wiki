# Mybatis技术内幕

[toc]

Mybatis基础架构 : 

![基础架构](D:\data\notes\notes\mybatis\Mybatis架构.assets\1568873394138.png)

## 解析器模块

常见的三种XML解析方式 : 

* DOM(Document Object Model) 解析方式
* SAX(Simple API for XML0) 解析方式
* StAX(Streaming API for XML) 解析方式 (JDK6.0开始支持)

Mybatis提供的 `org.apache.ibatis.parsing.XPathParser`

```Java
public class XPathParser {
  
  private Document document;  //Document对象
  private boolean validation; //是否开启验证
  private EntityResolver entityResolver;//用于加载本地DTD文件
  private Properties variables;//mybatis-config.xml 中<properties>标签定义的键值对集合
  private XPath xpath;//XPath对象
   
   public XPathParser(String xml) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }
    
    private Document createDocument(InputSource inputSource) {
    // important: this must only be called AFTER common constructor
    try {
        //创建 DocumentBuilderFactory对象
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //对对象进行配置
      factory.setValidating(validation);
      factory.setNamespaceAware(false);
      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(false);
      factory.setCoalescing(false);
      factory.setExpandEntityReferences(true);
       //创建 DocumentBuilder对象并进行配置
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setEntityResolver(entityResolver);
      builder.setErrorHandler(new ErrorHandler() {
        @Override
        public void error(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }
      });
        //加载 xml文件
      return builder.parse(inputSource);
    } catch (Exception e) {
      throw new BuilderException("Error creating document instance.  Cause: " + e, e);
    }
  }
 
    private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
    this.validation = validation;
    this.entityResolver = entityResolver;
    this.variables = variables;
    XPathFactory factory = XPathFactory.newInstance();
    this.xpath = factory.newXPath();
  }
}
```

XPathParser解析xml步骤 :

* 先调用 `commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver)` 完成初始化

* 再调用 `createDocument(InputSource inputSource)` 创建 document	

XPathParser 提供了一系列的eval*()方法 解析boolean,short,long,int,String,Node等类型,通过调用XPath.evaluate()方法查找指定的路径的节点或属性,并进行相应的类型转换.

注意 `XPathParser.evalString(Object root, String expression)`会调用 `PropertyParser.parse(result, variables)`方法处理节点中相应的默认值.

```Java
public String evalString(Object root, String expression) {
    String result = (String) evaluate(expression, root, XPathConstants.STRING);
    result = PropertyParser.parse(result, variables);
    return result;
  }
```

Mybatis提供的 EntityResolver 实现 `org.apache.ibatis.builder.xml.XMLMapperEntityResolver`



## 反射工具箱

Mybatis 中 `org.apache.ibatis.reflection.Reflector` 缓存了反射需要使用的类的元信息.

```Java
public class Reflector {

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private Class<?> type; //对应的 Class 类型
    //可读属性的名称集合,可读属性就是存在相应 getter 方法的属性,初始值为空数组
  private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
    //可写属性的集合,可写属性就是存在相应 setter 方法的属性,初始值为空数组
  private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
    //记录了属性对应的 setter 方法,key是属性名称,value 是 Invoker对象,它是 setter 方法对应Method 对象的封装
  private Map<String, Invoker> setMethods = new HashMap<String, Invoker>();
    //属性相应的 getter 的集合, key是属性名称,value 是 Invoker对象
  private Map<String, Invoker> getMethods = new HashMap<String, Invoker>();
    //记录了属性相应的 setter 方法的参数值类型,key 是属性名称,value 是 setter 方法的参数类型
  private Map<String, Class<?>> setTypes = new HashMap<String, Class<?>>();
    //记录属性相应的 getter 的返回值类型 key是属性名称,value 是 getter 方法的返回值类型
  private Map<String, Class<?>> getTypes = new HashMap<String, Class<?>>();
    //记录默认的构造方法
  private Constructor<?> defaultConstructor;
    //记录所有属性名称的集合
  private Map<String, String> caseInsensitivePropertyMap = new HashMap<String, String>();
    
    //构造函数
  public Reflector(Class<?> clazz) {
    type = clazz;
    addDefaultConstructor(clazz);
    addGetMethods(clazz);
    addSetMethods(clazz);
    addFields(clazz);
    readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
    writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
    for (String propName : readablePropertyNames) {
      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
    }
    for (String propName : writeablePropertyNames) {
      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
    }
  }
}
```

Mybatis中 `org.apache.ibatis.reflection.ReflectorFactory`主要实现了对 Reflector 对象的创建和缓存

```Java
public interface ReflectorFactory {
  //检测 ReflectorFactory 对象是否会缓存 Reflector 对象
  boolean isClassCacheEnabled();
  //设置是否缓存 Reflector 对象
  void setClassCacheEnabled(boolean classCacheEnabled);
 //创建指定 Class 对应的 Reflector 对象
  Reflector findForClass(Class<?> type);
}
```

Mybatis 提供了`ReflectorFactory` 的实现类 `org.apache.ibatis.reflection.DefaultReflectorFactory`