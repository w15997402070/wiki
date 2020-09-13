---
title: SpringBoot注解
description: 
published: true
date: 2020-09-13T14:02:12.619Z
tags: 
editor: markdown
---

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

## @AutoConfigureAfter

`**@AutoConfigureAfter**` 在加载配置的类之后再加载当前类



## @EnableTransactionManagement

查看Spring文件夹中的Spring事务文档

## @ControllerAdvice注解

这个注解可以配合 `@InitBinder`,`@ExceptionHandler`,`@ModelAttribute`使用

具体参考`@ExceptionHandler`或`@ModelAttribute`
##  @ExceptionHandler 注解

` @ExceptionHandler `可以处理异常

```java
@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping("/{username}")
    public ResponseEntity<User> get(@PathVariable String username) throws UserNotFoundException {
        // More logic on User

        throw UserNotFoundException.createWith(username);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleContentNotAllowedException(UserNotFoundException unfe) {
        List<String> errors = Collections.singletonList(unfe.getMessage());

        return new ResponseEntity<>(new ApiError(errors), HttpStatus.NOT_FOUND);
    }
}
```



```java
@RestController
@RequestMapping("/users/{username}/posts")
public class PostController {


    @PostMapping
    public ResponseEntity<Post> create(@PathVariable String username, @RequestBody Post post)
            throws ContentNotAllowedException {
        List<ObjectError> contentNotAllowedErrors = ContentUtils.getContentErrorsFrom(post);

        if (!contentNotAllowedErrors.isEmpty()) {
            throw ContentNotAllowedException.createWith(contentNotAllowedErrors);
        }

        // More logic on Post

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(ContentNotAllowedException.class)
    public ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException cnae) {
        List<String> errorMessages = cnae.getErrors()
                .stream()
                .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ApiError(errorMessages), HttpStatus.BAD_REQUEST);
    }
}
```

这样每个Controller有处理异常的代码 ,所以可以考虑将所有的异常都放到一个类中处理,

定义一个类` GlobalExceptionHandler `并且添加上注解`@ControllerAdvice`.

对于异常处理几点建议 : 

* 创建自己的异常类
* 一个项目中只有一个统一异常处理类
* 定义一个` handleException`方法处理所有的异常,,并且添加`@ExceptionHandler`注解,然后在这个方法中分发到具体的异常处理方法
* 每一个异常处理都有自己的方法
* 创建一个通用的返回数据给用户的方法

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 所有的异常都会在这个方法处理
     */
    /** Provides handling for exceptions throughout this service. */
    @ExceptionHandler({ UserNotFoundException.class, ContentNotAllowedException.class })
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof UserNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            UserNotFoundException unfe = (UserNotFoundException) ex;

            return handleUserNotFoundException(unfe, headers, status, request);
        } else if (ex instanceof ContentNotAllowedException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

            return handleContentNotAllowedException(cnae, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }
    /**
    * 单个的异常处理方法
    */
    /** Customize the response for UserNotFoundException. */
    protected ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }
    /**
    * 单个的异常处理方法
    */
    /** Customize the response for ContentNotAllowedException. */
    protected ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMessages = ex.getErrors()
                .stream()
                .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                .collect(Collectors.toList());

        return handleExceptionInternal(ex, new ApiError(errorMessages), headers, status, request);
    }

    /** A single place to customize the response body of all Exception types. */
    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
```


## @Import

导入配置类 : 

```java
package org.springframework.context.annotation;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * {@link Configuration}, {@link ImportSelector}, {@link ImportBeanDefinitionRegistrar}
	 * or regular component classes to import.
	 */
	Class<?>[] value();

}
```

```java
public class Student implements Serializable {
	private static final long serialVersionUID = 6519997700281088880L;
	private Integer id;
	private String name;
	private String tel;
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	
}

@SpringBootApplication
@Import(Student.class)  //这里引入
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WangApplication.class);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		System.out.println(context.getBean(Student.class));
	}
}

//结果就会打印出
//com.example.wang.domain.Student@7b8c9ce7
```
##  @ExceptionHandler 注解

` @ExceptionHandler `可以处理异常

```java
@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping("/{username}")
    public ResponseEntity<User> get(@PathVariable String username) throws UserNotFoundException {
        // More logic on User

        throw UserNotFoundException.createWith(username);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleContentNotAllowedException(UserNotFoundException unfe) {
        List<String> errors = Collections.singletonList(unfe.getMessage());

        return new ResponseEntity<>(new ApiError(errors), HttpStatus.NOT_FOUND);
    }
}
```



```java
@RestController
@RequestMapping("/users/{username}/posts")
public class PostController {


    @PostMapping
    public ResponseEntity<Post> create(@PathVariable String username, @RequestBody Post post)
            throws ContentNotAllowedException {
        List<ObjectError> contentNotAllowedErrors = ContentUtils.getContentErrorsFrom(post);

        if (!contentNotAllowedErrors.isEmpty()) {
            throw ContentNotAllowedException.createWith(contentNotAllowedErrors);
        }

        // More logic on Post

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(ContentNotAllowedException.class)
    public ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException cnae) {
        List<String> errorMessages = cnae.getErrors()
                .stream()
                .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ApiError(errorMessages), HttpStatus.BAD_REQUEST);
    }
}
```

这样每个Controller有处理异常的代码 ,所以可以考虑将所有的异常都放到一个类中处理,

定义一个类` GlobalExceptionHandler `并且添加上注解`@ControllerAdvice`.

对于异常处理几点建议 : 

* 创建自己的异常类
* 一个项目中只有一个统一异常处理类
* 定义一个` handleException`方法处理所有的异常,,并且添加`@ExceptionHandler`注解,然后在这个方法中分发到具体的异常处理方法
* 每一个异常处理都有自己的方法
* 创建一个通用的返回数据给用户的方法

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 所有的异常都会在这个方法处理
     */
    /** Provides handling for exceptions throughout this service. */
    @ExceptionHandler({ UserNotFoundException.class, ContentNotAllowedException.class })
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof UserNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            UserNotFoundException unfe = (UserNotFoundException) ex;

            return handleUserNotFoundException(unfe, headers, status, request);
        } else if (ex instanceof ContentNotAllowedException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

            return handleContentNotAllowedException(cnae, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }
    /**
    * 单个的异常处理方法
    */
    /** Customize the response for UserNotFoundException. */
    protected ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }
    /**
    * 单个的异常处理方法
    */
    /** Customize the response for ContentNotAllowedException. */
    protected ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMessages = ex.getErrors()
                .stream()
                .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                .collect(Collectors.toList());

        return handleExceptionInternal(ex, new ApiError(errorMessages), headers, status, request);
    }

    /** A single place to customize the response body of all Exception types. */
    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
```
## @ModelAttribute

`@ModelAttribute`注解用在方法上,可以在`@RequestMapping`方法执行之前,为这些方法设置值

### 1. 将值设置到model中

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class HelloController {
    /**
     * 1. 将@ModelAttribute 方法的值设置到model中
     * @param model
     * @return
     */
    @RequestMapping("/hello")
    public String hello(Model model){
        log.info(model.asMap().toString());
        return "hello";
    }

    /**
     * 这个方法会在其他方法的前面执行
     * map会在model参数中,key为 map ,值为返回的map
     * @return map
     */
    @ModelAttribute("modelMap")
    public Map testModelAttribute(){
        Map map = new HashMap();
        map.put("worldKey","world");
        return map;
    }
}
```

### 2. 将值设置到参数中

```java
 /**
     * 2. 将@ModelAttribute 方法的值 设置到参数`@ModelAttribute("modelMap") Map map`中
     * @param map
     * @param bindResult 参数设置错误时错误信息会存在这里
     * @return
     */
    @RequestMapping("/hello1")
    public String hello1(@ModelAttribute("modelMap") Map map, BindResult bindResult){
        log.info(map.toString());
        return "hello1";
    }
```

### 3. 配合@ControllerAdvice使用,统一设置值

```java
@ControllerAdvice
public class ControllerAdviceTest {

    @ModelAttribute
    public String testControllerAdvice(){
        return "ControllerAdvice";
    }
}
```
## @PostConstruct

PostConstruct注释用于在完成依赖项注入以执行任何初始化之后需要执行的方法。必须在类投入使用之前调用此方法。

```java
PostConstruct注释用于在完成依赖项注入以执行任何初始化之后需要执行的方法。必须在类投入使用之前调用此方法。
所有支持依赖注入的类都必须支持此注释。即使类没有请求注入任何资源，也必须调用使用PostConstruct注释的方法。
只有一个方法可以使用此批注进行批注。
应用PostConstruct注释的方法必须满足以下所有条件：除了拦截器之外，方法绝不能有任何参数，在这种情况下它采用Interceptor规范定义的InvocationContext对象。
在拦截器类上定义的方法必须具有以下签名之一：
void <METHOD>（InvocationContext）Object <METHOD>（InvocationContext）抛出异常注意：
PostConstruct拦截器方法不能抛出应用程序异常，但可以声明它抛出检查异常，包括java.lang.Exception，
如果相同的拦截器方法除了生命周期事件之外插入业务或超时方法。
如果PostConstruct拦截器方法返回一个值，容器将忽略它。
在非拦截器类上定义的方法必须具有以下签名：void <METHOD>（）应用PostConstruct的方法可以是public，protected，package private或private。
除应用程序客户端外，该方法绝不能是静态的。
```

springBoot中 `org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer`

```java
    @PostConstruct
	public void init() {
		if (!this.properties.isInitialize()) {
			logger.debug("Initialization disabled (not running DDL scripts)");
			return;
		}
		if (this.applicationContext.getBeanNamesForType(DataSource.class, false,
				false).length > 0) {
			this.dataSource = this.applicationContext.getBean(DataSource.class);
		}
		if (this.dataSource == null) {
			logger.debug("No DataSource found so not initializing");
			return;
		}
        //可以运行sql脚本
		runSchemaScripts();
	}
```
## @WebFilter注解过滤器

`@WebFilter`加在过滤器的注解上使用

```java
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Slf4j
public class WebFilterTest implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("WebFilterTest --- init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("WebFilterTest --- doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        log.info("WebFilterTest --- destroy");
    }
}
```

`@WebFilter`源码:

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {
    String description() default "";
    /**Filter显示名*/
    String displayName() default "";
    /**配置参数*/
    WebInitParam[] initParams() default {};
    /**Filter名称*/
    String filterName() default "";

    String smallIcon() default "";

    String largeIcon() default "";
    /**指定对哪些Servlet进行过滤*/
    String[] servletNames() default {};
    /**指定拦截的路径*/
    String[] value() default {};
    /**指定拦截的路径*/
    String[] urlPatterns() default {};
    /**指定Filter对哪种方式的请求进行过滤*/
    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};
    /**指定Filter是否支持异步模式*/
    boolean asyncSupported() default false;
}
```

在springBoot的启动类中加入注解:

```java
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
```

多个`@WebFilter`注解的过滤器可以配合`@Order()`注解实现执行过滤的顺序

```java
import org.springframework.core.annotation.Order;

@WebFilter(urlPatterns = "/*")
@Slf4j
@Order(1)
public class WebFilterTest implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("WebFilterTest --- init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("WebFilterTest --- doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        log.info("WebFilterTest --- destroy");
    }
}
```

```java
@WebFilter(urlPatterns = "/*")
@Slf4j
@Order(2)
public class WebFilterTest2 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("2---WebFilterTest2 --- init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("2 --- WebFilterTest2 --- doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        log.info("WebFilterTest2 --- destroy");
    }
}
```

执行结果

```java
WebFilterTest --- doFilter
2 --- WebFilterTest2 --- doFilter
```

### 不使用注解的方式使用过滤器

创建过滤器类

```java
public class WebFilterTest implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("WebFilterTest --- init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("WebFilterTest --- doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        log.info("WebFilterTest --- destroy");
    }
}
```

注册过滤器

```java
@Configuration
public class FilterConfig {

    @Bean
    public WebFilterTest webFilterTest(){
        return new WebFilterTest();
    }

    @Bean
    public FilterRegistrationBean filterRegist(){
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(webFilterTest());
        frBean.setOrder(1);
        frBean.addUrlPatterns("/*");
        return  frBean;
    }
}
```

### 多个过滤器注册

再添加一个过滤器:

```java
@Slf4j
public class WebFilterTest2 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("2---WebFilterTest2 --- init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("2 --- WebFilterTest2 --- doFilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        log.info("WebFilterTest2 --- destroy");
    }
}
```

修改配置类:

```java
@Configuration
public class FilterConfig {

    @Bean
    public WebFilterTest webFilterTest(){
        return new WebFilterTest();
    }
     @Bean
    public WebFilterTest2 webFilterTest2(){
        return new WebFilterTest2();
    }

    @Bean
    public FilterRegistrationBean filterRegist(){
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(webFilterTest());
        frBean.setOrder(1);
        frBean.addUrlPatterns("/*");
        return  frBean;
    }
    @Bean
    public FilterRegistrationBean filterRegist2(){
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(webFilterTest2());
        frBean.setOrder(2);
        frBean.addUrlPatterns("/*");
        return  frBean;
    }
}
```





