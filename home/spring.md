---
title: Spring
description: 
published: true
date: 2020-09-09T15:10:35.075Z
tags: spring
editor: markdown
---

# Spring

## Spring配置文件读取

### ClassPathXmlApplicationContext 和 ClassPathResource 读取区别
#### 使用 ClassPathXmlApplicationContext读取配置文件
ClassPathXmlApplicationContext 和 FileSystemXmlApplicationContext 都继承自AbstractXmlApplicationContext
ClassPathXmlApplicationContext从项目类路径读取配置文件
FileSystemXmlApplicationContext 从文件系统读取文件
```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
Object bean = applicationContext.getBean("userDao"); 
ApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext("file:d:/config/applicationContext.xml");
Object bean = fileSystemXmlApplicationContext.getBean("userDao"); 
```
ApplicationContext 是一站式便利的配置文件读取方式,在测试中使用方便,考虑将GenericApplicationContext类与XmlBeanDefinitionReader结合使用，以实现更灵活的上下文设置。

#### ClassPathResource读取方式

ClassPathResource 配合 XmlBeanDefinitionReader,  使用给定的ClassLoader或给定的类加载资源。配置更灵活

```java
ClassPathResource classPathResource = new ClassPathResource("");
DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(defaultListableBeanFactory);
xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);
```

## ClassPathXmlApplicationContext 配置文件读取流程

```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:daos.xml");
        Object bean = applicationContext.getBean("projectServiceImpl");
```

### 1. 构造函数最终会到这里
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
### 2. ClassPathXmlApplicationContext 会调用父类的 refresh()方法
`AbstractApplicationContext # refresh()`

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
		}

		catch (BeansException ex) {
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
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}
```

#### 2.1 prepareRefresh();

#### 2.2 obtainFreshBeanFactory() -> `AbstractApplicationContext # obtainFreshBeanFactory() `
2.2.1 refreshBeanFactory() -> `AbstractRefreshableApplicationContext # refreshBeanFactory()`
2.2.2 loadBeanDefinitions()是 AbstractRefreshableApplicationContext 的抽象方法 ->  `AbstractXmlApplicationContext # loadBeanDefinitions(DefaultListableBeanFactory beanFactory)`
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

#### 2.3 prepareBeanFactory(beanFactory) 






# Spring接口

## BeanFactory
## NamespaceHandler










