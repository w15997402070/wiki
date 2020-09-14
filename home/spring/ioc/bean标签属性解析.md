# Bean属性解析

## bean标签

BeanDefinitionParserDelegate.parseBeanDefinitionElement()源码

```java
public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName,
			@Nullable BeanDefinition containingBean, AbstractBeanDefinition bd) {

		if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
			error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
		}

		//解析scope属性
		else if (ele.hasAttribute(SCOPE_ATTRIBUTE)) {
			bd.setScope(ele.getAttribute(SCOPE_ATTRIBUTE));
		}
		else if (containingBean != null) {
			//在嵌入beanDefinition情况下且没有单独指定scope属性则使用父类默认的属性
			// Take default from containing bean in case of an inner bean definition.
			bd.setScope(containingBean.getScope());
		}

        //解析abstract属性
		if (ele.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			bd.setAbstract(TRUE_VALUE.equals(ele.getAttribute(ABSTRACT_ATTRIBUTE)));
		}

        //解析lazy-init属性
		String lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE);
		if (isDefaultValue(lazyInit)) {
			lazyInit = this.defaults.getLazyInit();
		}
		bd.setLazyInit(TRUE_VALUE.equals(lazyInit));

        //解析autowire属性 注入类型
		String autowire = ele.getAttribute(AUTOWIRE_ATTRIBUTE);
		bd.setAutowireMode(getAutowireMode(autowire));

        //解析depends-on属性 注解为 @DependsOn
		// 用于声明当前bean依赖于另外一个bean。所依赖的bean会被容器确保在当前bean实例化之前被实例化
		if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
			String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
			bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
		}

		// autowire-candidate 值为true或者false,表示是否自动注入,在一个接口多个实现时可以设置哪个不自动注入
        //解析autowire-candidate属性
		String autowireCandidate = ele.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
		if (isDefaultValue(autowireCandidate)) {
			String candidatePattern = this.defaults.getAutowireCandidates();
			if (candidatePattern != null) {
				String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
				bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
			}
		}
		else {
			bd.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
		}

        // 优先考虑，优先考虑被注入
        //解析primary属性 对应 @Primary
		if (ele.hasAttribute(PRIMARY_ATTRIBUTE)) {
			bd.setPrimary(TRUE_VALUE.equals(ele.getAttribute(PRIMARY_ATTRIBUTE)));
		}

		//定义spring 容器在初始化bean所做的操作
        //解析init-method属性
		if (ele.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
			String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
			bd.setInitMethodName(initMethodName);
		}
		else if (this.defaults.getInitMethod() != null) {
			bd.setInitMethodName(this.defaults.getInitMethod());
			bd.setEnforceInitMethod(false);
		}
        //定义spring 容器在容器销毁之前的所做的操作
		//解析 destroy-method属性
		if (ele.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
			String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
			bd.setDestroyMethodName(destroyMethodName);
		}
		else if (this.defaults.getDestroyMethod() != null) {
			bd.setDestroyMethodName(this.defaults.getDestroyMethod());
			bd.setEnforceDestroyMethod(false);
		}

		//解析factory-method属性
		if (ele.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
			bd.setFactoryMethodName(ele.getAttribute(FACTORY_METHOD_ATTRIBUTE));
		}

		//解析 factory-bean属性
		if (ele.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
			bd.setFactoryBeanName(ele.getAttribute(FACTORY_BEAN_ATTRIBUTE));
		}

		return bd;
	}
```

### init-method 和 destroy-method 属性

```java
public class BeanAttr {

    public BeanAttr(){
        System.out.println("构造方法方法");
    }
    public void testInitMethod(){
        System.out.println("init-method方法");
    }

    public void testDestroyMethod(){
        System.out.println("destroy-method方法");
    }
}
```

application-content.xml 文件中

```xml
<!--    对应的init和destroy方法-->
<bean id="beanAttr"
          class="com.spring.demo.bean.BeanAttr"
    init-method="testInitMethod"
    destroy-method="testDestroyMethod"></bean>
```

测试:

```java
public class TestBeanAttr {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("application-content.xml");
        BeanAttr beanAttr = (BeanAttr)applicationContext.getBean("beanAttr");
        System.out.println(beanAttr.toString());
        applicationContext.destroy();
    }
}
```

结果为:

```java
构造方法方法
init-method方法
com.spring.demo.bean.BeanAttr@6f195bc3
destroy-method方法
```

## 子标签:

### meta

<meta></meta>标签

作用?

### lookup-method

1. 创建父类

   ```java
   public class Person {
       public void showMe(){
           System.out.println("person");
       }
   }
   ```

2. 创建子类Student

   ```java
   public class Student extends Person {
       @Override
       public void showMe() {
           System.out.println("student");
       }
   }
   ```

3. 创建子类Teacher

   ```java
   public class Teacher extends Person {
       @Override
       public void showMe() {
           System.out.println("teacher");
       }
   }
   ```

4. 创建 LookupMethodTest ,这是一个抽象类

   ```java
   public abstract class LookupMethodTest {
       public void show(){
           this.getPerson().showMe();
       }
       public abstract Person getPerson();
   }
   ```

5. application-content.xml 配置文件

   ```xml
     <bean id="lookupMethodTest" class="com.spring.demo.bean.LookupMethodTest">
           <lookup-method bean="teacher" name="getPerson"></lookup-method>
       </bean>
       <bean id="student" class="com.spring.demo.bean.Student"></bean>
       <bean id="teacher" class="com.spring.demo.bean.Teacher"></bean>
   ```

   上面的`<lookup-method>`标签中设置了`bean`是`teacher`

6. 运行测试类

   ```java
   public static void main(String[] args) {
           ClassPathXmlApplicationContext applicationContext =
                   new ClassPathXmlApplicationContext("application-content.xml");
           LookupMethodTest lookupMethodTest = (LookupMethodTest)applicationContext.getBean("lookupMethodTest");
           lookupMethodTest.show();
       }
   ```

   结果为 : teacher

7. 修改 application-content.xml 配置文件

```xml
<bean id="lookupMethodTest" class="com.spring.demo.bean.LookupMethodTest">
        <lookup-method bean="student" name="getPerson"></lookup-method>
    </bean>
    <bean id="student" class="com.spring.demo.bean.Student"></bean>
    <bean id="teacher" class="com.spring.demo.bean.Teacher"></bean>
```

上面的`<lookup-method>`标签中设置了`bean`是`student`

8. 再次运行测试类

   结果为 : student

### replaced-method 

方法替换：可以在运行时用新的方法替换现有的方法。与之前的look-up不同的是， replaced-method不但可以动态地替换返回实体bean，而且还能动态地更改原有方法的逻辑

```java
public class ChangeMethod {
    public void changeMe(){
        System.out.println("原始方法");
    }
}
```

```java
import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

public class ReplacedMethod implements MethodReplacer {
    @Override
    public Object reimplement(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("改变了的方法");
        return null;
    }
}

```

```java
public class ReplaceMethodTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("application-content.xml");
        ChangeMethod changeMethod = (ChangeMethod)applicationContext.getBean("changeMethod");
         changeMethod.changeMe();
    }
}
```

结果输出为 : 改变了的方法