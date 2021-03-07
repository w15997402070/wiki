---
title: spring技术内幕
description: 
published: true
date: 2020-09-16T15:09:37.851Z
tags: 
editor: markdown
---

# Spring源码解析

## Bean生命周期
> 实例化Bean对象 -> 设置对象属性 -> 检查Aware相关接口并设置相关依赖 -> BeanPostProcessor前置处理 -> 检查是否实现了initializingBean -> 检查是否配置了init-method方法 -> BeanPostProcessor后置处理 -> 注册必要的相关回调接口 -> 使用 -> 检查是否实现DisposableBean接口 -> 是否配置有自定义的destroy方法
{.is-info}

总结：

**（1）实例化Bean：**

对于BeanFactory容器，当客户向容器请求一个尚未初始化的bean时，或初始化bean的时候需要注入另一个尚未初始化的依赖时，容器就会调用createBean进行实例化。对于ApplicationContext容器，当容器启动结束后，通过获取BeanDefinition对象中的信息，实例化所有的bean。

**（2）设置对象属性（依赖注入）：**

实例化后的对象被封装在BeanWrapper对象中，紧接着，Spring根据BeanDefinition中的信息 以及 通过BeanWrapper提供的设置属性的接口完成依赖注入。

**（3）处理Aware接口：**

接着，Spring会检测该对象是否实现了xxxAware接口，并将相关的xxxAware实例注入给Bean：

①如果这个Bean已经实现了BeanNameAware接口，会调用它实现的setBeanName(String beanId)方法，此处传递的就是Spring配置文件中Bean的id值；

②如果这个Bean已经实现了BeanFactoryAware接口，会调用它实现的setBeanFactory()方法，传递的是Spring工厂自身。

③如果这个Bean已经实现了ApplicationContextAware接口，会调用setApplicationContext(ApplicationContext)方法，传入Spring上下文；

**（4）BeanPostProcessor：**

如果想对Bean进行一些自定义的处理，那么可以让Bean实现了BeanPostProcessor接口，那将会调用postProcessBeforeInitialization(Object obj, String s)方法。

**（5）InitializingBean 与 init-method：**

如果Bean在Spring配置文件中配置了 init-method 属性，则会自动调用其配置的初始化方法。

**（6）如果这个Bean实现了BeanPostProcessor接口**，将会调用postProcessAfterInitialization(Object obj, String s)方法；由于这个方法是在Bean初始化结束时调用的，所以可以被应用于内存或缓存技术；

以上几个步骤完成后，Bean就已经被正确创建了，之后就可以使用这个Bean了。

**（7）DisposableBean：**

当Bean不再需要时，会经过清理阶段，如果Bean实现了DisposableBean这个接口，会调用其实现的destroy()方法；

**（8）destroy-method：**

最后，如果这个Bean的Spring配置中配置了destroy-method属性，会自动调用其配置的销毁方法。

## Spring IOC

### 注入方式

1. 接口注入
2. setter注入
3. 构造器注入

## 设计与实现

### BeanFactory

BeanDefinition: BeanDefinition来管理基于Spring的应用中的各种对象以及它们之间的相互依赖关系

### ApplicationContext

```text
MessageSource 支持国际化的实现，为开发多语言版本的应用提供服务
ResourcePatternResolver extends ResourceLoader 可以从不同地方得到Bean定义资源
ApplicationEventPublisher 事件机制。这些事件和Bean的生命周期的结合为Bean的管理提供了便利
```

```
FileSystemXmlApplicationContext 继承 AbstractXmlApplicationContext

//主要在这个方法的 refresh()方法中
public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        this.setConfigLocations(configLocations);
        if (refresh) {
            this.refresh();
        }
    }

```

### 问题?





