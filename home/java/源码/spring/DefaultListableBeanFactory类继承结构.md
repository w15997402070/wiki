# DefaultListableBeanFactory类继承结构

![DefaultListBeanFactory类继承结构](DefaultListBeanFactory类继承结构.png)

- AliasRegistry(接口) 定义对alias的增删改查

- SingletonBeanRegistry(接口) 定义对单例的注册及获取

- BeanDefinitionRegistry(接口) 对BeanDefinition的各种增删改查

- BeanFactory(接口) 定义获取bean及bean的各种属性

- ListableBeanFactory 根据各种条件获取bean的列表

- HierarchicalBeanFactory(接口) 继承BeanFactory,也就是在
BeanFactory定义的功能的基础上增加了对parentFactory的支持

- ConfigurableBeanFactory(接口) 提供配置Factory的各种方法

- ConfigurableListableBeanFactory(接口) BeanFactory配置清单,指定忽略类型及接口

- AutowireCapableBeanFactory(接口) 提供创建bean、自动注入、初始化以及应用bean的后处理器

- AbstractAutowireCapableBeanFactory(抽象类)    综合AbstractBeanFactory 并对接口AutowireCapableBeanFactory 进行实现
- DefaultSingletonBeanRegistry(类) 对接口SingletonBeanRegistry各方法的实现
- FactoryBeanRegistrySupport(抽象类) 在DefaultSingletonBeanRegistry基础上增加了对FactoryBean的特殊处理功能
- SimpleAliasRegistry(类) 主要使用map作为alias的缓存,并对接口AliasRegistry进行实现
- AbstractBeanFactory(抽象类) 综合FactoryBeanRegistrySupport 和 ConfigurableBeanFactory 的功能

- DefaultListableBeanFactory 综合上面所有的功能,主要是对Bean注册后的处理
