---
title: Spring中类解析
description: 
published: true
date: 2020-09-16T15:26:49.453Z
tags: 
editor: markdown
---

### 类解析

[toc]

## ImportBeanDefinitionRegistrar

​	自己手动的将bean注入到spring容器中就可以实现这个接口来实现,需要配合@Import 和 @Configuration

例如 : 

创建自定义注解:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ImportRegister.class})
public @interface ImportAnnotationTest {
}
```

创建`ImportRegister`类实现`ImportBeanDefinitionRegistrar`接口,上面`@Import`就引用这个类

```java
public class ImportRegister implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

        System.out.println("importingClassMetadata  "+importingClassMetadata.getClassName());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    }
}
```

创建一个带配置类

```java
@Configuration
@ImportAnnotationTest
public class ImportAnnotationBean {
}
```

启动项目,上面就会打印出这个类的名称

具体项目中可以在打印的地方处理相关逻辑

Mybatis和spring整合就是这样的.

从mybatis-spring`源码中看

首先看`mybatis-spring`中`@MapperScans`和`@MapperScan`源码

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MapperScannerRegistrar.class})
@Repeatable(MapperScans.class)
public @interface MapperScan {
   
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RepeatingRegistrar.class})
public @interface MapperScans {
    MapperScan[] value();
}
```

在`@Import`注解中引入了`MapperScannerRegistrar`类

再看`MapperScannerRegistrar`类源码

```java
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    public MapperScannerRegistrar() {
    }

    /** @deprecated */
    @Deprecated
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //1. MapperScan 注解属性值的map,key位注解名称
        
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        if (mapperScanAttrs != null) {
            //2. 生成基础的beanName
            //3. 注册bean
            this.registerBeanDefinitions(importingClassMetadata, mapperScanAttrs, registry, generateBaseBeanName(importingClassMetadata, 0));
        }

    }

    void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue("processPropertyPlaceHolders", true);
        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            builder.addPropertyValue("annotationClass", annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            builder.addPropertyValue("markerInterface", markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            builder.addPropertyValue("mapperFactoryBeanClass", mapperFactoryBeanClass);
        }

        String sqlSessionTemplateRef = annoAttrs.getString("sqlSessionTemplateRef");
        if (StringUtils.hasText(sqlSessionTemplateRef)) {
            builder.addPropertyValue("sqlSessionTemplateBeanName", annoAttrs.getString("sqlSessionTemplateRef"));
        }

        String sqlSessionFactoryRef = annoAttrs.getString("sqlSessionFactoryRef");
        if (StringUtils.hasText(sqlSessionFactoryRef)) {
            builder.addPropertyValue("sqlSessionFactoryBeanName", annoAttrs.getString("sqlSessionFactoryRef"));
        }

        List<String> basePackages = new ArrayList();
        basePackages.addAll((Collection)Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
        basePackages.addAll((Collection)Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
        basePackages.addAll((Collection)Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
        if (basePackages.isEmpty()) {
            basePackages.add(getDefaultBasePackage(annoMeta));
        }

        String lazyInitialization = annoAttrs.getString("lazyInitialization");
        if (StringUtils.hasText(lazyInitialization)) {
            builder.addPropertyValue("lazyInitialization", lazyInitialization);
        }

        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
        // registry -> DefaultListableBeanFactory
        // builder.getBeanDefinition() -> GenericBeanDefinition
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }
    /*
    * importingClassMetadata @MapperScan 所在的类名,例如放在启动类上,这就是启动类的全路径名称
    * index 默认为0
    * 
    * return 所以返回的就是com.test.TestApplication#MapperScannerRegistrar#0
    */
    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + MapperScannerRegistrar.class.getSimpleName() + "#" + index;
    }

    private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        return ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }

    static class RepeatingRegistrar extends MapperScannerRegistrar {
        RepeatingRegistrar() {
        }

        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            AnnotationAttributes mapperScansAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScans.class.getName()));
            if (mapperScansAttrs != null) {
                AnnotationAttributes[] annotations = mapperScansAttrs.getAnnotationArray("value");

                for(int i = 0; i < annotations.length; ++i) {
                    this.registerBeanDefinitions(importingClassMetadata, annotations[i], registry, MapperScannerRegistrar.generateBaseBeanName(importingClassMetadata, i));
                }
            }

        }
    }
}
```

实现了`ImportBeanDefinitionRegistrar`,`ResourceLoaderAware`两个接口

1. 首先获取`MapperScan`注解的属性值的map
2. `generateBaseBeanName`方法生成beanName



## MapperScannerConfigurer

`org.mybatis.spring.mapper.MapperScannerConfigurer`扫描包

## AbstractBeanDefinition

## InitializingBean
实现该接口的类,在这个Bean属性初始化之后的会调用`afterPropertiesSet()`处理方法,一般可以在Bean初始化完之后做属性验证
```java
public interface InitializingBean {

	void afterPropertiesSet() throws Exception;

}
```

例如上面`MapperScannerConfigurer`实现了`InitializingBean接口,在初始化完这个类后会验证basePackage
```java
  // MapperScannerConfigurer # afterPropertiesSet()
  @Override
  public void afterPropertiesSet() throws Exception {
    notNull(this.basePackage, "Property 'basePackage' is required");
  }
	
  // Assert # notNull()
  public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
```

## BeanPostProcessor

```java
public interface BeanPostProcessor {

	/**
	 * 实例化、依赖注入完毕，在调用显示的初始化之前完成一些定制的初始化任务
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 实例化、依赖注入、初始化完毕时执行
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```

## BeanFactoryPostProcessor
BeanFactoryPostProcessor：允许自定义对ApplicationContext的 bean definitions 进行修饰，扩展功能。
```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```

`BeanPostProcessor` 和 `BeanFactoryPostProcessor` 具体参考文章
https://www.cnblogs.com/duanxz/p/3750725.html

## FactoryBean



## ImportSelector

ImportSelector接口是至spring中导入外部配置的核心接口，在SpringBoot的自动化配置和@EnableXXX(功能性注解)都有它的存在.

该接口文档上说的明明白白，其主要作用是收集需要导入的配置类，selectImports()方法的返回值就是我们向Spring容器中导入的类的全类名。如果该接口的实现类同时实现EnvironmentAware， BeanFactoryAware  ，BeanClassLoaderAware或者ResourceLoaderAware，那么在调用其selectImports方法之前先调用上述接口中对应的方法，如果需要在所有的@Configuration处理完在导入时可以实现`DeferredImportSelector`接口。

```java
public interface ImportSelector {

	String[] selectImports(AnnotationMetadata importingClassMetadata);

	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}

}
```

