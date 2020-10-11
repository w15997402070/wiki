# Mybatis运行解析

[toc]



## SqlSessionFactory创建过程

```Java
import java.sql.Connection;

/**
 * Creates an {@link SqlSession} out of a connection or a DataSource
 * 
 * @author Clinton Begin
 */
public interface SqlSessionFactory {

  SqlSession openSession();

  SqlSession openSession(boolean autoCommit);
  SqlSession openSession(Connection connection);
  SqlSession openSession(TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType);
  SqlSession openSession(ExecutorType execType, boolean autoCommit);
  SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);
  SqlSession openSession(ExecutorType execType, Connection connection);

  Configuration getConfiguration();

}
```



主要功能是创建 Mybatis 的核心接口 SqlSession.

SqlSessionFactoryBuilder 采用构造模式(Builder模式)去创建 SqlSessionFactory.创建分为两步 : 

* 第一步，通过`org.apache.ibatis.builder.xml.XMLConfigBuilder`解析配置的XML文件，读出配置参数，并将读取的数据存入这个`org.apache.ibatis.session.Configuration`类中。注意，MyBatis几乎所有的配置都是存在这里的。

* 第二步，使用`Confinguration` 对象去创建 `SqlSessionFactory`。MyBatis 中的`SqlSessionFactory`是一个接口，而不是实现类，为此MyBatis提供了一个默认的`SqlSessionFactory`实现类，我们一般都会使用它`org.apache.ibatis.session.defaults.DefaultSqlSessionFactory`。注意，在大部分情况下我们都没有必要自己去创建新的`SqlSessionFactory`的实现类。

### 创建 [`Configuration`](D:\data\notes\notes\mybatis\创建Configuration过程.md)

configuration的作用 : 

* 读入配置文件，包括基础配置的XML文件和映射器的XML文件。
* 初始化基础配置，比如MyBatis的别名等，一些重要的类对象，例如，插件、映射器、ObjectFactory和typeHandler对象。
* 提供单例，为后续创建SessionFactory服务并提供配置的参数。
* 执行一些重要的对象方法，初始化配置信息。

### 映射器的组成

一个映射器一般由三个部分组成 : 

* MappedStatement，它保存映射器的一个节点（`select` l `insert`|`delete`l`update`）。包括许多我们配置的SQL、SQL的id、缓存信息、resultMap、parameterType、resultType、languageDriver等重要配置内容。
* SqlSource，它是提供BoundSql对象的地方，它是MappedStatement的一个属性。
* BoundSql，它是建立SQL和参数的地方。它有3个常用的属性：SQL、parameterObject、parameterMappings，稍后我们会讨论它们。

### 创建 SqlSessionFactory

`sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream)`

## SqlSession运行过程

```Java
package org.apache.ibatis.session;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;

public interface SqlSession extends Closeable {

  <T> T selectOne(String statement);

  <T> T selectOne(String statement, Object parameter);

  <E> List<E> selectList(String statement);

  <E> List<E> selectList(String statement, Object parameter);

  <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

  <K, V> Map<K, V> selectMap(String statement, String mapKey);

  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

  <T> Cursor<T> selectCursor(String statement);

  <T> Cursor<T> selectCursor(String statement, Object parameter);

  <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

  void select(String statement, Object parameter, ResultHandler handler);

  void select(String statement, ResultHandler handler);

  void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

  int insert(String statement);

  int insert(String statement, Object parameter);

  int update(String statement);

  int update(String statement, Object parameter);

  int delete(String statement);

  int delete(String statement, Object parameter);

  void commit();

  void commit(boolean force);

  void rollback();

  void rollback(boolean force);

  List<BatchResult> flushStatements();

  @Override
  void close();

  void clearCache();

  Configuration getConfiguration();

  <T> T getMapper(Class<T> type);

  Connection getConnection();
}

```

### 映射器的动态代理

Mapper是通过动态代理实现的 : 

#### MapperProxyFactory类 : 

```Java
package org.apache.ibatis.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;

/**
 * @author Lasse Voss
 */
public class MapperProxyFactory<T> {

  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();

  public MapperProxyFactory(Class<T> mapperInterface) {
    this.mapperInterface = mapperInterface;
  }

  public Class<T> getMapperInterface() {
    return mapperInterface;
  }

  public Map<Method, MapperMethod> getMethodCache() {
    return methodCache;
  }

  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
      //第三个参数传递了动态代理对象 MapperProxy
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

  /*
  * 生成动态代理对象
  */
  public T newInstance(SqlSession sqlSession) {
      //这个是真正的动态代理对象
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }

}
```

#### MapperProxy类 : 

```Java
package org.apache.ibatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 真正的代理
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

  private static final long serialVersionUID = -6424540398559729838L;
  private final SqlSession sqlSession;
  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

    /*
    * 这里是主要调用的方法
    */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      //判断是不是一个类
      if (Object.class.equals(method.getDeclaringClass())) {
      try {
        return method.invoke(this, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
      //如果是mapper接口就会生成 MapperMethod 对象
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);
  }

  private MapperMethod cachedMapperMethod(Method method) {
    MapperMethod mapperMethod = methodCache.get(method);
    if (mapperMethod == null) {
      mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
      methodCache.put(method, mapperMethod);
    }
    return mapperMethod;
  }

}

```

MapperMethod 的 execute方法 : 

`这里采用命令模式`

```Java
  private final SqlCommand command;
  private final MethodSignature method;

  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    this.command = new SqlCommand(config, mapperInterface, method);
    this.method = new MethodSignature(config, mapperInterface, method);
  }

public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    if (SqlCommandType.INSERT == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.insert(command.getName(), param));
    } else if (SqlCommandType.UPDATE == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.update(command.getName(), param));
    } else if (SqlCommandType.DELETE == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.delete(command.getName(), param));
    } else if (SqlCommandType.SELECT == command.getType()) {
      if (method.returnsVoid() && method.hasResultHandler()) {
        executeWithResultHandler(sqlSession, args);
        result = null;
      } else if (method.returnsMany()) {
        result = executeForMany(sqlSession, args);
      } else if (method.returnsMap()) {
        result = executeForMap(sqlSession, args);
      } else if (method.returnsCursor()) {
        result = executeForCursor(sqlSession, args);
      } else {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = sqlSession.selectOne(command.getName(), param);
      }
    } else if (SqlCommandType.FLUSH == command.getType()) {
        result = sqlSession.flushStatements();
    } else {
      throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName() 
          + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }
```

主要看 executeForMany(SqlSession sqlSession, Object[] args) 方法 :

```Java
private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.<E>selectList(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.<E>selectList(command.getName(), param);
    }
    // issue #510 Collections & arrays support
    if (!method.getReturnType().isAssignableFrom(result.getClass())) {
      if (method.getReturnType().isArray()) {
        return convertToArray(result);
      } else {
        return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
      }
    }
    return result;
  }
```

### SqlSession下的四大对象

从MapperMethod 的 execute 方法 进入 SqlSession 的删除,更新,插入,查询等方法.

通过类名与方法就可以匹配到sql,Mapper的执行过程是通过 Executor,StatementHandler,ParameterHandler,ResultHandler来完成数据库操作和结果返回.

* Executor 代表执行器，由它来调度StatementHandler、ParameterHandler、ResultHandler等来执行对应的SQL。
* StatementHandler的作用是使用数据库的Statement（PreparedStatement）执行操作，它是四大对象的核心，起到承上启下的作用。
* ParameterHandler用于SQL对参数的处理。
* ResultHandler 是进行最后数据集（ResultSet）的封装返回处理的。

### 执行器 Executor 

Mybatis 中存在三种执行器,在配置文件中可以配置,setting元素的属性 defaultExecutorType 设置 : 

* SIMPLE 简易执行器,不配置他就是默认执行器
* REUSE 是一种执行器重用预处理语句
* BATCH 执行器重用语句和批量更新,是针对批量专用的执行器

Mybatis 创建执行器的代码 : 

`org.apache.ibatis.session.Configuration`

```java
public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
      executor = new CachingExecutor(executor);
    }
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }
```

SimpleExecutor 执行器的执行过程 : 

`org.apache.ibatis.executor.SimpleExecutor`

```java
@Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
        //通过Configuration构建StatementHandler
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
        //对sql编译并对参数进行初始化
      stmt = prepareStatement(handler, ms.getStatementLog());
      return handler.<E>query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }

 private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    Connection connection = getConnection(statementLog);
     //预编译和基础设置
    stmt = handler.prepare(connection, transaction.getTimeout());
     //设置参数并执行
    handler.parameterize(stmt);
    return stmt;
  }
```

### 数据库回话器 StatementHandler

创建会话器的代码 : 

`org.apache.ibatis.session.Configuration`

```java
public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
  }
```

`RoutingStatementHandler` 对象,通过适配器模式找到对应的 StatementHandler:

`org.apache.ibatis.executor.statement.RoutingStatementHandler`

StatementHandler 分为三类 : 

* SimpleStatementHandler
* PreparedStatementHandler (默认)
* CallableStatementHandler

```jav
 public class RoutingStatementHandler implements StatementHandler {
 
     private final StatementHandler delegate;

      public RoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        switch (ms.getStatementType()) {
          case STATEMENT:
            delegate = new SimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            break;
          case PREPARED:
            delegate = new PreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            break;
          case CALLABLE:
            delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
            break;
          default:
            throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
        }
      }
 }
```

PreparedStatementHandler 的代码 : 

```java
package org.apache.ibatis.executor.statement

public class PreparedStatementHandler extends BaseStatementHandler {

  public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
  }

  @Override
  public int update(Statement statement) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    ps.execute();
    int rows = ps.getUpdateCount();
    Object parameterObject = boundSql.getParameterObject();
    KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
    keyGenerator.processAfter(executor, mappedStatement, ps, parameterObject);
    return rows;
  }

  @Override
  public void batch(Statement statement) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    ps.addBatch();
  }
  //这里真正执行 resultSetHandler 对返回结果进行封装
  @Override
  public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    ps.execute();
    return resultSetHandler.<E> handleResultSets(ps);
  }

  @Override
  public <E> Cursor<E> queryCursor(Statement statement) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    ps.execute();
    return resultSetHandler.<E> handleCursorResultSets(ps);
  }

//对sql进行预编译
  @Override
  protected Statement instantiateStatement(Connection connection) throws SQLException {
    String sql = boundSql.getSql();
    if (mappedStatement.getKeyGenerator() instanceof Jdbc3KeyGenerator) {
      String[] keyColumnNames = mappedStatement.getKeyColumns();
      if (keyColumnNames == null) {
        return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      } else {
        return connection.prepareStatement(sql, keyColumnNames);
      }
    } else if (mappedStatement.getResultSetType() != null) {
      return connection.prepareStatement(sql, mappedStatement.getResultSetType().getValue(), ResultSet.CONCUR_READ_ONLY);
    } else {
      return connection.prepareStatement(sql);
    }
  }
  //设置参数
  @Override
  public void parameterize(Statement statement) throws SQLException {
    parameterHandler.setParameters((PreparedStatement) statement);
  }

}
```

```java
package org.apache.ibatis.executor.statement;

public abstract class BaseStatementHandler implements StatementHandler {

  protected final Configuration configuration;
  protected final ObjectFactory objectFactory;
  protected final TypeHandlerRegistry typeHandlerRegistry;
  protected final ResultSetHandler resultSetHandler;
  protected final ParameterHandler parameterHandler;

  protected final Executor executor;
  protected final MappedStatement mappedStatement;
  protected final RowBounds rowBounds;

  protected BoundSql boundSql;

  protected BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    this.configuration = mappedStatement.getConfiguration();
    this.executor = executor;
    this.mappedStatement = mappedStatement;
    this.rowBounds = rowBounds;

    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    this.objectFactory = configuration.getObjectFactory();

    if (boundSql == null) { // issue #435, get the key before calculating the statement
      generateKeys(parameterObject);
      boundSql = mappedStatement.getBoundSql(parameterObject);
    }

    this.boundSql = boundSql;

    this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
    this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql);
  }

  @Override
  public BoundSql getBoundSql() {
    return boundSql;
  }

  @Override
  public ParameterHandler getParameterHandler() {
    return parameterHandler;
  }

  @Override
  public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
    ErrorContext.instance().sql(boundSql.getSql());
    Statement statement = null;
    try {
      statement = instantiateStatement(connection);
      setStatementTimeout(statement, transactionTimeout);
      setFetchSize(statement);
      return statement;
    } catch (SQLException e) {
      closeStatement(statement);
      throw e;
    } catch (Exception e) {
      closeStatement(statement);
      throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
    }
  }

  protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

  protected void setStatementTimeout(Statement stmt, Integer transactionTimeout) throws SQLException {
    Integer queryTimeout = null;
    if (mappedStatement.getTimeout() != null) {
      queryTimeout = mappedStatement.getTimeout();
    } else if (configuration.getDefaultStatementTimeout() != null) {
      queryTimeout = configuration.getDefaultStatementTimeout();
    }
    if (queryTimeout != null) {
      stmt.setQueryTimeout(queryTimeout);
    }
    StatementUtil.applyTransactionTimeout(stmt, queryTimeout, transactionTimeout);
  }

  protected void setFetchSize(Statement stmt) throws SQLException {
    Integer fetchSize = mappedStatement.getFetchSize();
    if (fetchSize != null) {
      stmt.setFetchSize(fetchSize);
      return;
    }
    Integer defaultFetchSize = configuration.getDefaultFetchSize();
    if (defaultFetchSize != null) {
      stmt.setFetchSize(defaultFetchSize);
    }
  }

  protected void closeStatement(Statement statement) {
    try {
      if (statement != null) {
        statement.close();
      }
    } catch (SQLException e) {
      //ignore
    }
  }

  protected void generateKeys(Object parameter) {
    KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
    ErrorContext.instance().store();
    keyGenerator.processBefore(executor, mappedStatement, null, parameter);
    ErrorContext.instance().recall();
  }

}
```

一条sql的执行流程 : 

Executor 先调用 StatementHandle 的 prepare() 方法预编译sql,同时设置一些基本运行参数,然后用 parameterize() 方法启用 ParameterHandler 设置参数,完成预编译,跟着执行查询,最后用ResultHandler封装结果返回给调用者.

### 参数处理器  ParameterHandler

ParameterHandler 代码 : 

```java
package org.apache.ibatis.executor.parameter

public interface ParameterHandler { 
    //返回参数对象
    Object getParameterObject();  
    //设置预编译SQL语句的参数
	void setParameters(PreparedStatement ps) throws SQLException;
}
```

默认实现为 DefaultParameterHandler : 

```java
  //设置参数
  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          } catch (SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }
```

### 结果处理器 ResultSetHandler

ResultSetHandler 代码  : 

```java
package org.apache.ibatis.executor.resultset;

public interface ResultSetHandler {
  //包装结果集
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;
  //处理存储过程输出参数
  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
```

默认实现为 DefaultResultSetHandler :

