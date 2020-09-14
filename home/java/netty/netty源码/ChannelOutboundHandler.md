# 类源码解析

[toc]

## Channel

Channel主要方法

* Channel read() : 从当前的Channel中读取数据到第一个 inbound 缓冲区中,如果数据被成功读取,触发 `ChannelHandler.channelRead(ChannelHandlerContext ctx,Object msg) `事件,读取操作API调用完成后,紧接着触发 ` ChannelHandler.channelReadComplete(ChannelHandlerContext ctx)`事件,这样业务的 `ChannelHandler` 可以决定是否需要继续读取数据,如果已经有读操作请求被挂起,则后续的操作会被忽略
*  ChannelOutboundInvoker # ChannelFuture write(Object msg): 请求将当前的msg通过ChannelPipeline写入到目标Channel中.write操作只是将消息写入到消息发送环形数组中,并没有真正被发送,只有调用flush操作才会被写入到Channel中,发送给对方
* EventLoop eventLoop() : 获取Channel注册的EventLoop.EventLoop本质上就是处理网络读写事件的Reactor线程.在netty中,它不仅仅用来处理网络事件,也可以用来执行定时任务和用户自定义NioTask等任务
* ChannelConfig config() : 获取当前Channel的配置信息
* ChannelMetadata metadata() : TCP参数元数据描述信息,例如TCP缓冲区大小,TCP超时时间,是否重用地址等
* Channel parent() : 对于服务端Channel而言,它的父Channel为空.对于客户端而言,它的父Channel就是创建它的ServerSocketChannel



## NioServerSocketChannel

![image-20200407153830818](D:\data\notes\notes\java\netty\netty源码\ChannelOutboundHandler\image-20200407153830818.png)



## NioSocketChannel

![image-20200407154244843](D:\data\notes\notes\java\netty\netty源码\ChannelOutboundHandler\image-20200407154244843.png)



## ChannelPipeline

![image-20200407201007826](D:\data\notes\notes\java\netty\netty源码\ChannelOutboundHandler\image-20200407201007826.png)

### ChannelInboundInvoker

```java
package io.netty.channel;
/**
 * 入站消息触发事件
 */
public interface ChannelInboundInvoker {
    /**
     * 当Channel注册到EventLoop中时
     */
    ChannelInboundInvoker fireChannelRegistered();
    /**
     */
    ChannelInboundInvoker fireChannelUnregistered();
    /**
     * TCP链路建立成功,Channel激活事件
     */
    ChannelInboundInvoker fireChannelActive();
    /**
     * TCP连接关闭
     */
    ChannelInboundInvoker fireChannelInactive();
    /**
     * 异常通知事件
     */
    ChannelInboundInvoker fireExceptionCaught(Throwable cause);
    /**
     * 用户自定义事件
     */
    ChannelInboundInvoker fireUserEventTriggered(Object event);
    /**
     * A {@link Channel} received a message.
     * 一个Channel接收到消息,
     * 这个方法会让ChannelPipeline中下一个 ChannelInboundHandler 的 方法被执行
     */
    ChannelInboundInvoker fireChannelRead(Object msg);
    /**
     * 读操作完成通知
     */
    ChannelInboundInvoker fireChannelReadComplete();
    /**
     * 用户的可写状态变换事件
     */
    ChannelInboundInvoker fireChannelWritabilityChanged();
}

```

### ChannelOutboundInvoker

![image-20200407211759168](D:\data\notes\notes\java\netty\netty源码\ChannelOutboundHandler\image-20200407211759168.png)

## ChannelHandler

`ChannelHandler`

```java
public interface ChannelHandler {

    /**
     * ChannelHandler 被添加到 上下文时方法执行并且准备处理事件
     */
    void handlerAdded(ChannelHandlerContext ctx) throws Exception;

    /**
     * ChannelHandler 从上下文被移除时执行
     */
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception;
}
```

## `ChannelInboundHandler`

```java
package io.netty.channel;

/**
 * {@link ChannelHandler} which adds callbacks for state changes. This allows the user
 * to hook in to state changes easily.
 */
public interface ChannelInboundHandler extends ChannelHandler {

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered with its {@link EventLoop}
     * ChannelHandlerContext 被注册到 EventLoop
     */
    void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was unregistered from its {@link EventLoop}
     * ChannelHandlerContext 从 EventLoop中移除注册
     */
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} is now active
     * ChannelHandlerContext 被激活
     */
    void channelActive(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its end of lifetime.
     * 注册的ChannelHandlerContext处于 inactive 并且到达了生命周期
     */
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    /**
     * Invoked when the current {@link Channel} has read a message from the peer.
     * 当前 Channel 读取到信息时执行
     */
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    /**
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     */
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    /**
     * 用户事件被触发时执行
     */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * Gets called once the writable state of a {@link Channel} changed. You can check the state with
     * {@link Channel#isWritable()}.
     * 当 Channel 的写状态改变时
     */
    void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     */
    @Override
    @SuppressWarnings("deprecation")
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
```

### ChannelInboundHandler解析

`ChannelInboundHandler`用于对入站事件的处理

### `@Sharable`注解

```java
public interface Message {
    // your methods here
}
  
@Sharable
public class DataServerHandler extends SimpleChannelInboundHandler<Message> {
    private final AttributeKey<Boolean> auth =
        AttributeKey.valueOf("auth");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Message message) {
        Attribute<Boolean> attr = ctx.attr(auth);
        if (message instanceof LoginMessage) {
            authenticate((LoginMessage) o);
            attr.set(true);
        } else (message instanceof GetDataMessage) {
            if (Boolean.TRUE.equals(attr.get())) {
                ctx.writeAndFlush(fetchSecret((GetDataMessage) o));
            } else {
                fail();
            }
        }
    }
}
```

```java
public class DataServerInitializer extends ChannelInitializer<Channel> {

    private static final DataServerHandler SHARED = new DataServerHandler();

    @Override
    public void initChannel(Channel channel) {
        channel.pipeline().addLast("handler", SHARED);
    }
}
```

```
如果 ChannelHandler标记了@Sharable注解,这个 handler实例只需要创建一次*  就可以将它添加到一个或者多个 ChannelPipeline 中多次
```

### `@Skip`

@Skip：被Skip注解的方法不会被调用，直接被忽略



## NioEventLoopGroup

![image-20200407223727841](D:\data\notes\notes\java\netty\netty源码\ChannelOutboundHandler\image-20200407223727841.png)

### NioEventLoop

## ByteToMessageDecoder

从`byteBuf`转换成另外一种消息类型

## ChannelOption

```java
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AbstractConstant;
import io.netty.util.ConstantPool;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * A {@link ChannelOption} allows to configure a {@link ChannelConfig} in a type-safe
 * way. Which {@link ChannelOption} is supported depends on the actual implementation
 * of {@link ChannelConfig} and may depend on the nature of the transport it belongs
 * to.
 *
 * @param <T>   the type of the value which is valid for the {@link ChannelOption}
 */
public class ChannelOption<T> extends AbstractConstant<ChannelOption<T>> {

    private static final ConstantPool<ChannelOption<Object>> pool = new ConstantPool<ChannelOption<Object>>() {
        @Override
        protected ChannelOption<Object> newConstant(int id, String name) {
            return new ChannelOption<Object>(id, name);
        }
    };

    /**
     * Returns the {@link ChannelOption} of the specified name.
     */
    @SuppressWarnings("unchecked")
    public static <T> ChannelOption<T> valueOf(String name) {
        return (ChannelOption<T>) pool.valueOf(name);
    }

    /**
     * Shortcut of {@link #valueOf(String) valueOf(firstNameComponent.getName() + "#" + secondNameComponent)}.
     */
    @SuppressWarnings("unchecked")
    public static <T> ChannelOption<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return (ChannelOption<T>) pool.valueOf(firstNameComponent, secondNameComponent);
    }

    /**
     * Returns {@code true} if a {@link ChannelOption} exists for the given {@code name}.
     */
    public static boolean exists(String name) {
        return pool.exists(name);
    }

    /**
     * Creates a new {@link ChannelOption} for the given {@code name} or fail with an
     * {@link IllegalArgumentException} if a {@link ChannelOption} for the given {@code name} exists.
     *
     * @deprecated use {@link #valueOf(String)}.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> ChannelOption<T> newInstance(String name) {
        return (ChannelOption<T>) pool.newInstance(name);
    }

    public static final ChannelOption<ByteBufAllocator> ALLOCATOR = valueOf("ALLOCATOR");
    public static final ChannelOption<RecvByteBufAllocator> RCVBUF_ALLOCATOR = valueOf("RCVBUF_ALLOCATOR");
    public static final ChannelOption<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = valueOf("MESSAGE_SIZE_ESTIMATOR");

    public static final ChannelOption<Integer> CONNECT_TIMEOUT_MILLIS = valueOf("CONNECT_TIMEOUT_MILLIS");
    /**
     * @deprecated Use {@link MaxMessagesRecvByteBufAllocator}
     * and {@link MaxMessagesRecvByteBufAllocator#maxMessagesPerRead(int)}.
     */
    @Deprecated
    public static final ChannelOption<Integer> MAX_MESSAGES_PER_READ = valueOf("MAX_MESSAGES_PER_READ");
    public static final ChannelOption<Integer> WRITE_SPIN_COUNT = valueOf("WRITE_SPIN_COUNT");
    /**
     * @deprecated Use {@link #WRITE_BUFFER_WATER_MARK}
     */
    @Deprecated
    public static final ChannelOption<Integer> WRITE_BUFFER_HIGH_WATER_MARK = valueOf("WRITE_BUFFER_HIGH_WATER_MARK");
    /**
     * @deprecated Use {@link #WRITE_BUFFER_WATER_MARK}
     */
    @Deprecated
    public static final ChannelOption<Integer> WRITE_BUFFER_LOW_WATER_MARK = valueOf("WRITE_BUFFER_LOW_WATER_MARK");
    public static final ChannelOption<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK =
            valueOf("WRITE_BUFFER_WATER_MARK");

    public static final ChannelOption<Boolean> ALLOW_HALF_CLOSURE = valueOf("ALLOW_HALF_CLOSURE");
    public static final ChannelOption<Boolean> AUTO_READ = valueOf("AUTO_READ");

    /**
     * If {@code true} then the {@link Channel} is closed automatically and immediately on write failure.
     * The default value is {@code true}.
     */
    public static final ChannelOption<Boolean> AUTO_CLOSE = valueOf("AUTO_CLOSE");

    public static final ChannelOption<Boolean> SO_BROADCAST = valueOf("SO_BROADCAST");
    /**参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的
     　　　　连接。当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文*/
    public static final ChannelOption<Boolean> SO_KEEPALIVE = valueOf("SO_KEEPALIVE");
    /**操作发送缓冲区的大小,发送缓冲区用于保存发送数据，直到发送成功*/
    public static final ChannelOption<Integer> SO_SNDBUF = valueOf("SO_SNDBUF");
    /**操作接收缓冲区的大小,接收缓冲区用于保存网络协议站内收到的数据，直到应用程序读取成功*/
    public static final ChannelOption<Integer> SO_RCVBUF = valueOf("SO_RCVBUF");
    /**表示允许重复使用本地地址和端口*/
    public static final ChannelOption<Boolean> SO_REUSEADDR = valueOf("SO_REUSEADDR");
    /**使用SO_LINGER可以阻塞close()的调用时间，直到数据完全发送*/
    public static final ChannelOption<Integer> SO_LINGER = valueOf("SO_LINGER");
    /**服务端可连接队列的大小*/
    public static final ChannelOption<Integer> SO_BACKLOG = valueOf("SO_BACKLOG");
    public static final ChannelOption<Integer> SO_TIMEOUT = valueOf("SO_TIMEOUT");

    public static final ChannelOption<Integer> IP_TOS = valueOf("IP_TOS");
    public static final ChannelOption<InetAddress> IP_MULTICAST_ADDR = valueOf("IP_MULTICAST_ADDR");
    public static final ChannelOption<NetworkInterface> IP_MULTICAST_IF = valueOf("IP_MULTICAST_IF");
    public static final ChannelOption<Integer> IP_MULTICAST_TTL = valueOf("IP_MULTICAST_TTL");
    public static final ChannelOption<Boolean> IP_MULTICAST_LOOP_DISABLED = valueOf("IP_MULTICAST_LOOP_DISABLED");

    public static final ChannelOption<Boolean> TCP_NODELAY = valueOf("TCP_NODELAY");

    @Deprecated
    public static final ChannelOption<Boolean> DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION =
            valueOf("DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION");

    public static final ChannelOption<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP =
            valueOf("SINGLE_EVENTEXECUTOR_PER_GROUP");

    /**
     * Creates a new {@link ChannelOption} with the specified unique {@code name}.
     */
    private ChannelOption(int id, String name) {
        super(id, name);
    }

    @Deprecated
    protected ChannelOption(String name) {
        this(pool.nextId(), name);
    }

    /**
     * Validate the value which is set for the {@link ChannelOption}. Sub-classes
     * may override this for special checks.
     */
    public void validate(T value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
    }
}
```



## 示例执行流程

### Server端

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TestServer {

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TestServerInitializer());
//                    .option(ChannelOption.SO_BACKLOG,128)
//                    .childOption(ChannelOption.SO_KEEPALIVE,false);

            ChannelFuture f = serverBootstrap.bind(9090).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
```

```java

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TestServerInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new StringDecoder());
        pipeline.addLast("testHttpServletHandler",new FirstHandler());
    }
}
```

```java
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FirstHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");
        System.out.println("channelRead msg : "+msg);
        ctx.writeAndFlush("服务端发送的消息");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered");
        System.out.println("userEventTriggered evt : "+evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelWritabilityChanged");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved");
    }
}

```

### Client客户端

```java
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TestClient {

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast("TimeHandler",new TestClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect("127.0.0.1", 9090);
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }

    }
}
```

```java
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class TestClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("来自服务端消息 : "+msg);
//            ctx.writeAndFlush("channelRead客户端消息");
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush("来自客户端消息");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

启动服务端,再启动客户端

1. 如果 `channelActive` 不发送给服务端消息

```java
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush("来自客户端消息");
    }
```

执行结果为

```java
handlerAdded
channelRegistered
channelActive
```

就会只执行这三个方法

然后关闭客户端会输出

```java
channelReadComplete
exceptionCaught
userEventTriggered
userEventTriggered evt : io.netty.channel.socket.ChannelInputShutdownReadComplete@6457be4b
channelInactive
channelUnregistered
handlerRemoved
```

2. 当客户端向服务端发送消息时

   ```java
       @Override
       public void channelActive(ChannelHandlerContext ctx) throws Exception {
          ctx.writeAndFlush("来自客户端消息");
       }
   ```

输出结果为

```java
handlerAdded
channelRegistered
channelActive
channelRead
channelRead msg : 来自客户端消息
channelReadComplete
```

再关闭客户端,控制台输出

```java
channelReadComplete
exceptionCaught
userEventTriggered
userEventTriggered evt : io.netty.channel.socket.ChannelInputShutdownReadComplete@6457be4b
channelInactive
channelUnregistered
handlerRemoved
```

