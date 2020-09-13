---
title: Spring事务源码
description: 
published: true
date: 2020-09-10T15:24:23.844Z
tags: spring, 源码, spring事务
editor: markdown
---

# Spring事务
## XML节点解析原理
### XML中事务配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="  
           http://www.springframework.org/schema/beans  
           http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
           http://www.springframework.org/schema/tx  
           http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
           http://www.springframework.org/schema/aop  
           http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
           http://www.springframework.org/schema/context  
           http://www.springframework.org/schema/context/spring-context-3.0.xsd">
  <!-- 使用annotation注解方式配置事务 -->
    <tx:annotation-driven transaction-manager="txManager" />
	<!-- 配置数据源 -->
	<bean id="dataSource"
		class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://localhost:3306/test" />
		<property name="username" value="root" />
		<property name="password" value="christmas258@" />
	</bean>
	<!-- 声明事务管理器 -->
	<bean id="txManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>
	<!-- 使用tx/aop来配置 -->
	<aop:config>
		<!-- 通过aop定义事务增强切面 -->
		<aop:pointcut id="aopMethod"
			expression="execution(* com.test.dao.*(..))" />
		<!-- 引用事务增强 -->
		<aop:advisor pointcut-ref="aopMethod" advice-ref="txAdvice" />
	</aop:config>
 
	<!--事务增强 -->
	<tx:advice id="txAdvice" transaction-manager="txManager">
		<!-- 事务属性定义 -->
		<tx:attributes>
			<tx:method name="get*" read-only="false" />
			<tx:method name="add*" rollback-for="Exception" />
			<tx:method name="del*" />
		</tx:attributes>
	</tx:advice>
</beans>
```

主要是 
`xmlns:tx="http://www.springframework.org/schema/tx`
`http://www.springframework.org/schema/tx`
`<tx:annotation-driven transaction-manager="txManager" />`

### XML读取源码

XML解析`<tx:annotation-driven transaction-manager="txManager" />`标签的入口
`DefaultBeanDefinitionDocumentReader # parseBeanDefinitions()`的
`delegate.parseCustomElement(ele)`方法中会进入解析自定义标签的代码

`BeanDefinitionParserDelegate # parseCustomElement()`
```java
  @Nullable
	public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
		// 1.拿到节点ele的命名空间，例如常见的:
		// <context> 节点对应命名空间: http://www.springframework.org/schema/context
		// <aop> 节点对应命名空间: http://www.springframework.org/schema/aop
		// 事务中就是  http://www.springframework.org/schema/tx
		String namespaceUri = getNamespaceURI(ele);
		if (namespaceUri == null) {
			return null;
		}
		// 2.拿到命名空间对应的的handler, 例如：http://www.springframework.org/schema/context 对应 ContextNameSpaceHandler
		// 2.1 getNamespaceHandlerResolver: 拿到namespaceHandlerResolver
		// 2.2 resolve: 使用namespaceHandlerResolver解析namespaceUri, 拿到namespaceUri对应的NamespaceHandler
		// 这里拿到  org.springframework.transaction.config.TxNamespaceHandler
		NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
		if (handler == null) {
			error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
			return null;
		}
		// 
		return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
	}
```
先看 `DefaultNamespaceHandlerResolver # resolve()`
```java
  @Override
	@Nullable
	public NamespaceHandler resolve(String namespaceUri) {
		// 获取默认的和自己定义的, 例如 : key : http://www.springframework.org/schema/tx,
		// value : org.springframework.transaction.config.TxNamespaceHandler
		// 
		Map<String, Object> handlerMappings = getHandlerMappings();
		// 这个Object就是handler的类路径
		Object handlerOrClassName = handlerMappings.get(namespaceUri);
		if (handlerOrClassName == null) {
			return null;
		}
		else if (handlerOrClassName instanceof NamespaceHandler) {
			return (NamespaceHandler) handlerOrClassName;
		}
		else {
			String className = (String) handlerOrClassName;
			try {
				Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
				if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
					throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri +
							"] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
				}
				//实例化
				NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
				//初始化,这个方法就会调用 TxNamespaceHandler # init() 方法,注册BeanDefinitionParser
				namespaceHandler.init();
				//将实例化的handler放到缓存，替换原来的className
				//原来为: namespaceUri -> handler的className，会被覆盖成: namespaceUri -> 实例化的handler
				handlerMappings.put(namespaceUri, namespaceHandler);
				return namespaceHandler;
			}
			catch (ClassNotFoundException ex) {
				throw new FatalBeanException("Could not find NamespaceHandler class [" + className +
						"] for namespace [" + namespaceUri + "]", ex);
			}
			catch (LinkageError err) {
				throw new FatalBeanException("Unresolvable class definition for NamespaceHandler class [" +
						className + "] for namespace [" + namespaceUri + "]", err);
			}
		}
	}
```

可以看到 `namespaceHandler.init()`就会执行对应 `namespaceHandler`的init方法
这个方法就会把解析器注册进去
`TxNamespaceHandler # init()`
```java
@Override
public void init() {
    // 注册 advice 命名解析器
	registerBeanDefinitionParser("advice", new TxAdviceBeanDefinitionParser());
	// 注册 annotation-driven 命名解析器 也就是扫描注解驱动的事务
	registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
	// 注册 jta-transaction-manager 分布式事务管理解析器
	registerBeanDefinitionParser("jta-transaction-manager", new JtaTransactionManagerBeanDefinitionParser());
}
```
`parseCustomElement` 方法中
`handler.parse(ele, new ParserContext(this.readerContext, this, containingBd))`就会调用对应的handler也就是 `TxNamespaceHandler`的`parse()`方法

`TxNamespaceHandler`会调用父类`NamespaceHandlerSupport`的`parse()`方法
`NamespaceHandlerSupport # 	parse() `
```java
  @Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionParser parser = findParserForElement(element, parserContext);
		return (parser != null ? parser.parse(element, parserContext) : null);
	}
  
  @Nullable
	private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
	    // localName = annotation-driven 
		String localName = parserContext.getDelegate().getLocalName(element);
		// parser -> AnnotationDrivenBeanDefinitionParser
		BeanDefinitionParser parser = this.parsers.get(localName);
		if (parser == null) {
			parserContext.getReaderContext().fatal(
					"Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
		}
		return parser;
	}
```

可以看到 `parser`就是 `TxNamespaceHandler # init()`中注册的`AnnotationDrivenBeanDefinitionParser`类,所以会调用
`AnnotationDrivenBeanDefinitionParser # parse()`

### 事务源码解析
上面看到最终解析时会到`AnnotationDrivenBeanDefinitionParser # parse()`,看一下这个方法做了什么
```java
  @Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		registerTransactionalEventListenerFactory(parserContext);
		String mode = element.getAttribute("mode");
		if ("aspectj".equals(mode)) {
			// mode="aspectj"
			registerTransactionAspect(element, parserContext);
			if (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader())) {
				registerJtaTransactionAspect(element, parserContext);
			}
		}
		else {
			// mode="proxy"
			AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
		}
		return null;
	}
```

## 事务相关类

* TransactionStatus

    ```java
    public interface TransactionStatus extends SavepointManager, Flushable {
    
    	boolean isNewTransaction();
    
    	boolean hasSavepoint();
    
    	void setRollbackOnly();
    
    	boolean isRollbackOnly();
    
    	@Override
    	void flush();
    
    	boolean isCompleted();
    
    }
    ```

    

* 