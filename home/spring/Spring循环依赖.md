---
title: Spring循环依赖
description: 
published: true
date: 2020-09-16T13:52:00.016Z
tags: spring, 循环依赖
editor: markdown
---

# Spring循环依赖

## 构造器循环依赖
Spring没有办法解决通过构造及注入构成的循环依赖，抛出BeanCurrentlylnCreationException 异常表示循环依赖
## 不是单例模式的循环依赖
Spring也没有办法解决这种循环依赖
## setter循环依赖和属性循环依赖

Spring通过三级缓存来解决这些循环依赖:
* 第一级缓存singletonObjects里面放置的是实例化好的单例对象。
* 第二级earlySingletonObjects里面存放的是提前曝光的单例对象（没有完全装配好）。
* 第三级singletonFactories里面存放的是要被实例化的对象的对象工厂。



