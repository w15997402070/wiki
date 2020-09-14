# CountDownLatch

类 `CountDownLatch` 所提供的功能是判断 count 计数不为0时,则当前线程呈 wait 状态,也就是在屏障处等待

![类结构](D:\data\notes\notes\java\源码\java.util.concurrent\CountDownLatch.assets\1566379751330.png)

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchTest {

    public static void main (String [] args){
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch cdManager = new CountDownLatch(1);
        CountDownLatch cdSales = new CountDownLatch(3);
        for (int i = 0; i < 3 ; i++) {
            Runnable runnable = () -> {
                System.out.println("线程 : "+Thread.currentThread().getName()+"准备");
                try {
                    cdManager.await();//sales线程处于等待中
                    System.out.println("线程 : "+Thread.currentThread().getName()+"已接受指令");
                    Thread.sleep((long) (Math.random()*10000));
                    System.out.println("线程 : "+Thread.currentThread().getName()+"处理完毕");
                    cdSales.countDown();//salse处理完毕cdSales减1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            executorService.execute(runnable);
        }
        try {
            Thread.sleep((long) (Math.random()*10000));
            System.out.println("线程 : "+Thread.currentThread().getName()+"即将开始任务");
            cdManager.countDown();//发送指令,cdManage减1
            System.out.println("线程 : "+Thread.currentThread().getName()+"已发送任务,等待结果");
            cdSales.await();//等待状态,cdSales为0时停止等待,继续往下执行
            System.out.println("线程 : "+Thread.currentThread().getName()+"已收到结果");
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//结果
线程 : pool-1-thread-1准备
线程 : pool-1-thread-3准备
线程 : pool-1-thread-2准备
线程 : main即将开始任务
线程 : main已发送任务,等待结果
线程 : pool-1-thread-1已接受指令
线程 : pool-1-thread-2已接受指令
线程 : pool-1-thread-3已接受指令
线程 : pool-1-thread-2处理完毕
线程 : pool-1-thread-1处理完毕
线程 : pool-1-thread-3处理完毕
线程 : main已收到结果
```

