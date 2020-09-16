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





