# Mybatis常用工具类

[toc]

## MetaObject

使用MetaObject工具类可以获取和设置对象的属性值

三个常用方法 : 

```
1.MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) 用于包装对象 SystemMetaObject.forObject(object) 代替了这个方法
2. Object getValue(String name) 获取对象属性值,支持OGNL
3. setValue(String name, Object value) 修改对象属性值
```

参见mybatis源码test中的MetaObjectTest

```java
  @Test
  public void shouldGetAndSetField() {
    RichType rich = new RichType();
    MetaObject meta = SystemMetaObject.forObject(rich);
    meta.setValue("richField", "foo");
    assertEquals("foo", meta.getValue("richField"));
  }

public class RichType {

  private RichType richType;

  private String richField;

  private String richProperty;

  private Map richMap = new HashMap();

  private List richList = new ArrayList() {
    {
      add("bar");
    }
  };

  public RichType getRichType() {
    return richType;
  }

  public void setRichType(RichType richType) {
    this.richType = richType;
  }

  public String getRichProperty() {
    return richProperty;
  }

  public void setRichProperty(String richProperty) {
    this.richProperty = richProperty;
  }

  public List getRichList() {
    return richList;
  }

  public void setRichList(List richList) {
    this.richList = richList;
  }

  public Map getRichMap() {
    return richMap;
  }

  public void setRichMap(Map richMap) {
    this.richMap = richMap;
  }
}
```

## MetaClass

MetaClass是MyBatis中的反射工具类，与MetaOjbect不同的是，MetaObject用于获取和设置对象的属性值，而MetaClass则用于获取类相关的信息。例如，我们可以使用MetaClass判断某个类是否有默认构造方法，还可以判断类的属性是否有对应的Getter/Setter方法



示例见mybatis源码test中的 MetaClassTest 

## ObjectFactory

ObjectFactory是MyBatis中的对象工厂，MyBatis每次创建Mapper映射结果对象的新实例时，都会使用一个对象工厂（ObjectFactory）实例来完成。ObjectFactory接口只有一个默认的实现，即DefaultObjectFactory，默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。

```java
public interface ObjectFactory {

  /**
   * Sets configuration properties.
   * @param properties configuration properties
   */
  void setProperties(Properties properties);

  /**
   * 通过默认构造函数创建对象
   * Creates a new object with default constructor. 
   * @param type Object type
   * @return
   */
  <T> T create(Class<T> type);

  /**
   * 通过指定构造函数和参数创建对象
   * @param type Object type
   * @param constructorArgTypes Constructor argument types
   * @param constructorArgs Constructor argument values
   * @return
   */
  <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);
  
  /**
   * Returns true if this object can have a set of other objects.
   * It's main purpose is to support non-java.util.Collection objects like Scala collections.
   * 
   * @param type Object type
   * @return whether it is a collection or not
   * @since 3.1.0
   */
  <T> boolean isCollection(Class<T> type);

}
```

```java
  @Test
  public void instantiateClass() throws Exception {
    DefaultObjectFactory defaultObjectFactory = new DefaultObjectFactory();
      // 根据指定构造函数实例化对象
    TestClass testClass = defaultObjectFactory.instantiateClass(TestClass.class,
        Arrays.<Class<?>>asList(String.class, Integer.class), Arrays.<Object>asList("foo", 0));

    Assert.assertEquals("myInteger didn't match expected", (Integer) 0, testClass.myInteger);
    Assert.assertEquals("myString didn't match expected", "foo", testClass.myString);
  }

public class TestClass {
  String myString;
  Integer myInteger;

  public TestClass(String myString, Integer myInteger) {
    this.myString = myString;
    this.myInteger = myInteger;
  }
}
```

## ProxyFactory

ProxyFactory是MyBatis中的代理工厂，主要用于创建动态代理对象，ProxyFactory接口有两个不同的实现，分别为CglibProxyFactory和JavassistProxyFactory。从实现类的名称可以看出，MyBatis支持两种动态代理策略，分别为Cglib和Javassist动态代理。ProxyFactory主要用于实现MyBatis的懒加载功能。当开启懒加载后，MyBatis创建Mapper映射结果对象后，会通过ProxyFactory创建映射结果对象的代理对象。当我们调用代理对象的Getter方法获取数据时，会执行CglibProxyFactory或JavassistProxyFactory中定义的拦截逻辑，然后执行一次额外的查询

## SQL

`org.apache.ibatis.jdbc.SQL`

SELECT : 

```java
private static SQL example1() {
    return new SQL() {{
      SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
      SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
      FROM("PERSON P");
      FROM("ACCOUNT A");
      INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
      INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
      WHERE("P.ID = A.ID");
      WHERE("P.FIRST_NAME like ?");
      OR();
      WHERE("P.LAST_NAME like ?");
      GROUP_BY("P.ID");
      HAVING("P.LAST_NAME like ?");
      OR();
      HAVING("P.FIRST_NAME like ?");
      ORDER_BY("P.ID");
      ORDER_BY("P.FULL_NAME");
    }};
  }
// 
 @Test
  public void shouldDemonstrateProvidedStringBuilder() {
    //You can pass in your own StringBuilder
    final StringBuilder sb = new StringBuilder();
    //From the tutorial
    final String sql = example1().usingAppender(sb).toString();

    assertEquals("SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" +
        "FROM PERSON P, ACCOUNT A\n" +
        "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n" +
        "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n" +
        "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n" +
        "OR (P.LAST_NAME like ?)\n" +
        "GROUP BY P.ID\n" +
        "HAVING (P.LAST_NAME like ?) \n" +
        "OR (P.FIRST_NAME like ?)\n" +
        "ORDER BY P.ID, P.FULL_NAME", sql);
  }
```

INSERT

```java
@Test
  public void variableLengthArgumentOnIntoColumnsAndValues() {
    final String sql = new SQL() {{
      INSERT_INTO("TABLE_A").INTO_COLUMNS("a", "b").INTO_VALUES("#{a}", "#{b}");
    }}.toString();

    System.out.println(sql);

    assertEquals("INSERT INTO TABLE_A\n (a, b)\nVALUES (#{a}, #{b})", sql);
  }
```

UPDATE

```java
@Test
  public void fixFor903UpdateJoins() {
    final SQL sql = new SQL().UPDATE("table1 a").INNER_JOIN("table2 b USING (ID)").SET("a.value = b.value");
    assertThat(sql.toString(), CoreMatchers.equalTo("UPDATE table1 a\nINNER JOIN table2 b USING (ID)\nSET a.value = b.value"));
  }
```

## ScriptRunner

该工具类用于读取脚本文件中的SQL语句并执行



```java
  @Test
  public void shouldRunScriptsUsingConnection() throws Exception {
    DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
    // 获取数据库连接
    Connection conn = ds.getConnection();
    ScriptRunner runner = new ScriptRunner(conn);
    runner.setAutoCommit(true);
    runner.setStopOnError(false);
    runner.setErrorLogWriter(null);
    runner.setLogWriter(null);
      // 执行的SQL脚本
    String resource = "test/test.sql";
    Reader reader = Resources.getResourceAsReader(resource);
    try {
        // 执行脚本
      runner.runScript(reader);
    } finally {
      reader.close();
    }
    assertProductsTableExistsAndLoaded();
  }

```

## SqlRunner

操作数据库的SqlRunner工具类

```java
 @Test
  public void shouldSelectList() throws Exception {
    DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
    Connection connection = ds.getConnection();
    SqlRunner exec = new SqlRunner(connection);
    List<Map<String, Object>> rows = exec.selectAll("SELECT * FROM PRODUCT");
    connection.close();
    assertEquals(16, rows.size());
  }
```

