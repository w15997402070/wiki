# DispatcherServlet解析

[toc]



## 1 . DispatcherServlet继承结构解析

![](dispatcherServlet继承结构图.png)

```
XXXAware在spring里表示对XXX可以感知,通熟点解释就是:如果某个类里面想要使用spring的一些东西,就可以通过实现XXXAware接口告诉spring,spring看到后就会给你送过来,而接收的方式是通过实现借库唯一的方法setXXX.

比如有一个类想要使用当前的ApplicationContext,那么我们只需要让它实现ApplicationContextAware接口,然后实现接口中的唯一的方法void setApplicationContext(ApplicationContext applicationContext)就可以了,spring会自动调用这个方法传给我们,我们只需要接收就可以了.
```

```
 EnviromentCapable,顾名思义,就是具有Environment的能力,也就是可以提供Environment,所以EnvironmentCapable 唯一的方法是 Environment getEnvironment(),用于实现EnvironmentCapable接口的类,就是告诉spring他可以提供Environment,当spring需要environment 的时候就会调用其getEnvironment 方法跟它要. 
```

### DispatcherServlet.properties:

```properties
org.springframework.web.servlet.LocaleResolver=org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver

org.springframework.web.servlet.ThemeResolver=org.springframework.web.servlet.theme.FixedThemeResolver

org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
	org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

org.springframework.web.servlet.HandlerExceptionResolver=org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver,\
	org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver,\
	org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver

org.springframework.web.servlet.RequestToViewNameTranslator=org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator

org.springframework.web.servlet.ViewResolver=org.springframework.web.servlet.view.InternalResourceViewResolver

org.springframework.web.servlet.FlashMapManager=org.springframework.web.servlet.support.SessionFlashMapManager
```

### initStrategies()方法解析

```java
    protected void initStrategies(ApplicationContext context) {
		//处理上传文件
		initMultipartResolver(context);
		//处理国际化 默认是AcceptHeaderLocaleResolver
		initLocaleResolver(context);
		//处理主题 默认是FixedThemeResolver
		initThemeResolver(context);
		//处理请求映射关系  默认用BeanNameUrlHandlerMapping
		initHandlerMappings(context);
		//根据Handler的类型定义不同的处理规则
		initHandlerAdapters(context);
		//当Handler处理出错时，通过这个Handler来统一做处理，默认的实现类是SimpleMappingExceptionResolver
		initHandlerExceptionResolvers(context);
		//将指定的ViewName按照定义的RequestToViewNameTranslator替换成想要的格式，如加上前缀或者后缀等
		initRequestToViewNameTranslator(context);
		//用于将View解析成页面，在ViewResolvers中可以设置多个解析策略
		initViewResolvers(context);
		initFlashMapManager(context);
	}
```

#### initHandlerMappings()

```java
private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;

		if (this.detectAllHandlerMappings) {
			//查找ApplicationContext中所有的 HandlerMappings 包括祖先容器(就是Spring容器)
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<>(matchingBeans.values());
				// HandlerMappings 排序
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerMappings declared for servlet '" + getServletName() +
						"': using default strategies from DispatcherServlet.properties");
			}
		}
	}
```



#### 获取默认的strategy `getDefaultStrategies()`

```java
    /**
	 * 通过策略接口获取一个默认的策略对象集合
	 * 默认的实现在 "DispatcherServlet.properties" 中
	 */
    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
		String key = strategyInterface.getName();
		//从defaultStrategies获取所需策略的类型
		String value = defaultStrategies.getProperty(key);
		if (value != null) {
			String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
			List<T> strategies = new ArrayList<>(classNames.length);
			for (String className : classNames) {
				try {
					Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
					Object strategy = createDefaultStrategy(context, clazz);
					strategies.add((T) strategy);
				}
				catch (ClassNotFoundException ex) {
					throw new BeanInitializationException(
							"Could not find DispatcherServlet's default strategy class [" + className +
							"] for interface [" + key + "]", ex);
				}
				catch (LinkageError err) {
					throw new BeanInitializationException(
							"Unresolvable class definition for DispatcherServlet's default strategy class [" +
							className + "] for interface [" + key + "]", err);
				}
			}
			return strategies;
		}
		else {
			return new LinkedList<>();
		}
	}
```



PropertyEditorRegistrySupport #  createDefaultEditors()

```java
private void createDefaultEditors() {
		this.defaultEditors = new HashMap<>(64);

		// Simple editors, without parameterization capabilities.
		// The JDK does not contain a default editor for any of these target types.
		this.defaultEditors.put(Charset.class, new CharsetEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Currency.class, new CurrencyEditor());
		this.defaultEditors.put(File.class, new FileEditor());
		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Path.class, new PathEditor());
		this.defaultEditors.put(Pattern.class, new PatternEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		this.defaultEditors.put(Reader.class, new ReaderEditor());
		this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
		this.defaultEditors.put(URI.class, new URIEditor());
		this.defaultEditors.put(URL.class, new URLEditor());
		this.defaultEditors.put(UUID.class, new UUIDEditor());
		this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());

		// Default instances of collection editors.
		// Can be overridden by registering custom instances of those as custom editors.
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
		this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

		// Default editors for primitive arrays.
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		// The JDK does not contain a default editor for char!
		this.defaultEditors.put(char.class, new CharacterEditor(false));
		this.defaultEditors.put(Character.class, new CharacterEditor(true));

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
		this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

		// Only register config value editors if explicitly requested.
		if (this.configValueEditorsActive) {
			StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
			this.defaultEditors.put(String[].class, sae);
			this.defaultEditors.put(short[].class, sae);
			this.defaultEditors.put(int[].class, sae);
			this.defaultEditors.put(long[].class, sae);
		}
	}
```

PropertyEditorSupport

```java
import java.beans.*;

public class PropertyEditorSupport implements PropertyEditor {
    public String getAsText() {
        return (this.value != null)
                ? this.value.toString()
                : null;
    }
    //子类覆盖这个方法,进行类型转换
     public void setAsText(String text) throws java.lang.IllegalArgumentException {
        if (value instanceof String) {
            setValue(text);
            return;
        }
        throw new java.lang.IllegalArgumentException(text);
    }
}
```

例如PropertiesEditor:

```java
package org.springframework.beans.propertyeditors
    
public class PropertiesEditor extends PropertyEditorSupport {

	/**
	 * 根据传入的字符串转换为 Properties
	 */
	@Override
	public void setAsText(@Nullable String text) throws IllegalArgumentException {
		Properties props = new Properties();
		if (text != null) {
			try {
				// Must use the ISO-8859-1 encoding because Properties.load(stream) expects it.
				props.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1)));
			}
			catch (IOException ex) {
				// Should never happen.
				throw new IllegalArgumentException(
						"Failed to parse [" + text + "] into Properties", ex);
			}
		}
		setValue(props);
	}

	/**
	 * Take {@link Properties} as-is; convert {@link Map} into {@code Properties}.
	 */
	@Override
	public void setValue(Object value) {
		if (!(value instanceof Properties) && value instanceof Map) {
			Properties props = new Properties();
			props.putAll((Map<?, ?>) value);
			super.setValue(props);
		}
		else {
			super.setValue(value);
		}
	}

}
```



## 初始化解析

```
HttpServletBean # init()
HttpServletBean # initServletBean()  -> FrameworkServlet # initServletBean()
FrameworkServlet # initWebApplicationContext()
```





```java
requestMappingHandlerMapping -> {RequestMappingHandlerMapping@9781} 
welcomePageHandlerMapping -> {WelcomePageHandlerMapping@9783} 
beanNameHandlerMapping -> {BeanNameUrlHandlerMapping@9785} 
routerFunctionMapping -> {RouterFunctionMapping@9787} 
resourceHandlerMapping -> {SimpleUrlHandlerMapping@9789} 
```



```java
requestMappingHandlerAdapter -> {RequestMappingHandlerAdapter@9803} 
handlerFunctionAdapter -> {HandlerFunctionAdapter@9805} 
httpRequestHandlerAdapter -> {HttpRequestHandlerAdapter@9807} 
simpleControllerHandlerAdapter -> {SimpleControllerHandlerAdapter@9809} 
```



```java
private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.initBinderCache.get(handlerType);
		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
			this.initBinderCache.put(handlerType, methods);
		}
		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<>();
		// Global methods first
		//将所有符合条件的全局InitBinder方法添加到initBinderMethods
		this.initBinderAdviceCache.forEach((clazz, methodSet) -> {
			if (clazz.isApplicableToBeanType(handlerType)) {
				Object bean = clazz.resolveBean();
				for (Method method : methodSet) {
					initBinderMethods.add(createInitBinderMethod(bean, method));
				}
			}
		});
		//将当前Handler中的InitBinder方法添加到initBinderMethods
		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			initBinderMethods.add(createInitBinderMethod(bean, method));
		}
		//创建DataBinderFactory并返回
		return createDataBinderFactory(initBinderMethods);
	}

    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
			throws Exception {

		return new ServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
	}
```







```java
private ModelFactory 	getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
		//获取 SessionAttributesHandler
		SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
		Class<?> handlerType = handlerMethod.getBeanType();
		//获取处理器类中注释了@ModelAttribute而且没有注释@RequestMapping的类型，第一次获取后添加到缓存，以后直接从缓存中获取
		Set<Method> methods = this.modelAttributeCache.get(handlerType);
		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
			this.modelAttributeCache.put(handlerType, methods);
		}
		List<InvocableHandlerMethod> attrMethods = new ArrayList<>();
		// Global methods first
		//先添加全局ModelAttribute方法，后添加当前处理器定义的@ModelAttribute方法
		this.modelAttributeAdviceCache.forEach((clazz, methodSet) -> {
			if (clazz.isApplicableToBeanType(handlerType)) {
				Object bean = clazz.resolveBean();
				for (Method method : methodSet) {
					attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
				}
			}
		});
		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
		}
		return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
	}
```

### 参数解析入口

```
RequestMappingHandlerAdapter # invokeHandlerMethod() -->
ServletInvocableHandlerMethod # invokeAndHandle()  -->
    InvocableHandlerMethod    # invokeForRequest()
    InvocableHandlerMethod    # getMethodArgumentValues()
                                  args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
```



```java
//invocableMethod = ServletInvocableHandlerMethod
//webRequest = new ServletWebRequest(request, response);
//mavContainer = new ModelAndViewContainer()
invocableMethod.invokeAndHandle(webRequest, mavContainer);
```