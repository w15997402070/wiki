---
title: Spring
description: 
published: true
date: 2020-09-08T13:30:14.796Z
tags: 
editor: undefined
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