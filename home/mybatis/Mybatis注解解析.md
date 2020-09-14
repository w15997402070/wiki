# Mybatis注解解析

Mybatis注解解析入口在`org.apache.ibatis.binding.MapperRegistry.addMapper(Class<T> type)`方法中 代码如下:

```java
public class MapperRegistry {
    //Configuration对象
    private final Configuration config;
    //
  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, MapperProxyFactory<?>>();

    public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) {
      if (hasMapper(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
          //加入knownMappers 中
        knownMappers.put(type, new MapperProxyFactory<T>(type));
        // It's important that the type is added before the parser is run
        // otherwise the binding may automatically be attempted by the
        // mapper parser. If the type is already known, it won't try.
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        parser.parse();
        loadCompleted = true;
      } finally {
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }
}
```



Mybatis注解解析入口类  `org.apache.ibatis.builder.annotation.MapperAnnotationBuilder`: 

```java
 public void parse() {
    String resource = type.toString();
    if (!configuration.isResourceLoaded(resource)) {
        //加载xml文件
      loadXmlResource();
      configuration.addLoadedResource(resource);
        //设置当前的Namespace
      assistant.setCurrentNamespace(type.getName());
        //解析缓存 @CacheNamespace 注解
      parseCache();
        //解析 @CacheNamespaceRef 注解
      parseCacheRef();
        //获取所有的方法
      Method[] methods = type.getMethods();
      for (Method method : methods) {
        try {
          // issue #237
            //判断是否是 桥接方法 (处理泛型编译后类型擦除)
          if (!method.isBridge()) {
            parseStatement(method);
          }
        } catch (IncompleteElementException e) {
          configuration.addIncompleteMethod(new MethodResolver(this, method));
        }
      }
    }
    parsePendingMethods();
  }

  //解析缓存方法 @CacheNamespace 注解
  private void parseCache() {
    CacheNamespace cacheDomain = type.getAnnotation(CacheNamespace.class);
    if (cacheDomain != null) {
      Integer size = cacheDomain.size() == 0 ? null : cacheDomain.size();
      Long flushInterval = cacheDomain.flushInterval() == 0 ? null : cacheDomain.flushInterval();
      assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size, cacheDomain.readWrite(), cacheDomain.blocking(), null);
    }
  }
   
   //解析@CacheNamespaceRef注解
   private void parseCacheRef() {
    CacheNamespaceRef cacheDomainRef = type.getAnnotation(CacheNamespaceRef.class);
    if (cacheDomainRef != null) {
      assistant.useCacheRef(cacheDomainRef.value().getName());
    }
  }

  void parseStatement(Method method) {
      //获取参数类型
    Class<?> parameterTypeClass = getParameterType(method);
    LanguageDriver languageDriver = getLanguageDriver(method);
      //获取 SqlSource
    SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
    if (sqlSource != null) {
        //解析 Options 注解
      Options options = method.getAnnotation(Options.class);
      final String mappedStatementId = type.getName() + "." + method.getName();
      Integer fetchSize = null;
      Integer timeout = null;
      StatementType statementType = StatementType.PREPARED;
      ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
      SqlCommandType sqlCommandType = getSqlCommandType(method);
      boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
      boolean flushCache = !isSelect;
      boolean useCache = isSelect;

      KeyGenerator keyGenerator;
      String keyProperty = "id";
      String keyColumn = null;
      if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
        // first check for SelectKey annotation - that overrides everything else
        SelectKey selectKey = method.getAnnotation(SelectKey.class);
        if (selectKey != null) {
          keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, getParameterType(method), languageDriver);
          keyProperty = selectKey.keyProperty();
        } else if (options == null) {
          keyGenerator = configuration.isUseGeneratedKeys() ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
        } else {
          keyGenerator = options.useGeneratedKeys() ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
          keyProperty = options.keyProperty();
          keyColumn = options.keyColumn();
        }
      } else {
        keyGenerator = new NoKeyGenerator();
      }

      if (options != null) {
        if (FlushCachePolicy.TRUE.equals(options.flushCache())) {
          flushCache = true;
        } else if (FlushCachePolicy.FALSE.equals(options.flushCache())) {
          flushCache = false;
        }
        useCache = options.useCache();
        fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize() : null; //issue #348
        timeout = options.timeout() > -1 ? options.timeout() : null;
        statementType = options.statementType();
        resultSetType = options.resultSetType();
      }

      String resultMapId = null;
      ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
      if (resultMapAnnotation != null) {
        String[] resultMaps = resultMapAnnotation.value();
        StringBuilder sb = new StringBuilder();
        for (String resultMap : resultMaps) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append(resultMap);
        }
        resultMapId = sb.toString();
      } else if (isSelect) {
          //解析 ResultMap
        resultMapId = parseResultMap(method);
      }

      assistant.addMappedStatement(
          mappedStatementId,
          sqlSource,
          statementType,
          sqlCommandType,
          fetchSize,
          timeout,
          // ParameterMapID
          null,
          parameterTypeClass,
          resultMapId,
          getReturnType(method),
          resultSetType,
          flushCache,
          useCache,
          // TODO gcode issue #577
          false,
          keyGenerator,
          keyProperty,
          keyColumn,
          // DatabaseID
          null,
          languageDriver,
          // ResultSets
          options != null ? nullOrEmpty(options.resultSets()) : null);
    }
  }
 // 返回ParamMap.class 或者 一个参数时的参数 类型
  private Class<?> getParameterType(Method method) {
    Class<?> parameterType = null;
    Class<?>[] parameterTypes = method.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++) {
      if (!RowBounds.class.isAssignableFrom(parameterTypes[i]) && !ResultHandler.class.isAssignableFrom(parameterTypes[i])) {
          //这里判断如果参数为一个就直接返回该参数的类型
          //如果参数为2个或以上就返回 ParamMap.class
        if (parameterType == null) {
          parameterType = parameterTypes[i];
        } else {
          // issue #135
          parameterType = ParamMap.class;
        }
      }
    }
    return parameterType;
  }
  //解析lang注解
   private LanguageDriver getLanguageDriver(Method method) {
        Lang lang = method.getAnnotation(Lang.class);
        Class<?> langClass = null;
        if (lang != null) {
          langClass = lang.value();
        }
        return assistant.getLanguageDriver(langClass);
   }

    //
    private SqlSource getSqlSourceFromAnnotations(Method method, Class<?> parameterType, LanguageDriver languageDriver) {
    try {
        //获取sql的类型  Select | Insert | Update | Delete
      Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
        // 获取sqlProvider类型 SelectProvider | InsertProvider | UpdateProvider | DeleteProvider
      Class<? extends Annotation> sqlProviderAnnotationType = getSqlProviderAnnotationType(method);
      if (sqlAnnotationType != null) {
        if (sqlProviderAnnotationType != null) {
          throw new BindingException("You cannot supply both a static SQL and SqlProvider to method named " + method.getName());
        }
        Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
          //获取sql语句
        final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
        return buildSqlSourceFromStrings(strings, parameterType, languageDriver);
      } else if (sqlProviderAnnotationType != null) {
        Annotation sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
        return new ProviderSqlSource(assistant.getConfiguration(), sqlProviderAnnotation);
      }
      return null;
    } catch (Exception e) {
      throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + e, e);
    }
  }

  //创建 SqlSource
  private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
    final StringBuilder sql = new StringBuilder();
    for (String fragment : strings) {
      sql.append(fragment);
      sql.append(" ");
    }
    return languageDriver.createSqlSource(configuration, sql.toString().trim(), parameterTypeClass);
  }
  //解析ResultMap
  private String parseResultMap(Method method) {
      //获取方法的返回值类型
    Class<?> returnType = getReturnType(method);
      //获取构造函数
    ConstructorArgs args = method.getAnnotation(ConstructorArgs.class);
      // 获取@Results注解,也就是注解形式的结果映射
    Results results = method.getAnnotation(Results.class);
      // 获取鉴别器
    TypeDiscriminator typeDiscriminator = method.getAnnotation(TypeDiscriminator.class);
    String resultMapId = generateResultMapName(method);
    applyResultMap(resultMapId, returnType, argsIf(args), resultsIf(results), typeDiscriminator);
    return resultMapId;
  }
```

`org.apache.ibatis.scripting.xmltags.XMLLanguageDriver.createSqlSource()`方法创建 SqlSource ,这里会根据sql语句创建不同的实现 : 

```java
  @Override
  public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
    // issue #3
    if (script.startsWith("<script>")) {
      XPathParser parser = new XPathParser(script, false, configuration.getVariables(), new XMLMapperEntityResolver());
      return createSqlSource(configuration, parser.evalNode("/script"), parameterType);
    } else {
      // issue #127
      script = PropertyParser.parse(script, configuration.getVariables());
      TextSqlNode textSqlNode = new TextSqlNode(script);
        //判断是否是动态sql
      if (textSqlNode.isDynamic()) {
        return new DynamicSqlSource(configuration, textSqlNode);
      } else {
        return new RawSqlSource(configuration, script, parameterType);
      }
    }
  }
```

