# Tomcat的JMX监控与管理

## JMX规范

JMX(java Management Extension)java管理扩展,主要负责系统管理,使基于此规范而扩展的系统拥有管理监控功能.

### 基本结构

JMX体系结构分三个层次 : 

![image-20191218173304997](D:\data\notes\notes\tomcat\Untitled\image-20191218173304997.png)

* 设备层(Instrumentation Level) : 主要定义了信息模型.(MBean)
* 代理层(Agent Level) : 主要定义了各种服务以及通信模型.核心是一个MBean服务器,所有管理构件都需要向它注册,才能被管理.注册在MBean服务器上的管理构件并不直接和远程应用程序进行通信，它们通过协议适配器进行通信。而协议适配器也以管理构件的形式向MBean服务器注册后才能提供相应的服务。
* 分布服务层（Distributed Service Level）：JMX架构的最外一层。它负责使JMX代理对外界可用，主要定义了能对代理层进行操作的管理接口和构件，具体内容依靠适配器实现，这样外部管理者就可以操作代理。 