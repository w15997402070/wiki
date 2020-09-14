# ExecutorService

invokeAny() 和 invokeAll() 具有阻塞特性

* invokeAny() 取得第一个完成任务的结果值,当第一个任务执行完成后,会调用 interrupt() 方法将其他任务中断,所以在这些任务中可以结合 if(Thread.currentThread().isInterrupted() == true) 代码来决定任务是否继续运行.
* invokeAll() 等全部任务执行完毕后,取得全部完成任务的结果值

```

invokeAny(Collection<? extends Callable<T>> tasks,                       long timeout, TimeUnit unit) 取得第一个任务的结果值
invokeAll(Collection<? extends Callable<T>> tasks) 取得所有任务的结果值
```

## 方法 invokeAny() 的使用与 interrupt() 方法

此实验验证方法invokeAny0的确是取得第一个完成任务的结果值，但在这个过程中出现两种情况：

* 1）无if（Thread.currentThread().islnterrupted()）代码：已经获得第一个运行的结果值后，其他线程继续运行。
* 2）有if（Thread.currentThread().isInterupted()）代码：已经获得第一个运行的结果值后，其他线程如果使用 throw new InterruptedException() 代码则这些线程中断，虽然 throw 抛出了异常，但在main 线程中并不能捕获异常。如果想捕获异常，则需要在 Callable 中使用 try-catch 显式进行捕获。

```java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceTest {

    public static void main (String [] args){
        ExecutorService executorService = null;
        try {
            List list = new ArrayList<>();
            list.add(new MyCallableA());
            list.add(new MyCallableB1());
            executorService = Executors.newCachedThreadPool();
            String t = (String)executorService.invokeAny(list);
            System.out.println("============ "+ t);
            System.out.println("zzzzzzzzzzzzzzzzzz "+ t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (executorService != null){
                executorService.shutdown();
            }
        }
    }
}

//new MyCallableB1() 结果为
MyCallableA begin 2019-09-07T18:10:37.433
MyCallableB1 begin 2019-09-07T18:10:37.433
MyCallableA end 2019-09-07T18:10:37.524
============ MyCallableA
zzzzzzzzzzzzzzzzzz MyCallableA
MyCallableB1 end 2019-09-07T18:10:37.532

//new MyCallableB2() 结果为
MyCallableA begin 2019-09-07T18:13:25.869
MyCallableB2 begin 2019-09-07T18:13:25.869
MyCallableA end 2019-09-07T18:13:25.969
============ MyCallableA
zzzzzzzzzzzzzzzzzz MyCallableA
抛出异常了


class MyCallableA implements Callable<String>{
    @Override
    public String call() throws Exception {
        System.out.println("MyCallableA begin "+LocalDateTime.now());
        for (int i = 0; i < 123456 ; i++) {
            Math.random();
            Math.random();
            Math.random();
//            System.out.println("MyCallableA "+(i+1));
        }
        System.out.println("MyCallableA end "+LocalDateTime.now());

        return "MyCallableA";
    }
}

class MyCallableB1 implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("MyCallableB1 begin "+LocalDateTime.now());
        for (int i = 0; i < 223456 ; i++) {
            Math.random();
            Math.random();
            Math.random();
//            System.out.println("MyCallableB1 "+(i+1));
        }
        System.out.println("MyCallableB1 end "+LocalDateTime.now());

        return "MyCallableB1";
    }
}
class MyCallableB2 implements Callable<String>{
    @Override
    public String call() throws Exception {
        System.out.println("MyCallableB2 begin "+LocalDateTime.now());
        for (int i = 0; i < 223456 ; i++) {
            if (Thread.currentThread().isInterrupted() == false){
                Math.random();
                Math.random();
                Math.random();
                //            System.out.println("MyCallableB2 "+(i+1));
            }else {
                System.out.println("抛出异常了");
                throw new InterruptedException();
            }
        }
        System.out.println("MyCallableB2 end "+LocalDateTime.now());

        return "MyCallableB2";
    }
}
```

## 方法invokeAll(Collection<? extends Callable<T>> tasks)

invokeAll() 会返回所有的任务的执行结果,并且此方法执行的效果也是阻塞执行的,要把所有的结果都取回时再继续向下执行.

```java
public class ExecutorServiceTest {

    public static void main (String [] args){
        ExecutorService executorService = null;
        try {
            List list = new ArrayList<>();
            list.add(new MyCallableA());
            list.add(new MyCallableB1());
            executorService = Executors.newCachedThreadPool();
            List<FutureTask<String>> t = executorService.invokeAll(list);
            for (int i = 0; i < t.size() ; i++) {
                System.out.println("result  "+ t.get(i).get());
            }
            System.out.println("main end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (executorService != null){
                executorService.shutdown();
            }
        }
    }
}

//结果
MyCallableA begin 2019-09-07T18:22:42.076
MyCallableB1 begin 2019-09-07T18:22:42.076
MyCallableA end 2019-09-07T18:22:42.174
MyCallableB1 end 2019-09-07T18:22:42.178
result  MyCallableA
result  MyCallableB1
main end
```

