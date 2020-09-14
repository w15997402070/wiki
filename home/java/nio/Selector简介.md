# Selector简介

Selector 将通道注册进选择器中,主要作用是使用一个线程来对多个通道中的已就绪通道进行选择,然后就可以对选择的通道进行数据处理,属于一对多的关系,也就是一个线程操作多个通道,这在NIO中称为"I/O多路复用".

## Selector,SelectKey和SelectableChannel的关系

### 通道类`AbstractInterruptibleChannel`与接口`InterruptibleChannel`的介绍
`InterruptibleChannel`接口的作用是使通道能以异步的方式进行关闭与中断;

* 如果通道实现了`asynchronously`和`closeable`特性，那么，当一个线程在一个能被中断的通道上出现了阻塞状态，其他线程调用这个通道的`close()`方法时，这个呈阻塞状态的线程将接收到`AsynchronousCloseException`异常。
* 如果通道实现了`asynchronously`和`closeable`，并且还实现了`interruptible`特性，那么，当一个线程在一个能被中断的通道上出现了阻塞状态，其他线程调用这个阻塞线程的`interrupt()`方法时，通道将被关闭，这个阻塞的线程将接收到`ClosedByInterruptException`异常，这个阻塞线程的状态一直是中断状态。

`AbstractInterruptibleChannel`是抽象类,提供一个可以被中断的通道的基本实现类

### 通道类 SelectableChannel的介绍



`SelectionKey register(Selector sel, int ops)` 将`SelectableChannel`注册到选择器上返回一个`SelectionKey`对象.

需要注意的是，不能直接注销通道，而是通过调用`SelectionKey`类的`cancel()`方法显式地取消，这将在选择器的下一次选择`select()`操作期间去注销通道。

一个通道至多只能在任意特定选择器上注册一次。可以通过调用`isRegistered()`方法来确定是否已经向一个或多个选择器注册了某个通道。



### SelectionKey

* ```
  OP_READ : 请求数据已经就绪,可以从Channel中读取时获得通知
  ```

* ```
  OP_WRITE: 请求可以向Channel中写更多的数据时获得通知
  ```

* ```
  OP_CONNECT : 请求在建立一个连接时获得通知
  ```

* ```
  OP_ACCEPT: 请求在接受新连接并创建Channel时获得通知
  ```