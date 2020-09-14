# CyclicBarrier

类 `CyclicBarrier` 允许一组线程互相等待,直到到达某个公共屏障点(common barrier point),这些线程必须实时的互相等待,这种情况就可以使用 `CyclicBarrier` 类方便地实现这样的功能,`CyclicBarrier`类的屏障点可以重用,

`CountDownLatch` 和 `CyclicBarrier` 的区别 : 

-  `CountDownLatch` 的作用 : 一个线程或者多个线程,等待另外一个线程或多个线程完成某个事情之后才能继续执行.

- `CyclicBarrier` 的作用 : 多个线程之间相互等待,任何一个线程完成之前,所有的线程都必须等待,所以对于  `CyclicBarrier`  来说,重点是"多个线程之间"任何一个线程没有完成任务,则所有的线程都必须等待.

```
isBroken() 查询此屏障是否处于损坏状态
await(long timeout, TimeUnit unit) 
在指定的时间内达到 parties 的数量,则程序继续向下运行,否则如果出现超时,则抛出 TimeoutException 异常.
getParties() 的作用是取得 parties 个数
getNumberWaiting() 的作用是有几个线程已经到达屏障点
reset() 重置屏障
```



## 线程数等于parties个数

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    public static void main (String [] args){

        CyclicBarrier cbRef = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("全都到了!");
            }
        });

        CyclicBarrierThread [] threadArr = new CyclicBarrierThread[5];
        for (int i = 0; i < threadArr.length ; i++) {
            threadArr[i] = new CyclicBarrierThread(cbRef);
        }
        for (int i = 0; i < threadArr.length; i++) {
            threadArr[i].start();
        }
    }
}

class CyclicBarrierThread extends Thread{

    private CyclicBarrier cbRef;

    public CyclicBarrierThread(CyclicBarrier cbRef){
       super();
       this.cbRef = cbRef;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int)(Math.random()*1000));
            System.out.println(Thread.currentThread().getName() +"到了!"+ System.currentTimeMillis());
            cbRef.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

//结果
Thread-3到了!1566544866804
Thread-4到了!1566544867051
Thread-0到了!1566544867072
Thread-1到了!1566544867681
Thread-2到了!1566544867737
全都到了!
```

## 线程个数大于parties个数,分批处理

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    public static void main (String [] args) throws InterruptedException {

        CyclicBarrier cbRef = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                System.out.println("全都到了!");
            }
        });

        CyclicBarrierThread [] threadArr = new CyclicBarrierThread[5];
        for (int i = 0; i < threadArr.length ; i++) {
            threadArr[i] = new CyclicBarrierThread(cbRef);
            threadArr[i].start();
            Thread.sleep(2000);
        }

    }
}

class CyclicBarrierThread extends Thread{

    private CyclicBarrier cbRef;

    public CyclicBarrierThread(CyclicBarrier cbRef){
       super();
       this.cbRef = cbRef;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int)(Math.random()*1000));
            System.out.println(Thread.currentThread().getName() +"到待凑齐2个继续运行!"+ System.currentTimeMillis());
            cbRef.await();
            System.out.println(Thread.currentThread().getName() +"已经凑齐两个继续运行!"+ System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
```

## 验证屏障重置性及getNumberWaiting() 方法的使用

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    public static void main (String [] args) throws InterruptedException {

        CyclicBarrier cbRef = new CyclicBarrier(3);

        CyclicBarrierThread thread1 = new CyclicBarrierThread(cbRef);
        thread1.start();
        thread1.sleep(500);
        System.out.println(cbRef.getNumberWaiting());

        CyclicBarrierThread thread2 = new CyclicBarrierThread(cbRef);
        thread2.start();
        thread2.sleep(500);
        System.out.println(cbRef.getNumberWaiting());

        CyclicBarrierThread thread3 = new CyclicBarrierThread(cbRef);
        thread3.start();
        thread3.sleep(500);
        System.out.println(cbRef.getNumberWaiting());

        CyclicBarrierThread thread4 = new CyclicBarrierThread(cbRef);
        thread4.start();
        thread4.sleep(500);
        System.out.println(cbRef.getNumberWaiting());
    }
}

class CyclicBarrierThread extends Thread{

    private CyclicBarrier cbRef;

    public CyclicBarrierThread(CyclicBarrier cbRef){
       super();
       this.cbRef = cbRef;
    }

    @Override
    public void run() {
        try {
            cbRef.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

//结果
1
2
0
1
```
