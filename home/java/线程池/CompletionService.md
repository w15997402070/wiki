# CompletionService

以异步的方式一边生产新的任务,一边处理已完成任务的结果,这样可以将执行任务与处理任务分离开来进行处理.使用 submit 执行任务,使用 take 取得已完成的任务,并按照完成这些任务的时间顺序处理他们的结果

## 使用 CompletionService 解决Future 的缺点

CompletionService<V> 只有一个实现类 ExecutorCompletionService<V>

```java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CompletionServiceTest {

    public static void main (String [] args) throws ExecutionException, InterruptedException {

        System.out.println("开始时间 : "+LocalDateTime.now());
        MyCallableCompletionService callable1 = new MyCallableCompletionService("callable1",5000);
        MyCallableCompletionService callable2 = new MyCallableCompletionService("callable2",4000);
        MyCallableCompletionService callable3 = new MyCallableCompletionService("callable3",3000);
        MyCallableCompletionService callable4 = new MyCallableCompletionService("callable4",2000);
        MyCallableCompletionService callable5 = new MyCallableCompletionService("callable5",1000);
        List<Callable> callableList = new ArrayList<>();
        callableList.add(callable1);
        callableList.add(callable2);
        callableList.add(callable3);
        callableList.add(callable4);
        callableList.add(callable5);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 10, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        ExecutorCompletionService completionService = new ExecutorCompletionService(poolExecutor);
        for (int i = 0; i < 5 ; i++) {
            completionService.submit(callableList.get(i));
        }

        for (int i = 0; i < 5 ; i++) {
            System.out.println("等待打印第 "+ ( i + 1 ) + "个返回值");
            System.out.println(completionService.take().get());
        }
        System.out.println("结束时间 : "+LocalDateTime.now());
        poolExecutor.shutdown();
    }
}

class MyCallableCompletionService implements Callable<String>{

    private String username;
    private long sleepValue;

    public MyCallableCompletionService(String username,long sleepValue){
        super();
        this.username = username;
        this.sleepValue = sleepValue;
    }

    @Override
    public String call() throws Exception {
        System.out.println(" call username : "+username);
        Thread.sleep(sleepValue);
        return "返回值 名称是 : "+username;
    }
}

//结果
开始时间 : 2019-09-07T17:16:04.676
 call username : callable1
 call username : callable3
 call username : callable2
等待打印第 1个返回值
 call username : callable5
 call username : callable4
返回值 名称是 : callable5
等待打印第 2个返回值
返回值 名称是 : callable4
等待打印第 3个返回值
返回值 名称是 : callable3
等待打印第 4个返回值
返回值 名称是 : callable2
等待打印第 5个返回值
返回值 名称是 : callable1
结束时间 : 2019-09-07T17:16:09.686
```

```
take() 取得最先完成任务的 Future 对象,谁执行时间最短谁最先返回
poll() 获取并移除表示下一个已完成任务的Future,如果不存在这样的任务,则返回null,方法 poll()无阻塞效果
poll(long timeout, TimeUnit unit) 等待指定的 timeout 时间,在 timeout 时间之内获取到值时立即向下继续执行,如果超时也立即xiang
```