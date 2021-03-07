# Spring源码

## xml启动源码

1. 循环依赖 
2. 注入方式
3. bean生命周期

`ClassPathXmlApplicationContext` 读取配置文件解析

`ApplicationContext applicationContext = new ClassPathXmlApplicationContext("");
Object bean = applicationContext.getBean("");`

1. 构造函数最终会到这里

  ```java
  public ClassPathXmlApplicationContext(
  		String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
  		throws BeansException {
       //调用父类构造方法
  	super(parent);
  	// 设置配置文件路径
  	setConfigLocations(configLocations);
  	if (refresh) {
  	// 主要操作在这个方法
  		refresh();
  	}
  }
  ```

2. ClassPathXmlApplicationContext 会调用父类的 refresh()方法
    AbstractApplicationContext # refresh()
  ```java
  @Override
  public void refresh() throws BeansException, IllegalStateException {
      synchronized (this.startupShutdownMonitor) {
  	//1 这里包含给 ${} 上下文中这种表达式赋值
  	prepareRefresh();

  	//2 初始化BeanFactory 这一步之后，ClassPathXmlApplicationContext实际上就已经包含了BeanFactory所提供的功能，也就是可以进行Bean的提取等基础操作了。
  	ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
  	 //设置@Autowired和 @Qualifier注解解析器QualifierAnnotationAutowireCandidateResolver
  	//3 Prepare the bean factory for use in this context.
  	prepareBeanFactory(beanFactory);
  	try {
  		//4 子类覆盖方法做额外的处理,提供了一个空的函数实现postProcessBeanFactory来方便程序员在业务上做进一步扩展。
  		postProcessBeanFactory(beanFactory);
  		//5 激活各种BeanFactory处理器
  		invokeBeanFactoryPostProcessors(beanFactory);
  		//6 注册拦截Bean创建的Bean处理器,这里只是注册，真正的调用是在getBean时候
  		registerBeanPostProcessors(beanFactory);
  		//7 为上下文初始化Message源，即不同语言的消息体进行国际化处理
  		initMessageSource();
  		//8 初始化应用消息广播器，并放入“applicationEventMulticaster”bean中
  		initApplicationEventMulticaster();
  		//9 留给子类来初始化其它的Bean
  		onRefresh();
  		//10 在所有注册的bean中查找Listener bean，注册到消息广播器中
  		registerListeners();
  		//11 初始化剩下的单实例（非惰性的）
  		finishBeanFactoryInitialization(beanFactory);
  		//12 完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出ContextRefreshEvent通知别人
  		finishRefresh();
  	}catch (BeansException ex) {
  		if (logger.isWarnEnabled()) {
  			logger.warn("Exception encountered during context initialization - " +
  					"cancelling refresh attempt: " + ex);
  		}
  		// Destroy already created singletons to avoid dangling resources.
  		destroyBeans();
  		// Reset 'active' flag.
  		cancelRefresh(ex);
  		// Propagate exception to caller.
  		throw ex;
  	}finally {
  		// Reset common introspection caches in Spring's core, since we
  		// might not ever need metadata for singleton beans anymore...
  		resetCommonCaches();
  	}
  }
  ```
2.1 prepareRefresh();
  2.2 obtainFreshBeanFactory()-> AbstractApplicationContext # obtainFreshBeanFactory()
      2.2.1 refreshBeanFactory() -> AbstractRefreshableApplicationContext # refreshBeanFactory()
	  2.2.2 loadBeanDefinitions()是 AbstractRefreshableApplicationContext 的抽象方法 ->  AbstractXmlApplicationContext # loadBeanDefinitions(DefaultListableBeanFactory beanFactory)

```java
  @Override
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
    // Create a new XmlBeanDefinitionReader for the given BeanFactory.
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    // Configure the bean definition reader with this context's
    // resource loading environment.
    beanDefinitionReader.setEnvironment(this.getEnvironment());
    beanDefinitionReader.setResourceLoader(this);
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

    // Allow a subclass to provide custom initialization of the reader,
    // then proceed with actually loading the bean definitions.
    initBeanDefinitionReader(beanDefinitionReader);
    loadBeanDefinitions(beanDefinitionReader);
}
		
```

这个方法里面就和下面的代码一样了
		

```java
DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(defaultListableBeanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);
```

2.3 prepareBeanFactory(beanFactory)  

## DI(依赖注入)

上一步解析之后Bean注入到容器当中,但是还没有正在的初始化
依赖注入发生的时间
IOC容器完成Bean定义资源的位,载入和解析注册以后,IOC容器中已经管理类Bean定义的相关数据
但是此时IOC容器还没有对所管理的Bean进行依赖注入,依赖注入在一下两种情况发生:
1. 用户第一次通过getBean()方法向IOC容器获取Bean的时候,IOC容器触发依赖注入
2. 当用户在Bean定义资源中为<bean>元素配置了lazy-init属性,即让容器在解析注册Bean定义时进行预实例化,触发依赖注入

获取Bean的方法主要方法在 AbstractBeanFactory # doGetBean()


## Spring中的接口
1. NamespaceHandler
NamespaceHandler 是 Spring 提供的 命名空间处理器。

<tx:annotation-driven transaction-manager="transactionManager"/>
http\://www.springframework.org/schema/tx=org.springframework.transaction.config.TxNamespaceHandler

handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
TxNamespaceHandler 继承 NamespaceHandlerSupport > NamespaceHandlerSupport # 	parse() 


AnnotationDrivenBeanDefinitionParser # parse()

// TransactionalEventListenerFactory ?? 有什么用  
处理标记了@TransactionalEventListener注解的方法
2. EventListenerFactory 接口
对标注了@EventListener注解的方法创建ApplicationListener的策略接口 

3. ApplicationListener 接口
ApplicationListener 接口继承了java.util.EventListener,观察者模式

4. ApplicationEventMulticaster

// org.springframework.transaction.config.internalTransactionalEventListenerFactory
例如Spring事务管理中的 TxNamespaceHandler
在初始化方法中
InfrastructureAdvisorAutoProxyCreator 实现了InstantiationAwareBeanPostProcessor 接口
所以Bean实例化时会调用postProcessAfterInitialization方法
InfrastructureAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator extends AbstractAutoProxyCreator
 AbstractAutoProxyCreator # postProcessAfterInitialization(@Nullable Object bean, String beanName) -> wrapIfNecessary()
 这个方法创建Bean的代理
AspectJAwareAdvisorAutoProxyCreator
AnnotationAwareAspectJAutoProxyCreator


AnnotationTransactionAttributeSource
TransactionInterceptor
BeanFactoryTransactionAttributeSourceAdvisor 

TransactionAttributeSource

2. BeanDefinitionParser






​	  