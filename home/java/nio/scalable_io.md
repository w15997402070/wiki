# scalable Io in java

## 大纲

* 高效的网络服务
         事件驱动程序
  Reactor模式
      基础版本
      多线程版本
      其它变异体
  java.nio.nonblocking IO APIs演练

网络服务
web服务,分布式对象等等
大多都有相似的基础服务
	读请求
	请求解码
	处理服务
	对响应编码
	返回响应
但每步在花费和本质上是不同的
	XMl解析,文件传输,Web页面生成,计算服务等等





