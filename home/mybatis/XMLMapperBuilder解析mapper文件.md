# XMLMapperBuilder解析mapper文件

XMLMapperBuilder 解析 mappers 节点的信息:

### mapper文件信息:

```xml
<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.*.PostMapper">
    <resultMap id="postLiteIdMap" type="org.apache.ibatis.domain.blog.PostLiteId">
          <constructor>
              <idArg javaType="_int" column="id"/>
          </constructor>
   </resultMap>
  <resultMap id="postLiteMap" type="org.apache.ibatis.domain.blog.PostLite">
      <constructor>
          <arg javaType="org.apache.ibatis.domain.blog.PostLiteId" resultMap="postLiteIdMap"/>
          <arg javaType="_int" column="blog_id"/>
      </constructor>
  </resultMap>

  <resultMap id="postLiteMap2NestedWithSelect" type="org.apache.ibatis.domain.blog.BlogLite">
    <id column="blog_id" property="id" />
    <collection property="posts" ofType="org.apache.ibatis.domain.blog.PostLite">
      <constructor>
          <arg javaType="org.apache.ibatis.domain.blog.PostLiteId" column="{id=id}" select="selectPostLiteId" />
          <arg javaType="_int" column="blog_id"/>
      </constructor>
    </collection>
  </resultMap>

  <resultMap id="postLiteMap2NestedWithoutSelect" type="org.apache.ibatis.domain.blog.BlogLite">
    <id column="blog_id" property="id" />
    <collection property="posts" ofType="org.apache.ibatis.domain.blog.PostLite">
      <constructor>
          <arg javaType="org.apache.ibatis.domain.blog.PostLiteId" resultMap="postLiteIdMap" />
          <arg javaType="_int" column="blog_id"/>
      </constructor>
    </collection>
  </resultMap>

  <resultMap id="mutablePostLiteMap" type="org.apache.ibatis.domain.blog.PostLite">
      <result property="blogId" column="blog_id"/>
      <association property="id" column="id" resultMap="postLiteIdMap"/>
  </resultMap>

  <resultMap id="mutablePostLiteIdMap" type="org.apache.ibatis.domain.blog.PostLiteId">
      <id property="id" column="id"/>
  </resultMap>

    <sql id="sometable">
        SomeTable
    </sql>
    
  <select id="selectPostLite" resultMap="postLiteMap">
      select id, blog_id from post where blog_id is not null
  </select>

  <select id="selectPostLite2NestedWithSelect" resultMap="postLiteMap2NestedWithSelect">
      select id, 1 as blog_id from post where blog_id is not null
  </select>

  <select id="selectPostLite2NestedWithoutSelect" resultMap="postLiteMap2NestedWithoutSelect">
      select id, 1 as blog_id from post where blog_id is not null
  </select>

  <select id="selectPostLiteId" resultMap="postLiteIdMap">
      select ${id} as id from (values(0)) as t
  </select>

  <select id="selectMutablePostLite" resultMap="mutablePostLiteMap">
      select id, blog_id from post where blog_id is not null
  </select>

  <sql id="byBlogId">
    <if test="blog_id != null">blog_id = #{blog_id}</if>
  </sql>

  <select id="findPost" resultType="org.apache.ibatis.domain.blog.Post">
    SELECT *
    FROM POST P
    <where>
      <choose>
        <when test="id != null">id = #{id}</when>
        <when test="author_id != null">AND author_id = #{author_id}</when>
        <otherwise>
          <if test="ids != null">
            AND id IN
            <foreach item="item_id" index="index" open="(" close=")" separator="," collection="ids">#{ids[${index}]}
            </foreach>
          </if>
          <trim prefix="AND">
            <include refid="byBlogId"/>
          </trim>
        </otherwise>
      </choose>
    </where>
  </select>

  <select id="selectPostIn" resultType="org.apache.ibatis.domain.blog.Post">
    SELECT *
    FROM POST P
    WHERE ID in
    <foreach item="item" index="index" collection="list"
             open="(" close=")">
      <if test="index != 0">,</if> #{item}
    </foreach>
  </select>

  <select id="selectOddPostsIn" resultType="org.apache.ibatis.domain.blog.Post">
    SELECT *
    FROM POST P
    WHERE ID in
    <foreach item="item" index="index" collection="list"
             open="(" separator="," close=")">
      <if test="index % 2 != 0">
        #{item}
      </if>
    </foreach>
    ORDER BY P.ID
  </select>

  <select id="selectOddPostsInKeysList" resultType="org.apache.ibatis.domain.blog.Post">
    SELECT *
    FROM POST P
    WHERE ID in
    <foreach item="item" index="index" collection="keys"
             open="(" separator="," close=")">
      <if test="index % 2 != 0">
        #{item}
      </if>
    </foreach>
    ORDER BY P.ID
  </select>
</mapper>
```

### XMLMapperBuilder.parse()方法解析:

```java
package org.apache.ibatis.builder.xml;

public class XMLMapperBuilder extends BaseBuilder {

  private XPathParser parser;
  private MapperBuilderAssistant builderAssistant;
  private Map<String, XNode> sqlFragments;
  private String resource;

    public void parse() {
        //判断是否已加载文件
        if (!configuration.isResourceLoaded(resource)) {
            //解析mapper节点
          configurationElement(parser.evalNode("/mapper"));
            //加入到Configuration中保存,Set<String> loadedResources
          configuration.addLoadedResource(resource);
          bindMapperForNamespace();
        }

        parsePendingResultMaps();
        parsePendingChacheRefs();
        parsePendingStatements();
      }
    //
    private void configurationElement(XNode context) {
        try {
            //获取namespace
          String namespace = context.getStringAttribute("namespace");
          if (namespace == null || namespace.equals("")) {
            throw new BuilderException("Mapper's namespace cannot be empty");
          }
          builderAssistant.setCurrentNamespace(namespace);
            //对其他命名空间缓存配置的引用
          cacheRefElement(context.evalNode("cache-ref"));
            //对给定命名空间的缓存配置
          cacheElement(context.evalNode("cache"));
            //已废弃
          parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            //结果映射
          resultMapElements(context.evalNodes("/mapper/resultMap"));
            //可以被引用的sql语句块
          sqlElement(context.evalNodes("/mapper/sql"));
         buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
          throw new BuilderException("Error parsing Mapper XML. Cause: " + e, e);
        }
    }
    
    //...
}

```

#### 缓存 cache 节点: 

```java
    //解析cache节点 : 
    private void cacheElement(XNode context) throws Exception {
        if (context != null) {
          String type = context.getStringAttribute("type", "PERPETUAL");
          Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
          String eviction = context.getStringAttribute("eviction", "LRU");
          Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
          Long flushInterval = context.getLongAttribute("flushInterval");
          Integer size = context.getIntAttribute("size");
          boolean readWrite = !context.getBooleanAttribute("readOnly", false);
          boolean blocking = context.getBooleanAttribute("blocking", false);
          Properties props = context.getChildrenAsProperties();
            //通过MapperBuilderAssistant 创建 Cache对象,并添加到 Configuration.caches集合中保存
          builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
        }
      }
```

启用全局二级缓存:

```xml
<cache/>
```

基本上就是这样。这个简单语句的效果如下:

- 映射语句文件中的所有 select 语句的结果将会被缓存。

- 映射语句文件中的所有 insert、update 和 delete 语句会刷新缓存。

- 缓存会使用最近最少使用算法（LRU, Least Recently Used）算法来清除不需要的缓存。

- 缓存不会定时进行刷新（也就是说，没有刷新间隔）。

- 缓存会保存列表或对象（无论查询方法返回哪种）的 1024 个引用。

- 缓存会被视为读/写缓存，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。


可以通过下面的元素属性配置

```xml

<cache
  eviction="FIFO" 
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

* eviction 清除策略

  可用的清除策略有：

  - `LRU` – 最近最少使用：移除最长时间不被使用的对象。
  - `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。
  - `SOFT` – 软引用：基于垃圾回收器状态和软引用规则移除对象。
  - `WEAK` – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。

* flushInterval（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。

* size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。

* readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。

Mybatis 缓存接口 : 

```java
public interface Cache {
  String getId();
  int getSize();
  void putObject(Object key, Object value);
  Object getObject(Object key);
  boolean hasKey(Object key);
  Object removeObject(Object key);
  void clear();
}
```

MapperBuilderAssistant是一个辅助类,useNewCache() 方法创建 Cache对象,并将其添加到 Configuration.caches中,Configuration 中的caches字段是 `StrictMap<Cache>`类型的字段,它记录 Cache 的i,默认是 映射文件的 namespace 与 Cache 对象(二级缓存)之间的对应关系

```java
public class MapperBuilderAssistant extends BaseBuilder {
     private String currentNamespace;
     private String resource;
     private Cache currentCache;
     private boolean unresolvedCacheRef; // issue #676
    
    public Cache useNewCache(Class<? extends Cache> typeClass,
      Class<? extends Cache> evictionClass,
      Long flushInterval,
      Integer size,
      boolean readWrite,
      boolean blocking,
      Properties props) {
        //建造者模式设置值
    Cache cache = new CacheBuilder(currentNamespace)
        .implementation(valueOrDefault(typeClass, PerpetualCache.class))
        .addDecorator(valueOrDefault(evictionClass, LruCache.class))
        .clearInterval(flushInterval)
        .size(size)
        .readWrite(readWrite)
        .blocking(blocking)
        .properties(props)
        .build();
        //加入到Configuration中
    configuration.addCache(cache);
    currentCache = cache;
    return cache;
  }
}
```

CacheBuilder.build() 方法 : 

```java

public class CacheBuilder {
    private String id;
    private Class<? extends Cache> implementation;
    private List<Class<? extends Cache>> decorators;
    private Integer size;
    private Long clearInterval;
    private boolean readWrite;
    private Properties properties;
    private boolean blocking;

    public Cache build() {
        //设置默认的实现
        setDefaultImplementations();
        //根据 implementation 指定的类型,通过反射获取参数为string 类型的构造方法,并通过该构造方法创建 Cache 对象
        Cache cache = newBaseCacheInstance(implementation, id);
        //设置属性信息
        setCacheProperties(cache);
        // issue #352, do not apply decorators to custom caches
        //如果自定义类型的Cache接口实现,则不添加 decorators 集合中的装饰器
        if (PerpetualCache.class.equals(cache.getClass())) {
          for (Class<? extends Cache> decorator : decorators) {
              //通过反射获取参数为Cache类型的构造方法,并通过该构造方法创建装饰器
            cache = newCacheDecoratorInstance(decorator, cache);
              //设置属性
            setCacheProperties(cache);
          }
            //提供Mybatis中提供的标准装饰器
          cache = setStandardDecorators(cache);
        } else if (!LoggingCache.class.isAssignableFrom(cache.getClass())) {
          cache = new LoggingCache(cache);
        }
        return cache;
    }
    
    private void setDefaultImplementations() {
        if (implementation == null) {
          implementation = PerpetualCache.class;
          if (decorators.isEmpty()) {
            decorators.add(LruCache.class);
          }
        }
   }
    
     private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
        Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);
        try {
          return cacheConstructor.newInstance(id);
        } catch (Exception e) {
          throw new CacheException("Could not instantiate cache implementation (" + cacheClass + "). Cause: " + e, e);
        }
  }
}

```

####  cache-ref 节点

```java
private void cacheRefElement(XNode context) {
    if (context != null) {
        //加入到 Configuration 的 cacheRefMap 中
      configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
        //创建 CacheRefSolver 对象
      CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, context.getStringAttribute("namespace"));
      try {
          //解析 cache引用,该过程主要是设置 MapperBuilderAssistant 中的 currentRefResolver和 unresolverCacheRef
        cacheRefResolver.resolveCacheRef();
      } catch (IncompleteElementException e) {
          //如果解析过程出现异常,则添加到Configuration.incompleteCacheRefs 集合,稍后再解析
        configuration.addIncompleteCacheRef(cacheRefResolver);
      }
    }
  }
```

#### resultMap节点

```java
  private void resultMapElements(List<XNode> list) throws Exception {
    for (XNode resultMapNode : list) {
      try {
        resultMapElement(resultMapNode);
      } catch (IncompleteElementException e) {
        // ignore, it will be retried
      }
    }
  }

  private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
    return resultMapElement(resultMapNode, Collections.<ResultMapping> emptyList());
  }

  private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings) throws Exception {
    ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
    String id = resultMapNode.getStringAttribute("id",
        resultMapNode.getValueBasedIdentifier());
    String type = resultMapNode.getStringAttribute("type",
        resultMapNode.getStringAttribute("ofType",
            resultMapNode.getStringAttribute("resultType",
                resultMapNode.getStringAttribute("javaType"))));
    String extend = resultMapNode.getStringAttribute("extends");
    Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
    Class<?> typeClass = resolveClass(type);
    Discriminator discriminator = null;
    List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
    resultMappings.addAll(additionalResultMappings);
    List<XNode> resultChildren = resultMapNode.getChildren();
    for (XNode resultChild : resultChildren) {
      if ("constructor".equals(resultChild.getName())) {
        processConstructorElement(resultChild, typeClass, resultMappings);
      } else if ("discriminator".equals(resultChild.getName())) {
        discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
      } else {
        List<ResultFlag> flags = new ArrayList<ResultFlag>();
        if ("id".equals(resultChild.getName())) {
          flags.add(ResultFlag.ID);
        }
        resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
      }
    }
    ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
    try {
      return resultMapResolver.resolve();
    } catch (IncompleteElementException  e) {
      configuration.addIncompleteResultMap(resultMapResolver);
      throw e;
    }
  }

```

ResultMapping 对象 对应 `<result property="username" column="author_username"/>`

``` xml
<resultMap id="authorResult" type="Author">
  <result property="username" column="author_username"/>
</resultMap>
```

```java
public class ResultMapping {

  private Configuration configuration; //Configuration对象
    //对应节点的property属性,表示与该列进行映射的属性
  private String property;
    //对应节点的column属性,表示从数据库中得到的列名或是列名的别名
  private String column;
    //对应节点的JavaType 表示的是一个JavaBean的完全限定名,或是一个类型别名
  private Class<?> javaType;
    //对应节点的jdbcType属性,表示的是进行映射的列的jdbc类型
  private JdbcType jdbcType;
    //对应节点的typeHandler属性,类型处理器,会覆盖默认的类型处理器
  private TypeHandler<?> typeHandler;
    //对应节点的resultMap属性，该属性通过id引用了另一个<resultMap>节点定义，它负责将结果集中的一部
//分列映射成其他关联的结果对象。这样我们就可以通过join方式进行关联查询，然后直接映射成多个对象，
//并同时设置这些对象之间的组合关系
  private String nestedResultMapId;
    //对应节点的select属性，该属性通过id引用了另一个<select>节点定义，它会把指定的列的值传入
//select属性指定的select语句中作为参数进行查询。使用select属性可能会导致N+1问题，请读者注意
  private String nestedQueryId;
    //对应节点的 notNullColumn 属性拆分后的结果
  private Set<String> notNullColumns;
    //对应 columnPrefix 属性
  private String columnPrefix;
    //处理后的标志,标志共两个 : id和constructor
  private List<ResultFlag> flags;
    //对应节点的 column 属性拆分后生成的结果,composites.size() > 0 会使 column为 null
  private List<ResultMapping> composites;
    //对应resulSet属性
  private String resultSet;
    // 对应 foreignColumn 属性
  private String foreignColumn;
    //是否延迟加载,对应节点的fetchType属性
  private boolean lazy;
}
```

ResultMap 对应 `<resultMap id="authorResult" type="Author"></resultMap>` : 

```java
public class ResultMap {
    //resultMap  节点id属性 
  private String id;
    // type 属性
  private Class<?> type;
    //记录了除<discriminator> 节点之外的其他映射关系
  private List<ResultMapping> resultMappings;
    //记录了映射关系中带有ID标志的映射关系,如<id> 和<constructor>节点的<idArg>子节点
  private List<ResultMapping> idResultMappings;
    //记录了映射关系中带有 Constructor 标志的映射关系,例如<constructor>所有的子元素
  private List<ResultMapping> constructorResultMappings;
    //记录了映射关系中不带有Constructor 标志的映射关系
  private List<ResultMapping> propertyResultMappings;
    //记录所有映射关系中涉及column属性的集合
  private Set<String> mappedColumns;
    //鉴别器对应<discriminator>节点
  private Discriminator discriminator;
    //是否含有嵌套的结果映射,如果某个映射关系中存在resultMap属性,且不存在resultSet,则为true
  private boolean hasNestedResultMaps;
    //是否有嵌套查询,如果某个属性映射在select属性,则为true
  private boolean hasNestedQueries;
    //是否开启自动映射
  private Boolean autoMapping;
}
```

#### sql节点

`<sql>`节点定义可重用的sql语句片段,当需要重用sql节点中的sql语句片段时,只需要使用`<include>`节点引入相应的片段即可.

XMLMapperBuilder.sqlElement()解析代码如下:

```java
private void sqlElement(List<XNode> list) throws Exception {
    if (configuration.getDatabaseId() != null) {
      sqlElement(list, configuration.getDatabaseId());
    }
    sqlElement(list, null);
  }

  private void sqlElement(List<XNode> list, String requiredDatabaseId) throws Exception {
    for (XNode context : list) {
      String databaseId = context.getStringAttribute("databaseId");
      String id = context.getStringAttribute("id");
        //检测sql节点的databaseId与当前Configuration中记录的databaseId是否一致
      id = builderAssistant.applyCurrentNamespace(id, false);
      if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
        sqlFragments.put(id, context);
      }
    }
  }
```

#### select|update|insert|delete节点

节点解析代码 : 

```java
private void buildStatementFromContext(List<XNode> list) {
    if (configuration.getDatabaseId() != null) {
      buildStatementFromContext(list, configuration.getDatabaseId());
    }
    buildStatementFromContext(list, null);
  }

 private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
    for (XNode context : list) {
        //创建 XMLStatementBuilder对象
      final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
      try {
          //statementParser 解析节点
        statementParser.parseStatementNode();
      } catch (IncompleteElementException e) {
        configuration.addIncompleteStatement(statementParser);
      }
    }
  }
```

XMLStatementBuilder.parseStatementNode()解析代码:

```java
package org.apache.ibatis.builder.xml;

public class XMLStatementBuilder extends BaseBuilder {
    
    public void parseStatementNode() {
    String id = context.getStringAttribute("id");
    String databaseId = context.getStringAttribute("databaseId");

    if (!databaseIdMatchesCurrent(id, databaseId, this.requiredDatabaseId)) {
      return;
    }

    Integer fetchSize = context.getIntAttribute("fetchSize");
    Integer timeout = context.getIntAttribute("timeout");
    String parameterMap = context.getStringAttribute("parameterMap");
    String parameterType = context.getStringAttribute("parameterType");
    Class<?> parameterTypeClass = resolveClass(parameterType);
    String resultMap = context.getStringAttribute("resultMap");
    String resultType = context.getStringAttribute("resultType");
    String lang = context.getStringAttribute("lang");
    LanguageDriver langDriver = getLanguageDriver(lang);

    Class<?> resultTypeClass = resolveClass(resultType);
    String resultSetType = context.getStringAttribute("resultSetType");
    StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));
    ResultSetType resultSetTypeEnum = resolveResultSetType(resultSetType);
   //根据sql节点的名称决定SqlCommandType 的类型 
    String nodeName = context.getNode().getNodeName();
    SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
    boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
    boolean flushCache = context.getBooleanAttribute("flushCache", !isSelect);
    boolean useCache = context.getBooleanAttribute("useCache", isSelect);
    boolean resultOrdered = context.getBooleanAttribute("resultOrdered", false);

    // Include Fragments before parsing
    //处理include节点
    XMLIncludeTransformer includeParser = new XMLIncludeTransformer(configuration, builderAssistant);
    includeParser.applyIncludes(context.getNode());

    // Parse selectKey after includes and remove them.
    //处理selectKey节点
    processSelectKeyNodes(id, parameterTypeClass, langDriver);
    
    // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
    SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);
    String resultSets = context.getStringAttribute("resultSets");
    String keyProperty = context.getStringAttribute("keyProperty");
    String keyColumn = context.getStringAttribute("keyColumn");
    KeyGenerator keyGenerator;
    String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
    keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
    if (configuration.hasKeyGenerator(keyStatementId)) {
      keyGenerator = configuration.getKeyGenerator(keyStatementId);
    } else {
      keyGenerator = context.getBooleanAttribute("useGeneratedKeys",
          configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType))
          ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
    }

    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
        fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
        resultSetTypeEnum, flushCache, useCache, resultOrdered, 
        keyGenerator, keyProperty, keyColumn, databaseId, langDriver, resultSets);
  }
}
```

##### 解析include节点

```java
XMLIncludeTransformer includeParser = new XMLIncludeTransformer(configuration, builderAssistant);
includeParser.applyIncludes(context.getNode());

public class XMLIncludeTransformer {

  private final Configuration configuration;
  private final MapperBuilderAssistant builderAssistant;
    
  public void applyIncludes(Node source) {
      //获取	Mybatis-config.xml 中 <properties> 节点下定义的变量集合
    Properties variablesContext = new Properties();
    Properties configurationVariables = configuration.getVariables();
    if (configurationVariables != null) {
      variablesContext.putAll(configurationVariables);
    }
      //处理include节点
    applyIncludes(source, variablesContext);
  }
    
    
   private void applyIncludes(Node source, final Properties variablesContext) {
    if (source.getNodeName().equals("include")) {
      // new full context for included SQL - contains inherited context and new variables from current include node
      Properties fullContext;
      
      String refid = getStringAttribute(source, "refid");
      // replace variables in include refid value
        //解析 refid属性的值
      refid = PropertyParser.parse(refid, variablesContext);
        //返回refid指向的sql节点,是其深克隆的Node对象
      Node toInclude = findSqlFragment(refid);
        //解析include节点下的property节点,将得到的键值对添加到variablesContext中,并形成新的properties对象返回,用于替换占位符
      Properties newVariablesContext = getVariablesContext(source, variablesContext);
      if (!newVariablesContext.isEmpty()) {
        // merge contexts
        fullContext = new Properties();
        fullContext.putAll(variablesContext);
        fullContext.putAll(newVariablesContext);
      } else {
        // no new context - use inherited fully
        fullContext = variablesContext;
      }
        //递归处理include节点,在sql节点中可能会使用include引用了其他sql片段
      applyIncludes(toInclude, fullContext);
      if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
        toInclude = source.getOwnerDocument().importNode(toInclude, true);
      }
        //将include节点替换成sql节点
      source.getParentNode().replaceChild(toInclude, source);
        //将
      while (toInclude.hasChildNodes()) {
          //将sql节点的子节点添加到sql节点前面
        toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
      }
        //删除sql节点
      toInclude.getParentNode().removeChild(toInclude);
    } else if (source.getNodeType() == Node.ELEMENT_NODE) {
      NodeList children = source.getChildNodes();
      for (int i=0; i<children.getLength(); i++) {
        applyIncludes(children.item(i), variablesContext);
      }
    } else if (source.getNodeType() == Node.ATTRIBUTE_NODE && !variablesContext.isEmpty()) {
      // replace variables in all attribute values
      source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
    } else if (source.getNodeType() == Node.TEXT_NODE && !variablesContext.isEmpty()) {
      // replace variables ins all text nodes
      source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
    }
  }

}
```

##### 解析selectKey节点

```java
private void processSelectKeyNodes(String id, Class<?> parameterTypeClass, LanguageDriver langDriver) {
    //获取全部的selectKey节点
    List<XNode> selectKeyNodes = context.evalNodes("selectKey");
    if (configuration.getDatabaseId() != null) {
      parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, configuration.getDatabaseId());
    }
    parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, null);
    //移除selectKey节点
    removeSelectKeyNodes(selectKeyNodes);
  }

 private void parseSelectKeyNodes(String parentId, List<XNode> list, Class<?> parameterTypeClass, LanguageDriver langDriver, String skRequiredDatabaseId) {
    for (XNode nodeToHandle : list) {
      String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
      String databaseId = nodeToHandle.getStringAttribute("databaseId");
      if (databaseIdMatchesCurrent(id, databaseId, skRequiredDatabaseId)) {
        parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, langDriver, databaseId);
      }
    }
  }

  private void parseSelectKeyNode(String id, XNode nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver, String databaseId) {
    String resultType = nodeToHandle.getStringAttribute("resultType");
    Class<?> resultTypeClass = resolveClass(resultType);
    StatementType statementType = StatementType.valueOf(nodeToHandle.getStringAttribute("statementType", StatementType.PREPARED.toString()));
    String keyProperty = nodeToHandle.getStringAttribute("keyProperty");
    String keyColumn = nodeToHandle.getStringAttribute("keyColumn");
    boolean executeBefore = "BEFORE".equals(nodeToHandle.getStringAttribute("order", "AFTER"));

    //defaults
    boolean useCache = false;
    boolean resultOrdered = false;
    KeyGenerator keyGenerator = new NoKeyGenerator();
    Integer fetchSize = null;
    Integer timeout = null;
    boolean flushCache = false;
    String parameterMap = null;
    String resultMap = null;
    ResultSetType resultSetTypeEnum = null;
    //通过 LanguageDriver.createSqlSource()方法生成SqlSource
    SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
      //selectKey节点中只能配置select语句
    SqlCommandType sqlCommandType = SqlCommandType.SELECT;
    //通过MapperBuilderAssistant 创建MappedStatement对象,并添加到Configuration.mappedStatements集合中保存,该集合为StrictMap<MappedStatement>
    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
        fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
        resultSetTypeEnum, flushCache, useCache, resultOrdered,
        keyGenerator, keyProperty, keyColumn, databaseId, langDriver, null);

    id = builderAssistant.applyCurrentNamespace(id, false);
    
    MappedStatement keyStatement = configuration.getMappedStatement(id, false);
      //创建selectKey节点对应的keyGenerator,添加到Configuration.keyGenerators集合中保存
      //Configuration.keyGenerators字段是 StrictMap<KeyGenerators>类型的对象
    configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
  }
```

##### langDriver.createSqlSource()创建sqlSource

接口langDriver 有两个实现类 `RawLanguageDriver`,`XMLLanguageDriver`,Configuration中默认的实现为:

```
languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
```

`XMLLanguageDriver.createSqlSource()`

```java
 @Override
  public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
      //创建 XMLScriptBuilder
    XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
    return builder.parseScriptNode();
  }
```

`XMLScriptBuilder`代码:

```java
public class XMLScriptBuilder extends BaseBuilder {
    
    public SqlSource parseScriptNode() {
        List<SqlNode> contents = parseDynamicTags(context);
        MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
        SqlSource sqlSource = null;
        if (isDynamic) {
            //动态sql
          sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
        } else {
          sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
        }
        return sqlSource;
   }

    List<SqlNode> parseDynamicTags(XNode node) {
        //记录sqlnode集合
        List<SqlNode> contents = new ArrayList<SqlNode>();
        //获取selectKey的
        NodeList children = node.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          XNode child = node.newXNode(children.item(i));
          if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
            String data = child.getStringBody("");
            TextSqlNode textSqlNode = new TextSqlNode(data);
            if (textSqlNode.isDynamic()) {
              contents.add(textSqlNode);
              isDynamic = true;
            } else {
              contents.add(new StaticTextSqlNode(data));
            }
          } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) { // issue #628
            String nodeName = child.getNode().getNodeName();
            NodeHandler handler = nodeHandlers(nodeName);
            if (handler == null) {
              throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
            }
            handler.handleNode(child, contents);
            isDynamic = true;
          }
        }
        return contents;
  }
}
```

#### 绑定mapper

```java
private void bindMapperForNamespace() {
    //获取映射文件的命名空间
    String namespace = builderAssistant.getCurrentNamespace();
    if (namespace != null) {
      Class<?> boundType = null;
      try {
          //解析命名空间对应的类型
        boundType = Resources.classForName(namespace);
      } catch (ClassNotFoundException e) {
        //ignore, bound type is not required
      }
      if (boundType != null) {
          //是否加载过
        if (!configuration.hasMapper(boundType)) {
          // Spring may not know the real resource name so we set a flag
          // to prevent loading again this resource from the mapper interface
          // look at MapperAnnotationBuilder#loadXmlResource
            //添加前缀
          configuration.addLoadedResource("namespace:" + namespace);
            //注册接口
          configuration.addMapper(boundType);
        }
      }
    }
  }
```

`configuration.addMapper(boundType)`会调用`MapperRegistry.addMapper(Class<T> type)`方法:

```java
public class MapperRegistry {

  private final Configuration config;
  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, MapperProxyFactory<?>>();
    
    public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) {
      if (hasMapper(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
        knownMappers.put(type, new MapperProxyFactory<T>(type));
        // It's important that the type is added before the parser is run
        // otherwise the binding may automatically be attempted by the
        // mapper parser. If the type is already known, it won't try.
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
          //调用parse方法,解析祖国
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

MapperAnnotationBuilder 代码如下:

```java
public class MapperAnnotationBuilder {
    private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet<Class<? extends Annotation>>();
  private final Set<Class<? extends Annotation>> sqlProviderAnnotationTypes = new HashSet<Class<? extends Annotation>>();

  private Configuration configuration;
  private MapperBuilderAssistant assistant;
  private Class<?> type;
    //构造函数中设置 sqlAnnotationTypes 和 sqlProviderAnnotationTypes
    public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;

        sqlAnnotationTypes.add(Select.class);
        sqlAnnotationTypes.add(Insert.class);
        sqlAnnotationTypes.add(Update.class);
        sqlAnnotationTypes.add(Delete.class);

        sqlProviderAnnotationTypes.add(SelectProvider.class);
        sqlProviderAnnotationTypes.add(InsertProvider.class);
        sqlProviderAnnotationTypes.add(UpdateProvider.class);
        sqlProviderAnnotationTypes.add(DeleteProvider.class);
   }
    
    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            //加载映射文件
          loadXmlResource();
          configuration.addLoadedResource(resource);
          assistant.setCurrentNamespace(type.getName());
            //解析@CacheNamespace
          parseCache();
            //解析@CacheNamespaceRef
          parseCacheRef();
          Method[] methods = type.getMethods();
          for (Method method : methods) {
            try {
              // issue #237
              if (!method.isBridge()) {
                  //解析@SelectKey,@ResultMap 等注解,并创建MappedStatement对象
                parseStatement(method);
              }
            } catch (IncompleteElementException e) {
              configuration.addIncompleteMethod(new MethodResolver(this, method));
            }
          }
        }
        parsePendingMethods();
   }
    
    private void loadXmlResource() {
        // Spring may not know the real resource name so we check a flag
        // to prevent loading again a resource twice
        // this flag is set at XMLMapperBuilder#bindMapperForNamespace
        if (!configuration.isResourceLoaded("namespace:" + type.getName())) {
          String xmlResource = type.getName().replace('.', '/') + ".xml";
          InputStream inputStream = null;
          try {
            inputStream = Resources.getResourceAsStream(type.getClassLoader(), xmlResource);
          } catch (IOException e) {
            // ignore, resource is not required
          }
          if (inputStream != null) {
            XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource, configuration.getSqlFragments(), type.getName());
              //又使用XMLMapperBuilder.parse()
            xmlParser.parse();
          }
        }
    }
}
```

