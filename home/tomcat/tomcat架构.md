# tomcat架构

## 组件

![](D:\data\notes\gitnote\image\2019-12-12-16-38-34-image.png)

### Server 组件

tomcat最顶级的组件,代表tomcat的运行实例,在一个JVM只会包含一个Server

### Service 组件

![](D:\data\notes\gitnote\image\2019-12-12-16-46-07-image.png)

Service是服务的抽象,它代表从接收到处理的所有组件的集合.

Server 包含若干个Service

Service 包含若干个用于接收客户端消息的Connector组件和处理请求的Engine组件

不同的Connector组件使用不同的通信协议,如Http协议和AJP协议

Service 还包含若干个Executor组件,每个Executor都是一个线程池,为Service内所有组件提供线程池执行任务

### Connector 组件

![](D:\data\notes\gitnote\image\2019-12-12-16-48-06-image.png)

Connector 主要职责是接收客户端连接并接收消息报文,消息报文由它解析后送往容器中处理

不同的协议对应不同的Connector组件

Connector组件内部实现根据网络`I/O`不同方式分为阻塞I/O和非阻塞I/O.

#### Http的BIO Connector结构 :

![](D:\data\notes\gitnote\image\2019-12-12-16-50-26-image.png)

* Http11Protocol组件 : 是Http协议1.1 版本的抽象,包含接收客户端连接、接收客户端消息报文、报文解析处理、对客户端响应等整个过程

* Mapper组件 : 客户端请求的路由导航组件,通过它能对一个完整的请求地址进行路由,就是它能通过请求地址找到对应的Servlet

* CoyoteAdaptor组件,一个将Connector和Container适配起来的适配器

#### Http的NIO Connector结构

![](D:\data\notes\gitnote\image\2019-12-12-16-55-37-image.png)

* poller 组件,它的职责是在非阻塞I/O方式下轮询多个客户端连接,不断检测、处理各种事件

### Engine组件

Tomcat内部有4个级别的容器，分别是Engine、Host、Context和Wrapper。Engine代表全局Servlet引擎，每个Service组件只能包含一个Engine容器组件，但Engine组件可以包含若干Host容器组件。除了Host之外，它还包含如下组件。

* Listener组件：可以在Tomcat生命周期中完成某些Engine容器相关工作的监听器。

* AccessLog组件：客户端的访问日志，所有客户端访问都会被记录。

* Cluster组件：它提供集群功能，可以将Engine容器需要共享的数据同步到集群中的其他Tomcat实例上。

* Pipeline组件：Engine容器对请求进行处理的管道。

* Realm组件：提供了Engine容器级别的用户-密码-权限的数据对象，配合资源认证模块使用。

### Host组件

Tomcat中Host组件代表虚拟主机，这些虚拟主机可以存放若干Web应用的抽象（Context容器）。除了Context组件之外，它还包含如下组件。

* Listener组件：可以在Tomcat生命周期中完成某些Host容器相关工作的监听器。

* AccessLog组件：客户端的访问日志，对该虚拟主机上所有Web应用的访问都会被记录。

* Cluster组件：它提供集群功能，可以将Host容器需要共享的数据同步到集群中的其他Tomcat实例上。

* Pipeline组件：Host容器对请求进行处理的管道。

* Realm组件：提供了Host容器级别的用户-密码-权限的数据对象，配合资源认证模块使用。

### Context 组件

Context组件是Web应用的抽象，我们开发的Web应用部署到Tomcat后运行时就会转化成Context对象。它包含了各种静态资源、若干Servlet（Wrapper容器）以及各种其他动态资源。它主要包括如下组件。

* Listener组件：可以在Tomcat生命周期中完成某些Context容器相关工作的监听器。

* AccessLog组件：客户端的访问日志，对该Web应用的访问都会被记录。

* Pipeline组件：Context容器对请求进行处理的管道。

* Realm组件：提供了Context容器级别的用户-密码-权限的数据对象，配合资源认证模块使用。

* Loader组件：Web应用加载器，用于加载Web应用的资源，它要保证不同Web应用之间的资源隔离。

* Manager组件：会话管理器，用于管理对应Web容器的会话，包括维护会话的生成、更新和销毁。

* NamingResource组件：命名资源，它负责将Tomcat配置文件的server.xml和Web应用的context.xml资源和属性映射到内存中。

* Mapper组件：Servlet映射器，它属于Context内部的路由映射器，只负责该Context容器的路由导航。

* Wrapper组件：Context的子容器。

### Wrapper 组件

Wrapper容器是Tomcat中4个级别的容器中最小的，与之相对应的是Servlet，一个Wrapper对应一个Servlet。它包含如下组件。

* Servlet组件：Servlet即Web应用开发常用的Servlet，我们会在Servlet中编写好请求的逻辑处理。

* ServletPool组件：Servlet对象池，当Web应用的Servlet实现了Single ThreadModel接口时则会在Wrapper中产生一个Servlet对象池。线程执行时，需先从对象池中获取到一个Servlet对象，ServletPool组件能保证Servlet对象的线程安全。

* Pipeline组件：Wrapper容器对请求进行处理的管道。

## 处理流程

![](D:\data\notes\gitnote\image\2019-12-12-17-07-25-image.png)

## How works tomcat

* 第一次调用Servlet时,要载入Servlet类,调用 init方法 (只会调用一次)

* 针对每个request请求,创建一个 Request 对象和一个 Response 对象

* 若请求的是一个静态资源,则调用 StaticResourceProcessor 对象的 process 方法,传入 request 和 response 对象

* 调用相应的Servlet的service 方法,将 Request对象和Response对象作为参数传入

* 关闭Servlet 时调用 destroy方法,并卸载该Servlet类

### Connector(连接器)

1. 创建HttpRequest对象

2. SocketInputStream对象
   
   * 解析请求行
   
   * 解析请求头

3. servletMappings

## container

Container 实现`org.apache.catalina.Container`接口

tomcat中有四种类型的 Container:

* engine : 表示tomcat的整个servlet引擎

* host : 表示包含一个或多个context的虚拟机

* context : 表示一个web应用.一个context 中可以有多个wrapper

* wrapper : 表示一个独立的servlet

![](D:\data\notes\gitnote\image\2019-12-17-15-50-22-image.png)

Engine 、Host、Context、Wrapper都实现了`org.apache.catalina.Container`接口,标准

实现分别为`org.apache.catalina.core.StandardEngine`、`org.apache.catalina.core.StandardHost`、`org.apache.catalina.core.StandardContext`、`org.apache.catalina.core.StandardWrapper`他们都继承自`org.apache.catalina.core.ContainerBase`

pipeline 包括了 container 中要执行的任务.一个阀( valve)表示一个指定的任务,在 pipeline中有一个基础 valve (basic valve),使用者可以根据自己的需要添加其他的valve(valve和filter类似)

## 生命周期(Lifecycle)

    `org.apache.catalina.Lifecycle`

* BEFORE_START_EVENT

* START_EVENT

* AFTER_START_EVENT

* BEFORE_STOP_EVENT

* STOP_EVENT

* AFTER_STOP_EVENT

启动时会触前面三个start相关的,关闭时会触发后面三个stop相关的.`org.apache.catalina.LifecycleListener`监听器事件发生,触发  LifecycleEvent 方法

`org.apache.catalina.LifecycleEvent`表示生命周期中的某个事件

`org.apache.catalina.util.LifecycleSupport`tomcat提供一个工具类来管理对组件注册的监听器
