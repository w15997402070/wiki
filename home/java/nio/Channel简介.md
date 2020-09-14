# channel简介

NIO中数据放到缓冲区进行管理,再使用通道将缓冲区中的数据传输到目的地

channel 子接口 : 

* 1)AsynchronousChannel
* 2)AsynchronousByteChannel
* 3)ReadableByteChannel
* 4)ScatteringByteChannel
* 5)WritableByteChannel
* 6)GatheringByteChannel
* 7)ByteChannel
* 8)SeekableByteChannel
* 9)NetworkChannel
* 10)MulticastChannel
* 11)InterruptibleChannel



在Netty中,Channel的实现一定是线程安全的.基于此,我们可以存储一个Channel的引用,并且在需要向远程端点发送数据时,通过这个引用来调用Channel相应的方法;即便当时有很多线程在使用它也不会出现线程问题,而且,消息一定会按顺序发送出去

在业务开发中,不要将长时间执行的耗时任务放入到EventLoop的执行队列中,因为它将会一直阻塞该线程所对应的所有Channel上的其他执行任务,如果我们需要进行阻塞调用或是耗时的操作,那么我们需要使用一个专门的EventExecutor(业务线程池)

通常会有两种实现方式:

1. 在ChannelHandler的回调方法中,使用自己定义的业务线程池,这样就可以实现异步调用

   

2. 借助于Netty提供的向ChannelPipeline添加Channelhanlder时调用的addLast方法来传递EventExecutor

   默认情况下(调用addLast(handler)),ChannelHandler中的回调方法都是i/o线程所执行,如果调用了ChannelPipeline addLast(EventExecutorGroup group,ChannelHandler... handlers); 方法,那么ChannelHandler中的回调方法就是由参数中的group线程祖来执行的