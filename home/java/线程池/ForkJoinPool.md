# ForkJoinPool

```
execute(ForkJoinTask<?> task) 以异步的方式执行任务,该方法无返回值,但是可以通过 RecursiveTask 对象 的 get() 方法处理返回值

submit(ForkJoinTask<T> task) 有返回值

```

execute(ForkJoinTask<?> task) 方法示例 : 

```java
import java.util.concurrent.RecursiveTask;
/**
* execute(ForkJoinTask<?> task) 方法返回值处理
*/
public class ExecuteTest {

    public static void main (String [] args) throws ExecutionException, InterruptedException {
        ExecuteTaskTest taskTest = new ExecuteTaskTest();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.execute(taskTest);
        System.out.println(" begin "+LocalDateTime.now());
        System.out.println(taskTest.get());
        System.out.println(" end "+LocalDateTime.now());
    }
}

class ExecuteTaskTest extends RecursiveTask<String>{
    @Override
    protected String compute() {
        return "我是返回值";
    }
}

//结果
 begin 2019-09-08T15:17:46.652
我是返回值
 end 2019-09-08T15:17:46.653
```
submit(ForkJoinTask<T> task) 有返回值 示例 : 

```java
public static void main (String [] args) throws ExecutionException, InterruptedException {
        ExecuteTaskTest taskTest = new ExecuteTaskTest();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<String> joinTask = forkJoinPool.submit(taskTest);
        System.out.println(" begin "+LocalDateTime.now());
        System.out.println(joinTask.get());
        System.out.println(" end "+LocalDateTime.now());
    }

//结果
 begin 2019-09-08T15:21:12.682
我是返回值
 end 2019-09-08T15:21:12.683
```

## 监视 pool 池的状态

类提供了若干个方法来监视任务池的状态：

* 方法getParallelism() ：获得并行的数量，与CPU的内核数有关；
* 方法getPoolSize() ：获得任务池的大小；
* 方法getQueuedSubmissionCount() ：取得已经提交但尚未被执行的任务数量；
* 方法hasQueuedSubmissions() ：判断队列中是否有未执行的任务；
* 方法getActive ThreadCount() ：获得活动的线程个数；
* 方法getQueuedTaskCount() ：获得任务的总个数；
* 方法getStealCount() ：获得偷窃的任务个数；
* 方法getRunning ThreadCount() ：获得正在运行并且不在阻塞状态下的线程个数；
* 方法isQuiescent() ：判断任务池是否是静止未执行任务的状态。