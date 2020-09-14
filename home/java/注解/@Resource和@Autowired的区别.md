### @Autowired和@Resource的区别

- 相同点：

@Resource的作用相当于@Autowired，均可标注在字段或者属性的setter方法上。

- 不同点：

@Autowired默认按类型装配（这个注解是属业spring的），默认情况下必须要求依赖对象必须存在，如果要允许null值，可以设置它的required属性为false，如@Autowired(required=false) ，如果我们想使用名称装配可以结合@Qualifier注解进行使用，如下：

  @Autowired() @Qualifier("baseDao")
  private BaseDao baseDao;
@Autowired实现：

注解驱动配置会向spring容器中注册AutowiredAnnotationBeanPostProcessor
当 Spring 容器启动时，AutowiredAnnotationBeanPostProcessor 将扫描 Spring 容器中所有 Bean，当发现 Bean 中拥有 @Autowired 注释时就找到和其匹配（默认按类型匹配）的 Bean，并注入到对应的地方中去。
@Resource 是JDK1.6支持的注解，默认按照名称进行装配，名称可以通过name属性进行指定。也提供按照byType 注入。

如果没有指定name属性，当注解写在字段上时，默认取字段名，按照名称查找。
当注解标注在属性的setter方法上，即默认取属性名作为bean名称寻找依赖对象。
当找不到与名称匹配的bean时才按照类型进行装配。但是需要注意的是，如果name属性一旦指定，就只会按照名称进行装配。
@Resource装配顺序
　　1. 如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异常
　　2. 如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常
　　3. 如果指定了type，则从上下文中找到类型匹配的唯一bean进行装配，找不到或者找到多个，都会抛出异常
　　4. 如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配，则回退为一个原始类型进行匹配，如果匹配则自动装配；

      @Resource(name="baseDao")
      private BaseDao baseDao;
他们的主要区别就是@Autowired是默认按照类型装配的 @Resource默认是按照名称装配的

byName 通过参数名 自动装配，如果一个bean的name 和另外一个bean的 property 相同，就自动装配。
byType 通过参数的数据类型自动自动装配，如果一个bean的数据类型和另外一个bean的property属性的数据类型兼容，就自动装配
在基于主机方式配置Spring的配置文件中，你可能会见到\<context:annotation-config/\>
这样一条配置，他的作用是式地向 Spring 容器注册

AutowiredAnnotationBeanPostProcessor
CommonAnnotationBeanPostProcessor
PersistenceAnnotationBeanPostProcessor
RequiredAnnotationBeanPostProcessor
这 4 个BeanPostProcessor。注册这4个BeanPostProcessor的作用，就是为了你的系统能够识别相应的注解。
