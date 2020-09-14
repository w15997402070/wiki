# Disruptor

[toc]

## Disruptor核心

![img](https://img2018.cnblogs.com/blog/1230278/201901/1230278-20190109142457016-825831798.png)

### RingBuffer,Disruptor

RingBuffer拥有一个序号,这个序号指向数组中下一个可用元素,基于数组的缓存实现,也是创建sequencer与定义WaitStrategy的入口

![RingBuffer继承图](D:\data\notes\notes\disruptor\disruptor\image-20200426202404939.png)

Disruptor: 持有RingBuffer,消费者线程池Executor,消费者集合ConsumerRepository等引用

### Sequence,Sequence Barrier

Sequence: 通过顺序递增的序号来编号,管理进行交换的数据(事件)

​                   对数据(事件)的处理过程总是沿着序号逐个递增处理

​                  一个Sequence用于跟踪标识某个特定的事件处理者(RingBuffer/Producer/Consumer)的处理进度

​                  可以看成是一个AtomicLong用于标识进度

​                  另外一个目的就是防止不同Sequence之间CPU缓存伪共享(Flase Sharing)的问题

Sequencer

```java
public interface Sequencer extends Cursored, Sequenced{
    long INITIAL_CURSOR_VALUE = -1L;
    //声明一个指定的序列
    void claim(long sequence);
    //确定一个序列已经发布并且这个事件可以使用
    boolean isAvailable(long sequence);
    //添加指定的门控序列到 Disruptor实例中
     void addGatingSequences(Sequence... var1);
    //移除指定的序列
    boolean removeGatingSequence(Sequence var1);
    //创建一个序列屏障
    SequenceBarrier newBarrier(Sequence... var1);
    //获取最小的序列值
    long getMinimumSequence();
    //获取最大的序列值
    long getHighestPublishedSequence(long var1, long var3);

    <T> EventPoller<T> newPoller(DataProvider<T> var1, Sequence... var2);
}
```

![image-20200427095906851](D:\data\notes\notes\disruptor\disruptor\image-20200427095906851.png)

Sequencer: 主要有两个接口实现类

		* MultiProducerSequencer
		* SingleProducerSequencer

主要实现生产者和消费者之间快速,正确地传递数据的并发算法

Sequence Barrier: 用于保持对RingBuffer的Main Published Sequence(Producer)和consumer之间的平衡关系;还定义了决定Consumer是否还有可处理的事件的逻辑

### WaitStrategy等待策略

决定一个消费者将如何等待生产者将Event置入Disruptor

主要策略有:

​	BlockingWaitStrategy:使用锁和条件变量, 效率较低, 但CPU的消耗最小, 在不同部署环境下性能表现比较一致

​    SleepingWaitStrategy: 多次循环尝试不成功后, 让出CPU, 等待下次调度; 多次调度后仍不成功, 睡眠纳秒级别的时间再尝试. 平衡了延迟和CPU资源占用, 但延迟不均匀.

​    YieldingWaitStrategy:多次循环尝试不成功后, 让出CPU, 等待下次调度. 平衡了延迟和CPU资源占用, 延迟也比较均匀

### Event,EventProcessor核心线程

Event : 从生产者到消费者过程中所处理的数据单元,由用户定义

EventProcessor:主要事件循环,处理Disruptor中的Event,拥有消费者的Sequence,它有一个实现类BatchEventProcessor,包含了 event loop有效的实现,并且将回调到一个EventHandler接口的实现对象

### EventHandler消费者处理器

EventHandler:由用户实现并且代表了Disruptor中的一个消费者的接口,消费者逻辑都需要写在这里

### WorkProcessor核心工作器

WorkProcessor: 确保每个Sequence只被一个Processor消费,在同一个workPool中处理多个WorkProcessor不会消费同样的sequence



数据结构层面:使用环形结构,数组,内存预加载

使用单线程写方式,内存屏障

消除伪共享(填充缓存行)

序号栅栏和序号配合使用来消除锁和CAS

##  Quick Start

建立一个Event工厂类,用于创建Event类实例对象

需要有一个监听事件类,用于处理数据(Event类)

实例化Disruptor实例,配置一系列参数,编写Disruptor核心组件

编写生产者组件,向Disruptor容器中去投递数据

## 源码



`Disruptor#start()`

```java
public RingBuffer<T> start(){
    //检查是否已经启动
    checkOnlyStartedOnce();
    for (final ConsumerInfo consumerInfo : consumerRepository)
    {
        consumerInfo.start(executor);
    }

    return ringBuffer;
}
```

consumerRepository就是在 `Disruptor#handleEventsWith()`方法调用`Disruptor#createEventProcessors()`方法里面设置

```java
public final EventHandlerGroup<T> handleEventsWith(final EventHandler<? super T>... handlers){
    return createEventProcessors(new Sequence[0], handlers);
}

EventHandlerGroup<T> createEventProcessors(
    final Sequence[] barrierSequences,
    final EventHandler<? super T>[] eventHandlers){
    //检查是否已经启动,在启动之前所有的eventHandlers都应该先加进去
    checkNotStarted();
    final Sequence[] processorSequences = new Sequence[eventHandlers.length];
    // barrier = ProcessingSequenceBarrier
    final SequenceBarrier barrier = ringBuffer.newBarrier(barrierSequences);
    for (int i = 0, eventHandlersLength = eventHandlers.length; i < eventHandlersLength; i++)
    {
        final EventHandler<? super T> eventHandler = eventHandlers[i];
        //batchEventProcessor 是存储了消费者和生产者的执行器，实现了 Runnable 接口，内部会不断循环去接收并处理事件
        final BatchEventProcessor<T> batchEventProcessor =
            new BatchEventProcessor<>(ringBuffer, barrier, eventHandler);

        if (exceptionHandler != null)
        {
            batchEventProcessor.setExceptionHandler(exceptionHandler);
        }

        //consumerRepository 可以看做是消费者的集合封装
        //consumerRepository 会将传入的三个参数包装成 EventProcessorInfo 并储存在集合和 map 里
        consumerRepository.add(batchEventProcessor, eventHandler, barrier);
        //记录下消费者对应的执行器的序列号
        processorSequences[i] = batchEventProcessor.getSequence();
    }
    //处理前置事件
    updateGatingSequencesForNextInChain(barrierSequences, processorSequences);
    return new EventHandlerGroup<>(this, consumerRepository, processorSequences);
}

```

`consumerRepository.add(batchEventProcessor, eventHandler, barrier);`解析

`ConsumerRepository#add()`方法

```java
 public void add(
        final EventProcessor eventprocessor,
        final EventHandler<? super T> handler,
        final SequenceBarrier barrier){
        final EventProcessorInfo<T> consumerInfo = new EventProcessorInfo<>(eventprocessor, handler, barrier);
     //private final Map<EventHandler<?>, EventProcessorInfo<T>> eventProcessorInfoByEventHandler = new IdentityHashMap<>();
        eventProcessorInfoByEventHandler.put(handler, consumerInfo);
     //private final Map<Sequence, ConsumerInfo> eventProcessorInfoBySequence = new IdentityHashMap<>();
        eventProcessorInfoBySequence.put(eventprocessor.getSequence(), consumerInfo);
     
     //private final Collection<ConsumerInfo> consumerInfos = new ArrayList<>();
        consumerInfos.add(consumerInfo);
    }
```



ConsumerRepository类实现`Iterable<ConsumerInfo>`接口所以可以遍历

`ConsumerRepository#iterator()`

```java
@Override
public Iterator<ConsumerInfo> iterator()
{   
    return consumerInfos.iterator();
}
```

获取的是 consumerInfos 里的元素, consumerInfos就是上面看到的一个集合元素就是`consumerInfo`也就是`EventProcessorInfo<>(eventprocessor, handler, barrier)`

然后`consumerInfo.start(executor)`这个方法会用自己设置的执行器启动线程,调用`BatchEventProcessor`的`run`方法,`BatchEventProcessor`实现了` EventProcessor`而`EventProcessor`继承了`Runnable`接口.

`BatchEventProcessor#run()`

```java
@Override
public void run(){
    //private final AtomicInteger running = new AtomicInteger(IDLE);IDLE默认=0
    if (running.compareAndSet(IDLE, RUNNING)){
        sequenceBarrier.clearAlert();

        notifyStart();
        try{
            if (running.get() == RUNNING){
                processEvents();
            }
        }finally{
            notifyShutdown();
            running.set(IDLE);
        }
    }else{
        // This is a little bit of guess work.  The running state could of changed to HALTED by
        // this point.  However, Java does not have compareAndExchange which is the only way
        // to get it exactly correct.
        if (running.get() == RUNNING){
            throw new IllegalStateException("Thread is already running");
        }else{
            earlyExit();
        }
    }
}
```

