# Executors

[toc]

## 线程池类型

	*  newFixedThreadPool ：创建一个核心线程个数和最大线程个数都为nThreads的线程池，并且阻塞队列长度为Integer.MAX_VALUE。keeyAliveTime=0说明只要线程个数比核心线程个数多并且当前空闲则回收。
	*  newSingleThreadExecutor：创建一个核心线程个数和最大线程个数都为1的线程池，并且阻塞队列长度为Integer.MAX_VALUE。keeyAliveTime=0说明只要线程个数比核心线程个数多并且当前空闲则回收。
	*  newCachedThreadPool ：创建一个按需创建线程的线程池，初始线程个数为0，最多线程个数为Integer.MAX_VALUE，并且阻塞队列为同步队列。keeyAliveTime=60说明只要当前线程在60s内空闲则回收。这个类型的特殊之处在于，加入同步队列的任务会被马上执行，同步队列里面最多只有一个任务。



Worker继承AQS和Runnable接口，是具体承载任务的对象。Worker继承了AQS，自己实现了简单不可重入独占锁，其中state=0表示锁未被获取状态，state=1表示锁已经被获取的状态，state=-1是创建Worker时默认的状态，创建时状态设置为-1是为了避免该线程在运行runWorker（）方法前被中断，下面会具体讲解。其中变量firstTask记录该工作线程执行的第一个任务，thread是具体执行任务的线程。DefaultThreadFactory是线程工厂，newThread方法是对线程的一个修饰。其中poolNumber是个静态的原子变量，用来统计线程工厂的个数，threadNumber用来记录每个线程工厂创建了多少线程，这两个值也作为线程池和线程的名称的一部分。

```
newCachedThreadPool() 创建无界线程池,可以进行线程自动回收,池中存放线程个数是理论上的Integer.MAX_VALUE 最大值

shutdown() 使当前未执行完的线程继续执行,而不再添加新的任务task
shutdownNow() :中断所有的任务Task，并且抛出 InterruptedException 异常
isShutdown() 判断线程池是否已经关闭
getCorePoolSize()  获取核心线程
getPoolSize() 取得池中有多少个线程
getMaximumPoolSize() 获取最大线程
getActiveCount() 取得有多少个线程正在执行任务
getCompletedTaskCount() 取得有多少个线程已完成任务
getTaskCount() 取得有多少个任务发送给了线程
isTerminating() 如果正在执行的程序处于shutdown或shutdownNow之后处于正在终止但尚未完全终止的过程中，调用方法isTerminating()则返回true。此方法可以比喻成，门是否正在关闭。门彻底关闭时，线程池也就关闭了。
isTerminated() 如果线程池关闭后，也就是所有任务都已完成，则方法isTerminated()返回true。此方法可以比喻成，门是否已经关闭。
awaitTermination(long timeout, TimeUnit unit) 查看在指定的时间之间，线程池是否已经终止工作，也就是最多等待多少时间后去判断线程池是否已经终止工作。
setRejectedExecutionHandler(RejectedExecutionHandler handler) 处理任务被拒绝执行时的行为
getRejectedExecutionHandler() 
allowsCoreThreadTimeOut() 
allowCoreThreadTimeOut(boolean value) 配置核心线程是否有超时的效果
prestartCoreThread() 每调用一次就创建一个核心线程,返回值为boolean,含义是是否启动了
prestartAllCoreThreads() 启动全部核心线程,返回值是启动核心线程的数量
getCompletedTaskCount() 取得已经执行完成的任务数
beforeExecute(Thread t, Runnable r) 开始执行任务操作
afterExecute(Runnable r, Throwable t) 任务执行结束操作
remove(Runnable task)  删除尚未执行的 Runnable 任务

```

### newCachedThreadPool()

```
newCachedThreadPool() 
newCachedThreadPool(ThreadFactory threadFactory) 定制线程工厂
newFixedThreadPool(int nThreads)  创建有界线程池
```

### newSingleThreadExecutor()

```
newSingleThreadExecutor() 创建单一线程池
```



##  ThreadPoolExecutor

```java 
//(高3位)用来表示线程池状态,(低29位)用来表示线程个数
//默认是RUNNING状态,线程个数为0
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
//线程个数掩码位数,并不是所有平台的int类型都是32位的,所以准确地说,是具体平台下Integer的二进制位数-3后的剩余位数所表示的数才是线程的个数
private static final int COUNT_BITS = Integer.SIZE - 3;
//线程最大个数(低29位)00011111111111111111111111111
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
```

左移

```
将a的二进制数左移2位，右补0。若a=15，即二进制数00001111，左移2位得00111100，即十进制数60
高位左移后溢出，舍弃。
左移一位相当于该数乘以2，左移2位相当于该数乘以2^2=4。上面举的例子15<< 2=60，即乘了4。但此结论只适用于该数左移时被溢出舍弃的高位中不包含1的情况。
```



```java
//高3位 : 11100000000000000000000000000000
private static final int RUNNING    = -1 << COUNT_BITS;
//高3位 : 00000000000000000000000000000000
private static final int SHUTDOWN   =  0 << COUNT_BITS;
//高3位 : 00100000000000000000000000000000
private static final int STOP       =  1 << COUNT_BITS;
//高3位 : 01000000000000000000000000000000
private static final int TIDYING    =  2 << COUNT_BITS;
//高3位 : 01100000000000000000000000000000
private static final int TERMINATED =  3 << COUNT_BITS;

// Packing and unpacking ctl
//获取高3位运行状态 
private static int runStateOf(int c)     { return c & ~CAPACITY; }
//获取低29位线程个数
private static int workerCountOf(int c)  { return c & CAPACITY; }
//计算ctl新值(线程状态和线程个数)
private static int ctlOf(int rs, int wc) { return rs | wc; }
```

线程状态含义 : 

* RUNNING：接受新任务并且处理阻塞队列里的任务。
* SHUTDOWN：拒绝新任务但是处理阻塞队列里的任务。
* STOP：拒绝新任务并且抛弃阻塞队列里的任务，同时会中断正在处理的任务。
* TIDYING：所有任务都执行完（包含阻塞队列里面的任务）后当前线程池活动线程数为0，将要调用terminated方法。
* TERMINATED：终止状态。terminated方法调用完成以后的状态。

线程池状态转换: 

* RUNNING -> SHUTDOWN ：显式调用shutdown（）方法，或者隐式调用了finalize（）方法里面的shutdown（）方法。
* RUNNING或SHUTDOWN）-> STOP ：显式调用shutdownNow（）方法时。
* SHUTDOWN -> TIDYING ：当线程池和任务队列都为空时。
* STOP -> TIDYING ：当线程池为空时。
* TIDYING -> TERMINATED：当terminated（）hook方法执行完成时。

线程池参数 : 

```
ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory, RejectedExecutionHandler handler)

corePoolSize 池中所保存的线程数,包括空闲线程,也就是核心池的大小.线程池核心线程个数。
maximumPoolSize 池中允许的最大线程数
keepAliveTime 当线程数量大于 corePoolSize 值时,在没有超过指定的时间内是不从线程池中将空闲线程删除的,如果超过此时间单位,则删除
unit 参数的时间单位
workQueue 执行前用于保持任务的队列,此任务仅保持由 execute 方法提交的 Runneable 任务
threadFactory 定制线程工厂,创建线程的工厂。
RejectedExecutionHandler : 饱和策略，当队列满并且线程个数达到maximunPoolSize后采取的策略，比如AbortPolicy（抛出异常）、CallerRunsPolicy（使用调用者所在线程来运行任务）、DiscardOldestPolicy（调用poll丢弃一个任务，执行当前任务）及DiscardPolicy（默默丢弃，不抛出异常） 
```

![线程池解释](D:\data\notes\notes\java\线程池\Executors.assets\1567568211393.png)

### 1. 线程池数量 <=corePoolSize

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池数量 <=corePoolSize
 */
public class ThreadPoolTest1 {

    /**
     * 队列使用 LinkedBlockingDeque类
     * 并且线程数量 <= corePoolSize
     *  所以 keepAliveTime > 5 时也不清除空闲线程
     */
    public static void main (String [] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " run : "+ System.currentTimeMillis());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(7, 8, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        poolExecutor.execute(runnable);//1
        poolExecutor.execute(runnable);//2
        poolExecutor.execute(runnable);//3
        poolExecutor.execute(runnable);//4
        poolExecutor.execute(runnable);//5
        poolExecutor.execute(runnable);//6
        poolExecutor.execute(runnable);//7
        Thread.sleep(300);
        System.out.println("A : "+poolExecutor.getCorePoolSize());//车中可载人的标准人数
        System.out.println("A : "+poolExecutor.getPoolSize());//车中正在载的人数
        System.out.println("A : "+poolExecutor.getMaximumPoolSize());//车中可载人的最大人数
        System.out.println("A : "+poolExecutor.getQueue().size());//扩展车中正在载的人数
        Thread.sleep(1000);
        System.out.println("B : "+poolExecutor.getCorePoolSize());
        System.out.println("B : "+poolExecutor.getPoolSize());
        System.out.println("B : "+poolExecutor.getQueue().size());
    }
}

//结果
pool-1-thread-1 run : 1567569799142
pool-1-thread-4 run : 1567569799142
pool-1-thread-2 run : 1567569799142
pool-1-thread-3 run : 1567569799142
pool-1-thread-7 run : 1567569799142
pool-1-thread-5 run : 1567569799142
pool-1-thread-6 run : 1567569799142
A : 7
A : 7
A : 8
A : 0
B : 7
B : 7
B : 0
```

### 2. 数量 > corePoolSize 并且 <= maximumPoolSize

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数量 > corePoolSize 并且 <= maximumPoolSize
 */
public class ThreadPoolTest2 {

    /**
     * 队列使用 LinkedBlockingDeque 类,也就是如果
     * 线程数量 > corePoolSize 时将其余的任务放入队列中
     *  同一时间最多有7个线程在运行
     *  如果使用 LinkedBlockingDeque 类 则 maximumPoolSize 参数作用将忽略
     */
    public static void main (String [] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " run : "+ System.currentTimeMillis());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(7, 8, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        poolExecutor.execute(runnable);//1
        poolExecutor.execute(runnable);//2
        poolExecutor.execute(runnable);//3
        poolExecutor.execute(runnable);//4
        poolExecutor.execute(runnable);//5
        poolExecutor.execute(runnable);//6
        poolExecutor.execute(runnable);//7
        poolExecutor.execute(runnable);//8
        Thread.sleep(300);
        System.out.println("A : "+poolExecutor.getCorePoolSize());//车中可载人的标准人数
        System.out.println("A : "+poolExecutor.getPoolSize());//车中正在载的人数
        System.out.println("A : "+poolExecutor.getMaximumPoolSize());//车中可载人的最大人数
        System.out.println("A : "+poolExecutor.getQueue().size());//扩展车中正在载的人数
        Thread.sleep(1000);
        System.out.println("B : "+poolExecutor.getCorePoolSize());
        System.out.println("B : "+poolExecutor.getPoolSize());
        System.out.println("B : "+poolExecutor.getQueue().size());
        poolExecutor.shutdown();
    }
}

//结果
pool-1-thread-1 run : 1567569674089
pool-1-thread-4 run : 1567569674089
pool-1-thread-3 run : 1567569674089
pool-1-thread-2 run : 1567569674089
pool-1-thread-5 run : 1567569674089
pool-1-thread-6 run : 1567569674089
pool-1-thread-7 run : 1567569674089
A : 7
A : 7
A : 8
A : 1
pool-1-thread-5 run : 1567569675089
B : 7
B : 7
B : 0
```

```java
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数量 > corePoolSize 并且 <= maximumPoolSize
 */
public class ThreadPoolTest3 {

    /**
     * 队列使用 SynchronousQueue 类
     * 并且线程数量 > corePoolSize 时
     * 将其余的任务也放入池中,总数量为8
     * 并且线程总数量也没有超过 maximumPoolSize 值的8
     * 由于运行的线程数为8,数量上 > corePoolSize 为7的值
     * 所以 keepAliveTime > 5 时清除空闲线程
     * 如果使用 SynchronousQueue 类则 maximumPoolSize 参数的作用将有效
     */
    public static void main (String [] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " run : "+ System.currentTimeMillis());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(7, 8, 5,
                TimeUnit.SECONDS, new SynchronousQueue<>());
        poolExecutor.execute(runnable);//1
        poolExecutor.execute(runnable);//2
        poolExecutor.execute(runnable);//3
        poolExecutor.execute(runnable);//4
        poolExecutor.execute(runnable);//5
        poolExecutor.execute(runnable);//6
        poolExecutor.execute(runnable);//7
        poolExecutor.execute(runnable);//8
//        poolExecutor.execute(runnable);//9  大于 maximumPoolSize时会抛异常
        Thread.sleep(300);
        System.out.println("A : "+poolExecutor.getCorePoolSize());
        System.out.println("A : "+poolExecutor.getPoolSize());
        System.out.println("A : "+poolExecutor.getQueue().size());
        Thread.sleep(10000);
        System.out.println("B : "+poolExecutor.getCorePoolSize());
        System.out.println("B : "+poolExecutor.getPoolSize());
        System.out.println("B : "+poolExecutor.getQueue().size());
        poolExecutor.shutdown();
    }
}

//结果
pool-1-thread-1 run : 1567578955976
pool-1-thread-2 run : 1567578955976
pool-1-thread-4 run : 1567578955977
pool-1-thread-3 run : 1567578955977
pool-1-thread-5 run : 1567578955977
pool-1-thread-8 run : 1567578955977
pool-1-thread-6 run : 1567578955977
pool-1-thread-7 run : 1567578955977
A : 7
A : 8
A : 0
B : 7
B : 7
B : 0
```

### 3. 数量 > maximumPoolSize 的情况

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数量 > corePoolSize 并且 <= maximumPoolSize
 */
public class ThreadPoolTest4 {

    /**
     * 队列使用 LinkedBlockingDeque 类,
     * 并且线程数量 > corePoolSize 时将其余的任务放入队列中
     *  同一时间最多有 corePoolSize 个线程在运行
     *  如果使用 LinkedBlockingDeque 类 则 maximumPoolSize 参数作用将忽略
     *  所以 keepAliveTime > 5 时也不清除空闲线程
     */
    public static void main (String [] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " run : "+ System.currentTimeMillis());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(7, 8, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        //maximumPoolSize 改成10 
        //        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(7, 10, 5,
//                TimeUnit.SECONDS, new SynchronousQueue<>());
        poolExecutor.execute(runnable);//1
        poolExecutor.execute(runnable);//2
        poolExecutor.execute(runnable);//3
        poolExecutor.execute(runnable);//4
        poolExecutor.execute(runnable);//5
        poolExecutor.execute(runnable);//6
        poolExecutor.execute(runnable);//7
        poolExecutor.execute(runnable);//8
        poolExecutor.execute(runnable);//9
        Thread.sleep(300);
        System.out.println("A : "+poolExecutor.getCorePoolSize());//车中可载人的标准人数
        System.out.println("A : "+poolExecutor.getPoolSize());//车中正在载的人数
        System.out.println("A : "+poolExecutor.getQueue().size());//扩展车中正在载的人数
        Thread.sleep(1000);
        System.out.println("B : "+poolExecutor.getCorePoolSize());
        System.out.println("B : "+poolExecutor.getPoolSize());
        System.out.println("B : "+poolExecutor.getQueue().size());
        poolExecutor.shutdown();
    }
}

//队列使用 LinkedBlockingDeque 结果
pool-1-thread-1 run : 1567579369950
pool-1-thread-2 run : 1567579369950
pool-1-thread-4 run : 1567579369950
pool-1-thread-5 run : 1567579369951
pool-1-thread-3 run : 1567579369951
pool-1-thread-6 run : 1567579369951
pool-1-thread-7 run : 1567579369951
A : 7
A : 7
A : 2
pool-1-thread-2 run : 1567579370950
pool-1-thread-1 run : 1567579370950
B : 7
B : 7
B : 0
//队列使用 SynchronousQueue 类时 线程数量小于 >= corePoolSize 并且 < maximumPoolSize , keepAliveTime > 5时清除空闲线程  如果 > maximumPoolSize 就会抛出异常
pool-1-thread-2 run : 1567579925481
pool-1-thread-4 run : 1567579925481
pool-1-thread-1 run : 1567579925481
pool-1-thread-5 run : 1567579925481
pool-1-thread-6 run : 1567579925481
pool-1-thread-3 run : 1567579925481
pool-1-thread-8 run : 1567579925482
pool-1-thread-9 run : 1567579925482
pool-1-thread-7 run : 1567579925482
A : 7
A : 9
A : 0
B : 7
B : 9
B : 0
```

### 4. 参数 keepAliveTime 为 0 时

keepAliveTIme : 当线程数量大于 corePoolSize 值时,在没有超过指定的时间内是不从线程池中将空闲线程删除的,如果超过此时间单位,则删除,如果为0则任务执行完毕后立即从队列中删除.

unit : keepAliveTime 参数的时间单位

### 5. 方法 shutdown() 和 shutdownNow() 与返回值

shutdown() : 使当前未执行完的线程继续执行,而不再添加新的任务task,shutdown() 方法不会阻塞,调用shutdown() 方法后主线程 main 就马上结束了,而线程池会继续运行直到所有任务执行完才会停止.如果不调用shutdown() 方法,那么线程池会一直保持下去,以便随时执行被添加的 Task 任务.

shutdownNow() :中断所有的任务Task，并且抛出 InterruptedException 异常，前提是在 Runnable 中使用if（Thread.curentThread().isInterupted(）== true）语句来判断当前线程的中断状态，而未执行的线程不再执行，也就是从执行队列中清除。如果没有if（Thread.currentThread().isInterrupted(）== true）语句及抛出异常的代码，则池中正在运行的线程直到执行完毕，而未执行的线程不再执行，也从执行队列中清除。

### 6. isShutdown() 

```
isShutdown() 判断线程池是否已经关闭
```

## 线程池 ThreadPoolExecutor 的拒绝策略

- AbortPolicy：当任务添加到线程池中被拒绝时，它将抛出RejectedExecutionException异常。
- CallerRunsPolicy：当任务添加到线程池中被拒绝时，会使用调用线程池的Thread线程对象处理被拒绝的任务。
- DiscardOldestPolicy：当任务添加到线程池中被拒绝时，线程池会放弃等待队列中最旧的未处理任务，然后将被拒绝的任务添加到等待队列中。
- DiscardPolicy：当任务添加到线程池中被拒绝时，线程池将丢弃被拒绝的任务。

### 1. AbortPolicy策略

当任务添加到线程池中被拒绝时，它将抛出RejectedExecutionException异常。

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AbortPolicyTest {

    public static void main (String [] args){
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " run end !");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 2, 5,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.AbortPolicy());
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);//报错
        poolExecutor.shutdown();
    }
}

//结果,注释掉报错那一行就执行成功
Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task com.test.threadPool.AbortPolicyTest$$Lambda$1/758529971@782830e rejected from java.util.concurrent.ThreadPoolExecutor@470e2030[Running, pool size = 2, active threads = 2, queued tasks = 1, completed tasks = 0]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2047)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:823)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1369)
	at com.test.threadPool.AbortPolicyTest.main(AbortPolicyTest.java:24)
pool-1-thread-2 run end !
pool-1-thread-1 run end !
pool-1-thread-2 run end !
```

### 2. CallerRunsPolicy策略

当任务添加到线程池中被拒绝时，会使用调用线程池的Thread线程对象处理被拒绝的任务。

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CallerRunsPolicyTest {

    public static void main (String [] args){
        MyCallerRunsPolicyThread thread = new MyCallerRunsPolicyThread();
        LinkedBlockingDeque blockingDeque = new LinkedBlockingDeque<>(2);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 2, 5,
                TimeUnit.SECONDS, blockingDeque, new ThreadPoolExecutor.CallerRunsPolicy());
        System.out.println(" thread begin "+ Thread.currentThread().getName() + " Time : "+System.currentTimeMillis());
        poolExecutor.execute(thread);
        poolExecutor.execute(thread);
        poolExecutor.execute(thread);
        poolExecutor.execute(thread);
        poolExecutor.execute(thread);
        System.out.println(" thread end "+ Thread.currentThread().getName() + " Time : "+System.currentTimeMillis());
    }
}

class MyCallerRunsPolicyThread extends Thread{

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " run end ! Time "+ System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//结果
thread begin main Time : 1567592087845
main run end ! Time 1567592088845
pool-1-thread-1 run end ! Time 1567592088845
 thread end main Time : 1567592088845
pool-1-thread-2 run end ! Time 1567592088846
pool-1-thread-1 run end ! Time 1567592089845
pool-1-thread-2 run end ! Time 1567592089846
```

### 3. DiscardOldestPolicy策略

当任务添加到线程池中被拒绝时，线程池会放弃等待队列中最旧的未处理任务，然后将被拒绝的任务添加到等待队列中。

```java
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiscardOldestPolicyTest {

    public static void main (String [] args) throws InterruptedException {
        ArrayBlockingQueue queue = new ArrayBlockingQueue(2);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 3, 5,
                TimeUnit.SECONDS, queue, new ThreadPoolExecutor.DiscardOldestPolicy());
        for (int i = 0; i < 5; i++) {
            MyRunnable myRunnable = new MyRunnable("Runnable " + (i + 1));
            poolExecutor.execute(myRunnable);
        }
        Thread.sleep(50);
        Iterator iterator = queue.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            System.out.println("queue : "+((MyRunnable)next).getUserName());
        }
        poolExecutor.execute(new MyRunnable("Ruunnable6"));
        poolExecutor.execute(new MyRunnable("Ruunnable7"));
        iterator = queue.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            System.out.println("queue : "+((MyRunnable)next).getUserName());
        }
    }
}

class MyRunnable implements Runnable{

    private String userName;

    public MyRunnable(String userName){
        super();
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public void run() {
        try {
            System.out.println(userName + " run");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//结果
Runnable 1 run
Runnable 2 run
Runnable 5 run
queue : Runnable 3
queue : Runnable 4
queue : Ruunnable6
queue : Ruunnable7
Ruunnable6 run
Ruunnable7 run
```

### 4.DiscardPolicy策略

当任务添加到线程池中被拒绝时，线程池将丢弃被拒绝的任务。

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiscardPolicyTest {

    public static void main (String [] args) throws InterruptedException {
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " run end !");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ArrayBlockingQueue queue = new ArrayBlockingQueue(2);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 3, 5,
                TimeUnit.SECONDS, queue, new ThreadPoolExecutor.DiscardPolicy());
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        Thread.sleep(8000);
        System.out.println(poolExecutor.getPoolSize() + "  "+ queue.size());
    }
}

//结果
pool-1-thread-3 run end !
pool-1-thread-2 run end !
pool-1-thread-1 run end !
pool-1-thread-2 run end !
pool-1-thread-3 run end !
2  0	
```

## ScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor继承了ThreadPoolExecutor并实现了ScheduledExecutorService接口。线程池队列是DelayedWorkQueue，其和DelayedQueue类似，是一个延迟队列。

ScheduledFutureTask是具有返回值的任务，继承自FutureTask。FutureTask的内部有一个变量state用来表示任务的状态，一开始状态为NEW，所有状态为

```java
private volatile int state;
private static final int NEW          = 0;//初始状态
private static final int COMPLETING   = 1;//执行中状态
private static final int NORMAL       = 2;//正常运行结束状态
private static final int EXCEPTIONAL  = 3;//运行中异常
private static final int CANCELLED    = 4;//任务被取消
private static final int INTERRUPTING = 5;//任务正在被中断
private static final int INTERRUPTED  = 6;//任务已经被中断
```

可能的任务状态转换路径为

```java
 NEW -> COMPLETING -> NORMAL
 NEW -> COMPLETING -> EXCEPTIONAL
 NEW -> CANCELLED
 NEW -> INTERRUPTING -> INTERRUPTED
```

ScheduledFutureTask内部还有一个变量period用来表示任务的类型，

任务类型如下：

* period=0，说明当前任务是一次性的，执行完毕后就退出了。
*  period为负数，说明当前任务为fixed-delay任务，是固定延迟的定时可重复执行任务。
* period为正数，说明当前任务为fixed-rate任务，是固定频率的定时可重复执行任务。

ScheduledThreadPoolExecutor 构造函数 : 

从构造函数中看到使用的是 DelayedWorkQueue

```java
 public ScheduledThreadPoolExecutor(int corePoolSize,
                                       ThreadFactory threadFactory,
                                       RejectedExecutionHandler handler) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue(), threadFactory, handler);
}

public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
}
```

### 三个重要函数:

* schedule(Runnable command, long delay, TimeUnit unit) : 该方法的作用是提交一个延迟执行的任务，任务从提交时间算起延迟单位为unit的delay时间后开始执行。提交的任务不是周期性任务，任务只会执行一次
* scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
* scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)



### Runnable 转换为 Callable

```java
//Runnable runnable, V result
this.callable = Executors.callable(runnable, result);
//	Executors # callable
public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);
}

// RunnableAdapter 适配器类
static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }
```



## beforeExecute(Thread t, Runnable r) 和 afterExecute(Runnable r, Throwable t) 

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BeforeAndAfterExecuteTest {

    public static void main (String [] args){

        MyThreadPoolExecutor poolExecutor = new MyThreadPoolExecutor(2, 3, Integer.MAX_VALUE,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        poolExecutor.execute(new MyRunnable1("A1"));
        poolExecutor.execute(new MyRunnable1("A2"));
        poolExecutor.execute(new MyRunnable1("A3"));
        poolExecutor.execute(new MyRunnable1("A4"));
    }
}
class MyRunnable1 implements Runnable{

    private String userName;

    public MyRunnable1(String userName){
        super();
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public void run() {
        try {
            System.out.println(userName + " begin run ! Time : "+System.currentTimeMillis());
            Thread.sleep(1000);
            System.out.println(userName + " end run ! Time : "+System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyThreadPoolExecutor extends ThreadPoolExecutor{

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        System.out.println(((MyRunnable1)r).getUserName()+"执行开始");
    }
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        System.out.println(((MyRunnable1)r).getUserName()+"执结束");
    }
}

//结果
A1执行开始
A2执行开始
A1 begin run ! Time : 1567654174290
A2 begin run ! Time : 1567654174290
A2 end run ! Time : 1567654175290
A1 end run ! Time : 1567654175290
A1执结束
A2执结束
A3执行开始
A4执行开始
A3 begin run ! Time : 1567654175290
A4 begin run ! Time : 1567654175290
A4 end run ! Time : 1567654176290
A4执结束
A3 end run ! Time : 1567654176290
A3执结束
```

