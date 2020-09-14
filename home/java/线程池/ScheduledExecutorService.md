# ScheduledExecutorService

## ScheduledThreadPoolExecutor

```java
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorTest {

    public static void main (String [] args){

        ScheduledExecutorService scheduledExecutorService = null;
        try {
            ScheduledCallableA callableA = new ScheduledCallableA();
            ScheduledCallableB callableB = new ScheduledCallableB();
 //scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
            ScheduledFuture<String> scheduledFutureA = scheduledExecutorService.schedule(callableA, 4L, TimeUnit.SECONDS);
            ScheduledFuture<String> scheduledFutureB = scheduledExecutorService.schedule(callableB, 4L, TimeUnit.SECONDS);
            System.out.println("main begin "+LocalDateTime.now());
            System.out.println("返回值A : "+scheduledFutureA.get());
            System.out.println("返回值B : "+scheduledFutureB.get());
            System.out.println("main end "+LocalDateTime.now());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (scheduledExecutorService != null){
                scheduledExecutorService.shutdown();
            }
        }
    }
}

class ScheduledCallableA implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("callA begin "+Thread.currentThread().getName()+" Time : "+LocalDateTime.now());
        Thread.sleep(3000);
        System.out.println("callA end "+Thread.currentThread().getName()+" Time : "+LocalDateTime.now());
        return "resultA";
    }
}
class ScheduledCallableB implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("callA begin "+Thread.currentThread().getName()+" Time : "+LocalDateTime.now());
//        Thread.sleep(3000);
        System.out.println("callA end "+Thread.currentThread().getName()+" Time : "+LocalDateTime.now());
        return "resultB";
    }
}

//结果
    
main begin 2019-09-07T21:00:52.820
    //这里等待了4秒
callA begin pool-1-thread-2 Time : 2019-09-07T21:00:56.719
callB begin pool-1-thread-1 Time : 2019-09-07T21:00:56.721
callB end pool-1-thread-1 Time : 2019-09-07T21:00:56.721
callA end pool-1-thread-2 Time : 2019-09-07T21:00:59.720
返回值A : resultA
返回值B : resultB
main end 2019-09-07T21:00:59.721
```

## scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) 方法实现周期性运行

```java
private static void scheduledFixRateMethod(){
        ScheduledExecutorService scheduledExecutorService = null;
        try {
            ScheduledRunnable scheduledRunnable = new ScheduledRunnable();
            scheduledExecutorService =  Executors.newSingleThreadScheduledExecutor();
            System.out.println("main begin "+LocalDateTime.now());
            scheduledExecutorService.scheduleAtFixedRate(scheduledRunnable,1,2,TimeUnit.SECONDS);
            System.out.println("main end "+LocalDateTime.now());
        }finally {
            if (scheduledExecutorService != null){
//                scheduledExecutorService.shutdown();
            }
        }
    }

class ScheduledRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("schedule run "+LocalDateTime.now());
    }
}
//结果
main begin 2019-09-07T21:39:55.326
main end 2019-09-07T21:39:55.333
schedule run 2019-09-07T21:39:56.335
schedule run 2019-09-07T21:39:58.333
schedule run 2019-09-07T21:40:00.333
schedule run 2019-09-07T21:40:02.335
```

注意，`scheduleAtFixedRate()`方法返回的`ScheduledFuture`对象无法获得返回值，也就是`scheduleAtFixedRate()`方法不具有获得返回值的功能，而`schedule()`方法却可以获得返回值。所以当使用`scheduleAtFixedRate()`方法实现重复运行任务的效果时，需要结合自定义Runnable接口的实现类，不要使用FutureTask类，因为FutureTask类并不能实现重复运行的效果.

```
getQueue() 取得队列中的任务,这些任务是未来将要运行的,正在运行的任务不在此列
remove(Runnable task)

setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) 当对 ScheduledThreadPoolExecutor 执行了shutdown() 方法时,任务是否继续运行,默认值是true
setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value)  当使用 scheduleAtFixedRate() 方法或 scheduleWithFixedDelay() 方法时,如果调用 shutdown() 方法,value 为 true 时继续运行,false 时任务不运行,进程销毁

cancel(boolean mayInterruptIfRunning) 设定是否取消任务,任务被取消之后还在队列中
setRemoveOnCancelPolicy(boolean value) 设定是否将取消后的任务从队列中清楚
```