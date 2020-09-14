# Netty Server启动分析



## pipeline创建时机

`NioServerSocketChannel`分析pipeline创建过程

![NioServerSocketChannel继承关系图](D:\data\notes\notes\java\netty\netty源码\Server启动分析\image-20200319222359911.png)

最终会在`AbstractChannel`创建pipeline

```java
public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {
    private final DefaultChannelPipeline pipeline;
    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        id = newId();
        unsafe = newUnsafe();
        //在这里创建pipeline
        pipeline = newChannelPipeline();
    }
}
    
```

创建过程分析:

`ServerBootstrap.bind(port)`方法是调用父类的方法

AbstractBootstrap # bind()

```java
	public ChannelFuture bind(int inetPort) {
        return bind(new InetSocketAddress(inetPort));
    }

	public ChannelFuture bind(SocketAddress localAddress) {
        validate();
        return doBind(ObjectUtil.checkNotNull(localAddress, "localAddress"));
    }
```

调用doBind()方法

AbstractBootstrap # doBind()

```java
	    private ChannelFuture doBind(final SocketAddress localAddress) {
            //这里初始化Channel
        final ChannelFuture regFuture = initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }

        if (regFuture.isDone()) {
            // At this point we know that the registration was complete and successful.
            ChannelPromise promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        } else {
            // Registration future is almost always fulfilled already, but just in case it's not.
            final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
            regFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                        // IllegalStateException once we try to access the EventLoop of the Channel.
                        promise.setFailure(cause);
                    } else {
                        // Registration was successful, so set the correct executor to use.
                        // See https://github.com/netty/netty/issues/2586
                        promise.registered();

                        doBind0(regFuture, channel, localAddress, promise);
                    }
                }
            });
            return promise;
        }
    }
```

查看初始化Channel的方法

AbstractBootstrap # initAndRegister()

```java
	 final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            //这里从 channelFactory 工厂中利用反射创建Channel,
            channel = channelFactory.newChannel();
            init(channel);
        } catch (Throwable t) {
            if (channel != null) {
                // channel can be null if newChannel crashed (eg SocketException("too many open files"))
                channel.unsafe().closeForcibly();
                // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
                return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
            }
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
        }

        ChannelFuture regFuture = config().group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }

        // If we are here and the promise is not failed, it's one of the following cases:
        // 1) If we attempted registration from the event loop, the registration has been completed at this point.
        //    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
        // 2) If we attempted registration from the other thread, the registration request has been successfully
        //    added to the event loop's task queue for later execution.
        //    i.e. It's safe to attempt bind() or connect() now:
        //         because bind() or connect() will be executed *after* the scheduled registration task is executed
        //         because register(), bind(), and connect() are all bound to the same thread.

        return regFuture;
    }
```

`channelFactory.newChannel()`创建Channel时会根据`ServerBootstrap .channel(NioServerSocketChannel.class)`方法传入的参数通过反射创建实例,查看`NioServerSocketChannel`创建实例时构造方法中做了什么

`NioServerSocketChannel`代码

```java
public class NioServerSocketChannel extends AbstractNioMessageChannel
                             implements io.netty.channel.socket.ServerSocketChannel {
    //反射创建时通过默认的构造方法创建
    public NioServerSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }
    //上面会调用这个构造方法
    public NioServerSocketChannel(ServerSocketChannel channel) {
        super(null, channel, SelectionKey.OP_ACCEPT);
        config = new NioServerSocketChannelConfig(this, javaChannel().socket());
    }
}
```

会调用`NioServerSocketChannel`父类`AbstractNioMessageChannel `的构造方法

```java
public abstract class AbstractNioMessageChannel extends AbstractNioChannel {
     
    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }
}
```

在继续往上调用父类构造方法`AbstractNioChannel`构造方法

```java
public abstract class AbstractNioChannel extends AbstractChannel {
    protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent);
        this.ch = ch;
        this.readInterestOp = readInterestOp;
        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            try {
                ch.close();
            } catch (IOException e2) {
                logger.warn(
                            "Failed to close a partially initialized socket.", e2);
            }

            throw new ChannelException("Failed to enter non-blocking mode.", e);
        }
    }
}
```

再继续往上调用父类`AbstractChannel`构造方法

```java
public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {   
    private final DefaultChannelPipeline pipeline;
    //这里终于到了创建pipeline的方法
    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        id = newId();
        unsafe = newUnsafe();
        pipeline = newChannelPipeline();
    }
}
```

`DefaultChannelPipeline`

```java
public class DefaultChannelPipeline implements ChannelPipeline {
    
    protected DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
        succeededFuture = new SucceededChannelFuture(channel, null);
        voidPromise =  new VoidChannelPromise(channel, true);

        tail = new TailContext(this);
        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }
}
```

## ChannelFuture 创建分析

AbstractBootstrap # doBind() 

```java
private ChannelFuture doBind(final SocketAddress localAddress) {
        final ChannelFuture regFuture = initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }

        if (regFuture.isDone()) {
            // At this point we know that the registration was complete and successful.
            ChannelPromise promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        } else {
            // Registration future is almost always fulfilled already, but just in case it's not.
            final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
            regFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                        // IllegalStateException once we try to access the EventLoop of the Channel.
                        promise.setFailure(cause);
                    } else {
                        // Registration was successful, so set the correct executor to use.
                        // See https://github.com/netty/netty/issues/2586
                        promise.registered();

                        doBind0(regFuture, channel, localAddress, promise);
                    }
                }
            });
            return promise;
        }
    }
```

AbstractBootstrap # initAndRegister() 

```java
final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            channel = channelFactory.newChannel();
            init(channel);
        } catch (Throwable t) {
            if (channel != null) {
                // channel can be null if newChannel crashed (eg SocketException("too many open files"))
                channel.unsafe().closeForcibly();
                // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
                return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
            }
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        //这里创建 ChannelFuture
        // config() -> ServerBootstrapConfig
        // group() -> NioEventLoopGroup
        ChannelFuture regFuture = config().group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }

        // If we are here and the promise is not failed, it's one of the following cases:
        // 1) If we attempted registration from the event loop, the registration has been completed at this point.
        //    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
        // 2) If we attempted registration from the other thread, the registration request has been successfully
        //    added to the event loop's task queue for later execution.
        //    i.e. It's safe to attempt bind() or connect() now:
        //         because bind() or connect() will be executed *after* the scheduled registration task is executed
        //         because register(), bind(), and connect() are all bound to the same thread.

        return regFuture;
    }
```

`config()` 方法调用 ServerBootstrap # config() 方法 返回

```java
	// this 就是 ServerBootstrap 
	private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
	@Override
    public final ServerBootstrapConfig config() {
        return config;
    }
```

`register(channel)`调用  `NioEventLoopGroup`的 `register(Channel channel)`,`NioEventLoopGroup`继承`MultithreadEventLoopGroup `所以最终会调用

MultithreadEventLoopGroup # register(Channel channel)

```java
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {
     @Override
    public ChannelFuture register(Channel channel) {
        return next().register(channel);
    }
    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }
}
```

### next()方法分析

MultithreadEventLoopGroup # next() 方法调用父类next()方法,代码如下

```java
public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
    //chooser 在构造函数protected MultithreadEventExecutorGroup(int nThreads, Executor executor,EventExecutorChooserFactory chooserFactory, Object... args)中创建
    //chooser = chooserFactory.newChooser(children);
    private final EventExecutorChooserFactory.EventExecutorChooser chooser;
    

    @Override
    public EventExecutor next() {
        return chooser.next();
    }
}
```

`MultithreadEventExecutorGroup`的构造函数中会创建`EventExecutor []`组数

** EventExecutor 是一个特殊的`EventExecutorGroup`,定义了一些的方便判断线程是否在Event Loop(事件循环)中执行.并且扩展了`EventExecutorGroup`允许访问通用的方法*

```java
public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
    //EventExecutor 数组
    private final EventExecutor[] children;
    
	protected MultithreadEventExecutorGroup(int nThreads, Executor executor,
                                            EventExecutorChooserFactory chooserFactory, Object... args) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
        }

        if (executor == null) {
            executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
        }

        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {
                // TODO: Think about if this is a good exception type
                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {
                if (!success) {
                    for (int j = 0; j < i; j ++) {
                        children[j].shutdownGracefully();
                    }

                    for (int j = 0; j < i; j ++) {
                        EventExecutor e = children[j];
                        try {
                            while (!e.isTerminated()) {
                                e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interrupted) {
                            // Let the caller handle the interruption.
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }

        chooser = chooserFactory.newChooser(children);

        final FutureListener<Object> terminationListener = new FutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                if (terminatedChildren.incrementAndGet() == children.length) {
                    terminationFuture.setSuccess(null);
                }
            }
        };

        for (EventExecutor e: children) {
            e.terminationFuture().addListener(terminationListener);
        }

        Set<EventExecutor> childrenSet = new LinkedHashSet<EventExecutor>(children.length);
        Collections.addAll(childrenSet, children);
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }
}
```

NioEventLoopGroup # newChild()方法创建`NIOEventLoop`

```java
@Override
    protected EventLoop newChild(Executor executor, Object... args) throws Exception {
        EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory) args[3] : null;	
        return new NioEventLoop(this, executor, (SelectorProvider) args[0],
            ((SelectStrategyFactory) args[1]).newSelectStrategy(), (RejectedExecutionHandler) args[2], queueFactory);
    }
```

所以`EventExecutor[] children`数组中存放的都是`NioEventLoop`

`EventExecutorChooserFactory chooserFactory`中的`chooserFactory`默认实现为`DefaultEventExecutorChooserFactory `创建方法如下

DefaultEventExecutorChooserFactory #  newChooser()

```java
public final class DefaultEventExecutorChooserFactory implements EventExecutorChooserFactory {
    @Override
    public EventExecutorChooser newChooser(EventExecutor[] executors) {
        if (isPowerOfTwo(executors.length)) {
            return new PowerOfTwoEventExecutorChooser(executors);
        } else {
            return new GenericEventExecutorChooser(executors);
        }
    }
    //创建的PowerOfTwoEventExecutorChooser 
    private static final class PowerOfTwoEventExecutorChooser implements EventExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final EventExecutor[] executors;

        PowerOfTwoEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            return executors[idx.getAndIncrement() & executors.length - 1];
        }
    }
    // 创建的GenericEventExecutorChooser 
    private static final class GenericEventExecutorChooser implements EventExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final EventExecutor[] executors;

        GenericEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            return executors[Math.abs(idx.getAndIncrement() % executors.length)];
        }
    }
}

```

`PowerOfTwoEventExecutorChooser`和`GenericEventExecutorChooser`是`DefaultEventExecutorChooserFactory`的两个私有的内部类

### register(channel) 方法分析

从上面分析可知`next()`方法会获取`NIOEventLoop`所以查看`NIOEventLoop`的`register()`方法

会调用父类`SingleThreadEventLoop`的`register(Channel channel)`方法

```java
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
    @Override
    public ChannelFuture register(Channel channel) {
        return register(new DefaultChannelPromise(channel, this));
    }
    
    @Override
    public ChannelFuture register(final ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }
}
```

最终会返回`DefaultChannelPromise`

