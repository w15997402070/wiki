# AOP

* Advice（通知）：定义在连接点做什么，为切面增强提供织入接口.分为前置通知（before advice）、后置通知（after advice）、环绕通知（around advice）、事后返回通知（afterReturning advice）和异常通知（afterThrowing advice）.在Spring AOP中，它主要描述Spring AOP围绕方法调用而注入的切面行为
* Pointcut（切点）：决定Advice通知应该作用于哪个连接点，也就是说通过Pointcut切点来定义需要增强的方法的集合，这些集合的选取可以按照一定的规则来完成。在这种情况下，Pointcut通常意味着标识方法，例如，这些需要增强的地方可以是被某个正则表达式进行标识，或根据某个方法名进行匹配等。
* Advisor（通知器）: 当我们完成对目标方法的切面增强设计（advice）和关注点的设计（pointcut）以后，需要一个对象把它们结合起来，完成这个作用的就是Advisor。通过Advisor，可以定义应该使用哪个Advice并在哪个Pointcut使用它
* join point(连接点): 对应的是具体被拦截的对象
* target(目标对象): 被代理对象
* introduction(引入): 指引入新的类和其方法，增强现有Bean的功能
* weaving(织入): 它是一个通过动态代理技术，为原有服务对象生成代理对象，然后将与切点定义匹配的连接点拦截，并按约定将各类通知织入约定流程的过程。
* aspect(切面): 是一个可以定义切点、各类通知和引入的内容，Spring AOP将通过它的信息来增强Bean的功能或者将对应的方法织入流程。
* MethodMatcher:判断是否需要对当前方法调用进行增强
* JdkRegexpMethodPointcut: 正则表达式切点.正则表达式对方法名进行匹配的功能
* NameMatchMethodPointcut: 通过方法名匹配进行Advice匹配的

AopNamespaceHandler

```java
public void init() {
		// In 2.0 XSD as well as in 2.1 XSD.
		registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
		registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
		registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());

		// Only in 2.0 XSD: moved to context namespace as of 2.1
		registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
}
```

AspectJAutoProxyBeanDefinitionParser的parse方法

```java
    @Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		//注册AnnotationAwareAspect JAutoProxyCreator
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
		//对于注解中子类的处理
		extendBeanDefinition(element, parserContext);
		return null;
	}
```

AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element)

```java
public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(
			ParserContext parserContext, Element sourceElement) {
        //注册或升级AutoProxyCreator定义beanName为org.Springframework.aop.config.internalAutoProxyCreator的BeanDefinition
		BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(
				parserContext.getRegistry(), parserContext.extractSource(sourceElement));
		//对于proxy-target-class以及expose-proxy属性的处理
		useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
		//注册组件并通知，便于监听器做进一步处理其中beanDefinition的className为AnnotationAwareAspectJAutoProxyCreat
		registerComponentIfNecessary(beanDefinition, parserContext);
	}
```

