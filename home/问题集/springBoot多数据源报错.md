
# 多数据源报错

```java
Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)

    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.tomcat.util.modeler.BaseModelMBean.invoke(BaseModelMBean.java:300)
    at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.invoke(DefaultMBeanServerInterceptor.java:819)
    at com.sun.jmx.mbeanserver.JmxMBeanServer.invoke(JmxMBeanServer.java:801)
    at org.apache.catalina.mbeans.MBeanFactory.createStandardContext(MBeanFactory.java:484)
    at org.apache.catalina.mbeans.MBeanFactory.createStandardContext(MBeanFactory.java:433)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.__invoke(DelegatingMethodAccessorImpl.java:43)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.apache.tomcat.util.modeler.BaseModelMBean.invoke(BaseModelMBean.java:300)
    at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.invoke(DefaultMBeanServerInterceptor.java:819)
    at com.sun.jmx.mbeanserver.JmxMBeanServer.invoke(JmxMBeanServer.java:801)
    at javax.management.remote.rmi.RMIConnectionImpl.doOperation(RMIConnectionImpl.java:1468)
    at javax.management.remote.rmi.RMIConnectionImpl.access$300(RMIConnectionImpl.java:76)
    at javax.management.remote.rmi.RMIConnectionImpl$PrivilegedOperation.run(RMIConnectionImpl.java:1309)
    at javax.management.remote.rmi.RMIConnectionImpl.doPrivilegedOperation(RMIConnectionImpl.java:1401)
    at javax.management.remote.rmi.RMIConnectionImpl.invoke(RMIConnectionImpl.java:829)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.__invoke(DelegatingMethodAccessorImpl.java:43)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:346)
    at sun.rmi.transport.Transport$1.run(Transport.java:200)
    at sun.rmi.transport.Transport$1.run(Transport.java:197)
    at java.security.AccessController.doPrivileged(Native Method)
    at sun.rmi.transport.Transport.serviceCall(Transport.java:196)
    at sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:568)
    at sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:826)
    at sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:683)
    at java.security.AccessController.doPrivileged(Native Method)
    at sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:682)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at java.lang.Thread.run(Thread.java:748)
```  

    需要在配置数据源的地方加上注解@Primary

    ```java
    @Bean(name = CommentDataSourceConfig.DATA_SOURCE_BEAN_NAME)
    @Primary
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        datasource.setConnectionProperties(connectionProperties);
        try {
            datasource.setFilters(filters);
        } catch (Exception e) {
            logger.error("druid configuration initialization filter", e);
        }
        return datasource;
   }

    @Bean(name = CommentDataSourceConfig.TRANSACTION_MANAGER_BEAN_NAME)  
    public PlatformTransactionManager annotationDrivenTransactionManager() {  
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = CommentDataSourceConfig.SQL_SESSION_FACTORY_BEAN_NAME)
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean(
            PageInterceptor pageInterceptor) throws Exception {
        final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        Set<String> set = new HashSet<>();
        set.add("equals");
        set.add("clone");
        set.add("hashCode");
        set.add("toString");
        configuration.setLazyLoadTriggerMethods(set);
        configuration.setLocalCacheScope(LocalCacheScope.SESSION);
        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
        configuration.setSafeRowBoundsEnabled(false);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        configuration.setCacheEnabled(true);
        configuration.setJdbcTypeForNull(JdbcType.OTHER);
        bean.setConfiguration(configuration);
        // 自定义数据库配置的时候，需要将pageHelper的bean注入到Plugins中，如果采用系统默认的数据库配置，则只需要定义pageHelper的bean，会自动注入。
        bean.setPlugins(new Interceptor[] { pageInterceptor });
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 如果基于注解扫描Mapper，不需配置xml路径
        bean.setMapperLocations(
                resolver.getResources(CommentDataSourceConfig.MAPPER_XML_PATH));
        return bean.getObject();
    }

```java
