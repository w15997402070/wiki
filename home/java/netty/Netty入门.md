# Netty 处理http

`TestServerInitializer`

```java
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        pipeline.addLast("testHttpServletHandler",new TestHttpServletHandler());
    }
}
```

`TestHttpServletHandler`

```java
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class TestHttpServletHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        ctx.writeAndFlush(response);
    }
}
```





测试类

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
                    .childHandler(new TestServerInitializer())
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture f = serverBootstrap.bind(8888).sync();
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



## Netty相关知识点

```java
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//1
                    .childHandler(new ChatServerInitializer());//2


            ChannelFuture cf = serverBootstrap.bind(8899).sync();
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }       
```

上面代码中 `serverBootstrap.handler()`方法是相对于`bossGroup`

`serverBootstrap.childHandler()`方法是相对于`workerGroup`



```java
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //Delimiters.lineDelimiter() 这个表示每一条消息之后都需要加'\n'或'\r\n',不然消息接收端的消息接收不到
        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ChatClientHandler());
    }
}
```

`new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter())`每条消息后面都要加分隔符

`\r\n`或`\n`



## 粘包/拆包

TCP是 `流` 协议 流数据,就是没有界限的一串数据.
一个完整的包可能会被TCP拆分成多个包进行发送,也有可能把多个小的包封装成一个大的数据包发送,这就是TCP粘包和拆包问题

例如:

![image-20200308211819522](D:\data\notes\notes\java\netty\Untitled\image-20200308211819522.png)

假设客户端分别发送了两个数据包D1和D2给服务端,由于服务器一次读取到的字节数不确定的,所以可能存在以下情况

1. 服务端分两次读取收到两个独立的数据包,分别是D1和D2,没有粘包和拆包
2. 服务端一次接收到了两个数据包,D1和D2沾在一起,被称为TCP粘包
3. 服务端分两次读取到了两个数据包,第一次读取到了完整的D1包和部分的D2的包,第二次读取到了D2包的剩余内容,这被称为TCP拆包
4. 服务端分两次读取到了两个数据包,第一次读取到D1包的部分内容D1_1,第二次读取到了D1包的剩余内容D1_2和D2包的整包

如果服务器TCP接收滑窗非常小,而数据包D1和D2比较大,但很可能会发生第五种可能,即服务端多次才能将D1和D2包接收完全,期间发生多次拆包.

TCP粘包/拆包产生的原因:

1. 应用程序write写入的字节大小大于套接口发送缓冲区大小
2. 进行MSS大小的TCP分段
3. 以太网帧的playload大于MTU进行IP分片

主流解决方案:

（1）消息定长，例如每个报文的大小为固定长度200字节，如果不够，空位补空格；
        （2）在包尾增加回车换行符进行分割，例如FTP协议；
        （3）将消息分为消息头和消息体，消息头中包含表示消息总长度（或者消息体长度）的字段，通常设计思路为消息头的第一个字段使用int32来表示消息的总长度；
        （4）更复杂的应用层协议。

### netty提供的粘包/拆包处理器

1. `LineBasedFrameDecoder`以`\n`或`\r\n`结尾就认为消息结束,再用`StringDecoder`对消息字节流解码成字符串

2. `DelimiterBasedFrameDecoder`自动完成以分隔符做结束标志的消息解码
3. `FixedLengthFrameDecoder`自动完成对定消息的解码





## Netty核心组件

* Channel

* 回调

* Future

  ChannelFuture

* 事件和ChannelHandler

### Channel

主要方法

```md
bind() 
connect()
read()
write()
```

### EventLoop接口

* 一个 EventLoopGroup 包含一个或者多个 EventLoop；
* 一个 EventLoop 在它的生命周期内只和一个 Thread 绑定；
* 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理；
* 一个 Channel 在它的生命周期内只注册于一个 EventLoop；
* 一个 EventLoop 可能会被分配给一个或多个 Channel。  

### ChannelFuture 接口  

### ChannelHandler 接口  

### ChannelPipeline 接口  

ChannelHandler 安装到 ChannelPipeline 中的过程如下所示：

* 一个ChannelInitializer的实现被注册到了ServerBootstrap中 ①；
* 当 ChannelInitializer.initChannel()方法被调用时， ChannelInitializer
  将在 ChannelPipeline 中安装一组自定义的 ChannelHandler；
* ChannelInitializer 将它自己从 ChannelPipeline 中移除。  