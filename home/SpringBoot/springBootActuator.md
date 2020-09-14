1.      Spring Boot Actuator的关键特性是在应用程序里提供众多Web端点，通过它们了解应用程序
        运行时的内部状况。有了Actuator，你可以知道Bean在Spring应用程序上下文里是如何组装在一
        起的，掌握应用程序可以获取的环境属性信息，获取运行时度量信息的快照……

  Actuator提供了13个端点，具体如表7-1所示。

    表7-1 Actuator的端点
HTTP方法     路 径              描 述
GET       /autoconfig        提供了一份自动配置报告，记录哪些自动配置条件通过了，哪些没通过<br/>
GET       /configprops       描述配置属性（包含默认值）如何注入Bean<br/>
GET       /beans             描述应用程序上下文里全部的Bean，以及它们的关系<br/>
GET       /dump              获取线程活动的快照<br/>
GET       /env               获取全部环境属性<br/>
GET       /env/{name}        根据名称获取特定的环境属性值<br/>
GET       /health            报告应用程序的健康指标，这些值由HealthIndicator的实现类提供<br/>
GET       /info              获取应用程序的定制信息，这些信息由info打头的属性提供<br/>
GET       /mappings          描述全部的URI路径，以及它们和控制器（包含Actuator端点）的映射关系<br/>
GET       /metrics           报告各种应用程序度量信息，比如内存用量和HTTP请求计数<br/>
GET       /metrics/{name}    报告指定名称的应用程序度量值<br/>
OST       /shutdown          关闭应用程序，要求endpoints.shutdown.enabled设置为true<br/>
GET        /trace            提供基本的HTTP请求跟踪信息（时间戳、 HTTP头等）<br/>

2. 启用Actuator:
要启用Actuator的端点，只需在项目中引入Actuator的起步依赖即可。在Gradle构建说明文件
里，这个依赖是这样的：
```
  compile 'org.springframework.boot:spring-boot-starter-actuator'
```
对于Maven项目，引入的依赖是这样的：
```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```
亦或你使用Spring Boot CLI，可以使用如下@Grab注解：
```
   @Grab('spring-boot-starter-actuator')
```
无论Actuator是如何添加的，在应用程序运行时自动配置都会生效。 Actuator会开启。
表7-1中的端点可以分为三大类：配置端点、度量端点和其他端点。
