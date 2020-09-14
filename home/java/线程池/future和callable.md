# Future 和 Callable

## Callable 和 Runnable的区别

- Callable 接口的 call() 方法可以有返回值,而 Runnable接口的 run() 方法没有返回值

- Callable 接口的 call() 方法可以声明异常,而 Runnable接口的 run() 方法不可以声明异常

- 执行完Callable接口中的任务后,返回值是通过 Future接口进行获得的

## 方法 execute() 与 submit() 的区别

- 方法 execute() 没有返回值,而 submit() 方法可以有返回值
- 方法 execute() 在默认的情况下异常直接抛出,不能捕获,但可以通过自定义 ThreadFactory 的方式进行捕获,而 submit() 方法在默认的情况下,可以 catch ExecutionException 捕获异常

## Future

```
get() 获取返回值(阻塞)
get(long timeout, TimeUnit unit) 指定的最大时间内等待获得返回值
isDone() 
cancel(boolean mayInterruptIfRunning) 如果线程正在运行则是否中断正在运行的线程,在代码中需要使用if(Thread.currentThread().isInterrupted()) 进行配合,返回值代表发送取消任务的命令是否成功完成

```

## Callable使用


### 1. submit(Callable<T> task) 通过get()方法获取返回值

  ```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CallableTest {

    public static void main (String [] args) throws ExecutionException, InterruptedException {
        MyCallable callable = new MyCallable(100);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 3, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        Future<String> future = poolExecutor.submit(callable);
        System.out.println("main A "+System.currentTimeMillis());
        System.out.println(future.get());
        System.out.println("main B "+System.currentTimeMillis());
        poolExecutor.shutdown();
    }
}

class MyCallable implements Callable<String>{
    private int age;
    public MyCallable(int age){
        super();
        this.age = age;
    }
    @Override
    public String call() throws Exception {
        Thread.sleep(5000);
        return "返回值 年龄是 : "+age;
    }
}
//结果
main A 1567668691525
返回值 年龄是 : 100  
main B 1567668696525
  ```

### 2. submit(Runnable task, T result)

第二个参数 result 可以作为执行结果的返回值

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CallableTest2 {

    public static void main (String [] args) throws ExecutionException, InterruptedException {
        UserInfo userInfo = new UserInfo();
        MyRunnable2 runnable2 = new MyRunnable2(userInfo);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 3, 5,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        Future<UserInfo> future = poolExecutor.submit(runnable2,userInfo);
        System.out.println("main A "+System.currentTimeMillis());
        userInfo = future.get();
        System.out.println("username : "+userInfo.getUsername()+ "   password : "+userInfo.getPassword());
        System.out.println("main B "+System.currentTimeMillis());
        poolExecutor.shutdown();
    }
}

class MyRunnable2 implements Runnable{

    private UserInfo userInfo;

    public MyRunnable2(UserInfo userInfo){
        super();
        this.userInfo = userInfo;
    }
    @Override
    public void run() {
        userInfo.setUsername("usernameValue");
        userInfo.setPassword("upasswordValue");
    }
}

class UserInfo{

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

//结果
main A 1567668339209
username : usernameValue   password : upasswordValue
main B 1567668339210

```

