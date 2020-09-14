# Mybatis插件解析

以`PageHelper`插件为例 : 

默认项目中已配置好插件,也就是 `Configuration`中已经从配置文件中设置 插件`interceptorChain.addInterceptor(interceptor);`

插件初始化 : 

```java
package org.apache.ibatis.builder.xml;

public class XMLConfigBuilder extends BaseBuilder {

    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
          for (XNode child : parent.getChildren()) {
            String interceptor = child.getStringAttribute("interceptor");
            Properties properties = child.getChildrenAsProperties();
            Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
            interceptorInstance.setProperties(properties);
              //这里调用 interceptorChain.addInterceptor(interceptor)
            configuration.addInterceptor(interceptorInstance);
          }
        }
      }
    
}
```



PageHelper 代码 :

```java
package com.github.pagehelper;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mybatis - 通用分页拦截器
 *
 * @author liuzh/abel533/isea533
 * @version 3.3.0
 *          项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 */
@SuppressWarnings("rawtypes")
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PageHelper implements Interceptor {
    //其他方法省略
    ....
    /**
     * Mybatis拦截器方法
     *
     * @param invocation 拦截器入参
     * @return 返回执行结果
     * @throws Throwable 抛出异常
     */
    public Object intercept(Invocation invocation) throws Throwable {
        if (autoRuntimeDialect) {
            SqlUtil sqlUtil = getSqlUtil(invocation);
            return sqlUtil.processPage(invocation);
        } else {
            if (autoDialect) {
                initSqlUtil(invocation);
            }
            return sqlUtil.processPage(invocation);
        }
    }

    /**
     * 只拦截Executor
     *
     * @param target
     * @return
     */
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

   /**
     * 设置属性值
     *
     * @param p 属性值
     */
    public void setProperties(Properties p) {
        checkVersion();
        //多数据源时，获取jdbcurl后是否关闭数据源
        String closeConn = p.getProperty("closeConn");
        //解决#97
        if(StringUtil.isNotEmpty(closeConn)){
            this.closeConn = Boolean.parseBoolean(closeConn);
        }
        //初始化SqlUtil的PARAMS
        SqlUtil.setParams(p.getProperty("params"));
        //数据库方言
        String dialect = p.getProperty("dialect");
        String runtimeDialect = p.getProperty("autoRuntimeDialect");
        if (StringUtil.isNotEmpty(runtimeDialect) && runtimeDialect.equalsIgnoreCase("TRUE")) {
            this.autoRuntimeDialect = true;
            this.autoDialect = false;
            this.properties = p;
        } else if (StringUtil.isEmpty(dialect)) {
            autoDialect = true;
            this.properties = p;
        } else {
            autoDialect = false;
            sqlUtil = new SqlUtil(dialect);
            sqlUtil.setProperties(p);
        }
    }
}

```

InterceptorChain 代码 : 

```java
public class InterceptorChain {
 //这里已经加载 PageHelper
  private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }
  
  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
```

`org.apache.ibatis.plugin.InterceptorChain`的`interceptors`集合中已经有`PageHelper`插件

Configuration 加载 Executor 时会调用插件 : 

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
    //主要在这一行,调用InterceptorChain的pluginAll方法
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }
```

然后执行 `InterceptorChain.pluginAll(Object target)`方法 : 

```java
 public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
        //这个 interceptor 就是 PageHelper
      target = interceptor.plugin(target);
    }
    return target;
  }
```

再调用`PageHelper.plugin(Object target)`方法 :

```java
 /**
     * 只拦截Executor
     *
     * @param target
     * @return
     */
    public Object plugin(Object target) {
        //这里做判断是 Executor就代理
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
```

`Plugin`类代码 : 

```java
package org.apache.ibatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * @author Clinton Begin
 */
public class Plugin implements InvocationHandler {

  private Object target;
  private Interceptor interceptor;
  private Map<Class<?>, Set<Method>> signatureMap;

  private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
    this.target = target;
    this.interceptor = interceptor;
    this.signatureMap = signatureMap;
  }

  public static Object wrap(Object target, Interceptor interceptor) {
    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
    Class<?> type = target.getClass();
    Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
    if (interfaces.length > 0) {
        //这里返回代理
      return Proxy.newProxyInstance(
          type.getClassLoader(),
          interfaces,
          new Plugin(target, interceptor, signatureMap));
    }
    return target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
  //解析注解
  private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
    //@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
    Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
    // issue #251
    if (interceptsAnnotation == null) {
      throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());      
    }
      //@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
      //@Signature 可以传多个
    Signature[] sigs = interceptsAnnotation.value();
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<Class<?>, Set<Method>>();
    for (Signature sig : sigs) {
      Set<Method> methods = signatureMap.get(sig.type());
      if (methods == null) {
        methods = new HashSet<Method>();
          //type = Executor.class
        signatureMap.put(sig.type(), methods);
      }
      try {
          //method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        Method method = sig.type().getMethod(sig.method(), sig.args());
        methods.add(method);
      } catch (NoSuchMethodException e) {
        throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
      }
    }
    return signatureMap;
  }

  private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
    Set<Class<?>> interfaces = new HashSet<Class<?>>();
    while (type != null) {
      for (Class<?> c : type.getInterfaces()) {
        if (signatureMap.containsKey(c)) {
          interfaces.add(c);
        }
      }
      type = type.getSuperclass();
    }
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

}
```

