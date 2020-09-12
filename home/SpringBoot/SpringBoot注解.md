# SpringBoot注解

[toc]



```
@Conditional 它的作用是按照一定的条件进行判断，满足条件的才给容器注册Bean。

@ConditionalOnBean         //   当给定的在bean存在时,则实例化当前Bean
@ConditionalOnMissingBean  //   当给定的在bean不存在时,则实例化当前Bean
@ConditionalOnClass        //   当给定的类名在类路径上存在，则实例化当前Bean
@ConditionalOnMissingClass //   当给定的类名在类路径上不存在，则实例化当前Bean
```

## @Conditional

```java
package org.springframework.context.annotation

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {
    Class<? extends Condition>[] value();
}

```

spring 的 condition接口如下 : 

```java
package org.springframework.context.annotation

public interface Condition {
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

示例 : 

```java
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;
//实现接口
public class ConditionTest implements Condition {
    //条件判断
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            System.out.println(activeProfile);
            if (Objects.equals(activeProfile,"local")){
                return true;
            }
        }
        return false;
    }
}
```

下面代码就会被执行 : 

```java
    @Bean
    @Conditional(ConditionTest.class)
    public CustomVo testCon(){
        CustomVo customVo = new CustomVo();
        System.out.println("---------------------测试condition");
        return customVo;
    }
```



## @ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})

```java
package org.springframework.boot.autoconfigure.condition

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({OnClassCondition.class})
public @interface ConditionalOnClass {
    Class<?>[] value() default {};

    String[] name() default {};
}

package org.springframework.boot.autoconfigure.condition;

@Order(Ordered.HIGHEST_PRECEDENCE)
class OnClassCondition extends SpringBootCondition
		implements AutoConfigurationImportFilter, BeanFactoryAware, BeanClassLoaderAware {
		
	private BeanFactory beanFactory;

	private ClassLoader beanClassLoader;

	@Override
	public boolean[] match(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata) {
		ConditionEvaluationReport report = getConditionEvaluationReport();
		ConditionOutcome[] outcomes = getOutcomes(autoConfigurationClasses,
				autoConfigurationMetadata);
		boolean[] match = new boolean[outcomes.length];
		for (int i = 0; i < outcomes.length; i++) {
			match[i] = (outcomes[i] == null || outcomes[i].isMatch());
			if (!match[i] && outcomes[i] != null) {
				logOutcome(autoConfigurationClasses[i], outcomes[i]);
				if (report != null) {
					report.recordConditionEvaluation(autoConfigurationClasses[i], this,
							outcomes[i]);
				}
			}
		}
		return match;
	}

    //...其他代码略
}
	
```

`OnClassCondition` 有自己的`matches`方法,`boolean[] match(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata)`

## @ConditionalOnMissingBean

```java
package org.springframework.boot.autoconfigure.condition;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnMissingBean {

	Class<?>[] value() default {};

	String[] type() default {};

	Class<?>[] ignored() default {};

	String[] ignoredType() default {};

	Class<? extends Annotation>[] annotation() default {};

	String[] name() default {};

	SearchStrategy search() default SearchStrategy.ALL;

}


class OnBeanCondition extends SpringBootCondition implements ConfigurationCondition {
    public static final String FACTORY_BEAN_OBJECT_TYPE = BeanTypeRegistry.FACTORY_BEAN_OBJECT_TYPE;

    @Override
	public ConditionOutcome getMatchOutcome(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		ConditionMessage matchMessage = ConditionMessage.empty();
        //这里处理 ConditionalOnBean
		if (metadata.isAnnotated(ConditionalOnBean.class.getName())) {
			BeanSearchSpec spec = new BeanSearchSpec(context, metadata,
					ConditionalOnBean.class);
			List<String> matching = getMatchingBeans(context, spec);
			if (matching.isEmpty()) {
				return ConditionOutcome.noMatch(
						ConditionMessage.forCondition(ConditionalOnBean.class, spec)
								.didNotFind("any beans").atAll());
			}
			matchMessage = matchMessage.andCondition(ConditionalOnBean.class, spec)
					.found("bean", "beans").items(Style.QUOTE, matching);
		}
        //这里处理 ConditionalOnSingleCandidate
		if (metadata.isAnnotated(ConditionalOnSingleCandidate.class.getName())) {
			BeanSearchSpec spec = new SingleCandidateBeanSearchSpec(context, metadata,
					ConditionalOnSingleCandidate.class);
			List<String> matching = getMatchingBeans(context, spec);
			if (matching.isEmpty()) {
				return ConditionOutcome.noMatch(ConditionMessage
						.forCondition(ConditionalOnSingleCandidate.class, spec)
						.didNotFind("any beans").atAll());
			}
			else if (!hasSingleAutowireCandidate(context.getBeanFactory(), matching,
					spec.getStrategy() == SearchStrategy.ALL)) {
				return ConditionOutcome.noMatch(ConditionMessage
						.forCondition(ConditionalOnSingleCandidate.class, spec)
						.didNotFind("a primary bean from beans")
						.items(Style.QUOTE, matching));
			}
			matchMessage = matchMessage
					.andCondition(ConditionalOnSingleCandidate.class, spec)
					.found("a primary bean from beans").items(Style.QUOTE, matching);
		}
        //这里处理 ConditionalOnMissingBean
		if (metadata.isAnnotated(ConditionalOnMissingBean.class.getName())) {
			BeanSearchSpec spec = new BeanSearchSpec(context, metadata,
					ConditionalOnMissingBean.class);
			List<String> matching = getMatchingBeans(context, spec);
			if (!matching.isEmpty()) {
				return ConditionOutcome.noMatch(ConditionMessage
						.forCondition(ConditionalOnMissingBean.class, spec)
						.found("bean", "beans").items(Style.QUOTE, matching));
			}
			matchMessage = matchMessage.andCondition(ConditionalOnMissingBean.class, spec)
					.didNotFind("any beans").atAll();
		}
		return ConditionOutcome.match(matchMessage);
	}
    
}


public abstract class SpringBootCondition implements Condition {

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public final boolean matches(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		String classOrMethodName = getClassOrMethodName(metadata);
		try {
			ConditionOutcome outcome = getMatchOutcome(context, metadata);
			logOutcome(classOrMethodName, outcome);
			recordEvaluation(context, classOrMethodName, outcome);
			return outcome.isMatch();
		}
		catch (NoClassDefFoundError ex) {
			throw new IllegalStateException(
					"Could not evaluate condition on " + classOrMethodName + " due to "
							+ ex.getMessage() + " not "
							+ "found. Make sure your own configuration does not rely on "
							+ "that class. This can also happen if you are "
							+ "@ComponentScanning a springframework package (e.g. if you "
							+ "put a @ComponentScan in the default package by mistake)",
					ex);
		}
		catch (RuntimeException ex) {
			throw new IllegalStateException(
					"Error processing condition on " + getName(metadata), ex);
		}
	}
}
```

`OnBeanCondition ` 没有 `matches()`方法,他继承自`SpringBootCondition` 的 `matches(ConditionContext context,      AnnotatedTypeMetadata metadata)`方法,

## @Qualifier
* 按类型自动装配可能多个bean实例的情况，可以使用Spring的`@Qualifier`注解缩小范围（或指定唯一），也可以用于指定单独的构造器参数或方法参数
* 可用于注解集合类型变量
* 如果通过名字进行注解注入，主要使用的不是`@Autowired`（即使在技术上能够通过`@Qualifier`指定bean的名字），替代方式是使用JSR-250`@Resource`注解，它是通过其独特的名称来定义来识别特定的目标（这是一个与所声明的类型是无关的匹配过程）
* 因语义差异，集合或Map类型的bean无法通过`@Autowired`来注入，因为没有类型匹配到这样的bean，为这些bean使用`@Resource`注解，通过唯一名称引用集合或Map的bean

* `@Autowired`适用于fields，constructors，multi-argument methods这些允许在参数级别使用`@Qualifier`注解缩小范围的情况
* `@Resource`适用于成员变量、只有一个参数的setter方法，所以在目标是构造器或一个多参数方法时，最好的方式是使用qualifiers

## @ConditionalOnProperty

根据配置文件的属性选择是否加载类

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional({OnPropertyCondition.class})
public @interface ConditionalOnProperty {
    String[] value() default {};

    // 配置前缀
    String prefix() default "";
    // 配置名
    String[] name() default {};
    //匹配的值
    String havingValue() default "";
    // 即便没有配置，也依然创建默认为false
    boolean matchIfMissing() default false;
}
```

```java
@ConditionalOnProperty(name ="spring.profiles.active",havingValue = "dev")
public class Test {
    
}

@Bean
@ConditionalOnProperty(name = {"conditional.property"}, havingValue = "properExists")
public PropertyValueExistBean propertyValueExistBean() {
    return new PropertyValueExistBean("properExists");
}
```

## @ConditionalOnExpression

根据判断条件选择是否加载类

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional({OnExpressionCondition.class})
public @interface ConditionalOnExpression {
    String value() default "true";
}
```

```java
@ConditionalOnExpression("'${spring.profiles.active}' == 'dev'")
public class Test {
    
}
```

## @Profile

根据配置文件选择是否加载类

```java
@Profile("dev")
public class Test {
    
}
```



## @EnableConfigurationProperties

# @AutoConfigureAfter

`**@AutoConfigureAfter**` 在加载配置的类之后再加载当前类



## @EnableTransactionManagement

查看Spring文件夹中的Spring事务文档