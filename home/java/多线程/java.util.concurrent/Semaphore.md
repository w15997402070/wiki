# Semaphore

`java.util.concurrent.Semaphore`主要作用是限制线程并发的数量.

![结构图](D:\data\notes\notes\java\源码\java.util.concurrent\Semaphore.assets\1565683792106.png)

```
java.util.concurrent.Semaphore;

acquire() : 表示每调用一次此方法,就使用1个许可
acquire(int permits) : 表示每调用一次此方法,就使用x个许可
release() : 表示每调用一次此方法,就添加一个许可
release(int permits) : 表示每调用一次此方法,就添加x个许可
acquireUninterruptibly() : 使等待进入 acquire() 方法的线程,不允许被中断
acquireUninterruptibly(int permits) : 等待许可的情况下不允许中断,如果成功获得锁,则取得指定的permits许可个数
availablePermits() : 返回此Semaphore 对象中当前可用的许可数,此方法通常用于测试,因为许可的数量有可能实时在改变,并不是固定的数量
drainPermits() : 可获取并返回立即可用的许可个数,并且将可用许可置为0
getQueueLength() : 取得等待许可的线程个数
hasQueuedThreads() : 判断有没有线程在等待这个许可
```

## 构造函数传入许可参数

构造函数参数permits表示同一时间允许几个线程同时执行acquire和release之间的代码

```java
import java.util.concurrent.Semaphore;

public class SemaphoreTest1 {
    public static void main(String[] args) {
        Service service = new Service();
        ThreadA threadA = new ThreadA(service);
        threadA.setName("A");
        ThreadB threadB = new ThreadB(service);
        threadB.setName("B");
        ThreadC threadC = new ThreadC(service);
        threadC.setName("C");
        threadA.start();
        threadB.start();
        threadC.start();
    }
}

class Service{
    //构造函数参数permits表示同一时间允许几个线程同时执行acquire和release之间的代码
    private Semaphore semaphore = new Semaphore(1);

    public void testMethod(){
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+" begin time = "+System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName()+" end time = "+System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadA extends Thread{

    private Service service;

    public  ThreadA(Service service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}
class ThreadB extends Thread{

    private Service service;

    public  ThreadB(Service service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}
class ThreadC extends Thread{

    private Service service;

    public  ThreadC(Service service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}

//结果为
A begin time = 1565685021635
A end time = 1565685023635
C begin time = 1565685023635
C end time = 1565685025636
B begin time = 1565685025636
B end time = 1565685027636
```

- 当构造函数中传参数为`1`时结果为 : 

```A begin time = 1565685021635
A end time = 1565685023635
C begin time = 1565685023635
C end time = 1565685025636
B begin time = 1565685025636
B end time = 1565685027636
```

- 当构造函数中传参数为`2`时结果为 : 

```C begin time = 1565685157689
B begin time = 1565685157689
C end time = 1565685159690
B end time = 1565685159690
A begin time = 1565685159690
A end time = 1565685161690
```

- 当构造函数中传参数`大于等于3`时结果为 : 

```B begin time = 1565685261218
A begin time = 1565685261218
C begin time = 1565685261218
C end time = 1565685263218
A end time = 1565685263218
B end time = 1565685263218
```
## 1.acquire(int permits)

 方法`acquire(int permits)`参数作用

```java
import java.util.concurrent.Semaphore;

public class SemaphoreTest1 {

    public static void main(String[] args) {
        ServiceAcquire service = new ServiceAcquire();
        ThreadA threadA = new ThreadA(service);
        threadA.setName("A");
        ThreadB threadB = new ThreadB(service);
        threadB.setName("B");
        ThreadC threadC = new ThreadC(service);
        threadC.setName("C");
        threadA.start();
        threadB.start();
        threadC.start();
    }
}

class Service implements ServiceInterface{
    //构造函数参数permits表示同一时间允许几个线程同时执行acquire和release之间的代码
    private Semaphore semaphore = new Semaphore(4);

    public void testMethod(){
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+" begin time = "+System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName()+" end time = "+System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class ThreadA extends Thread{

    private ServiceInterface service;

    public  ThreadA(ServiceInterface service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}
class ThreadB extends Thread{

    private ServiceInterface service;

    public  ThreadB(ServiceInterface service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}
class ThreadC extends Thread{

    private ServiceInterface service;

    public  ThreadC(ServiceInterface service){
        super();
        this.service = service;
    }

    @Override
    public void run() {
        service.testMethod();
    }
}
class ServiceAcquire implements ServiceInterface {
    //构造函数参数permits表示同一时间允许几个线程同时执行acquire和release之间的代码
    private Semaphore semaphore = new Semaphore(4);

    public void testMethod() {
        try {
            //表示每调用一次此方法,就使用x个许可
            semaphore.acquire(2);
            System.out.println(Thread.currentThread().getName() + " begin time = " + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " end time = " + System.currentTimeMillis());
            //如果多次调用这个方法,可以动态增加 permits 的个数
            semaphore.release(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
interface ServiceInterface{
    void testMethod();
}
```
结果为 : 
```
A begin time = 1565686752258
C begin time = 1565686752258
A end time = 1565686754259
C end time = 1565686754259
B begin time = 1565686754259
B end time = 1565686756259
```
## 2.release(int permits)

Semaphore类的release() 或 release(int permits) 方法可以动态增加 permits 的个数

```java
import java.util.concurrent.Semaphore;

public class SemaphoreTest1 {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(5);
        try {
            semaphore.acquire();
            semaphore.acquire();
            semaphore.acquire();
            semaphore.acquire();
            semaphore.acquire();
            System.out.println("availablePermits : "+semaphore.availablePermits());
            semaphore.release();
            semaphore.release();
            semaphore.release();
            semaphore.release();
            semaphore.release();
            System.out.println("availablePermits : "+semaphore.availablePermits());
            semaphore.release(4);
            System.out.println("availablePermits : "+semaphore.availablePermits());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//结果为
availablePermits : 0
availablePermits : 5
availablePermits : 9
```
 ## 3 .acquireUninterruptibly(int permits)

`acquireUninterruptibly()`和 `acquireUninterruptibly(int permits)`的作用是使等待进入 `acquire()`方法的线程,不允许被中断

```java
import java.util.concurrent.Semaphore;

public class SemaphoreTest1 {
    public static void main(String[] args) {
        Service service = new Service();
        ThreadA a = new ThreadA(service);
        a.setName("A");
        a.start();
        ThreadB b = new ThreadB(service);
        b.setName("B");
        b.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b.interrupt();
        System.out.println("main 中断了a");
    }
}
//结果
A begin time = 1565688355141
java.lang.InterruptedException
main 中断了a
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireSharedInterruptibly(AbstractQueuedSynchronizer.java:998)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly(AbstractQueuedSynchronizer.java:1304)
	at java.util.concurrent.Semaphore.acquire(Semaphore.java:312)
	at com.test.Service.testMethod(SemaphoreTest1.java:32)
	at com.test.ThreadB.run(SemaphoreTest1.java:69)
A end time = 1565688357142
    
  //非中断测试
    //将testMethod()方法更改为
      public void testMethod(){
        try {
            //这里修改为非中断
            semaphore.acquireUninterruptibly();
            System.out.println(Thread.currentThread().getName()+" begin time = "+System.currentTimeMillis());
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName()+" end time = "+System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
//            System.out.println("线程 : "+Thread.currentThread().getName()+"进入catch  "+System.currentTimeMillis());
            e.printStackTrace();
        }
    }
```

## 4.getQueueLength() 和 hasQueuedThreads()

```java
public class SemaphoreTest1 {
    public static void main(String[] args) {
        ServiceInterface service = new MyService();
        ThreadA firstThread = new ThreadA(service);
        firstThread.start();

        ThreadA [] threadAS = new ThreadA[4];
        for (int i = 0; i < 4 ; i++) {
            threadAS[i] = new ThreadA(service);
            threadAS[i].start();
        }
    }
}
class MyService implements ServiceInterface{

    private Semaphore semaphore = new Semaphore(1);
    @Override
    public void testMethod() {
        try {
            semaphore.acquire();
            Thread.sleep(2000);
            System.out.println("大约还有 : " +semaphore.getQueueLength()+ " 个线程在等待" );
            System.out.println("是否有线程正在等待信号量 :  " +semaphore.hasQueuedThreads());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }
}

//结果为
大约还有 : 4 个线程在等待
是否有线程正在等待信号量 :  true
大约还有 : 3 个线程在等待
是否有线程正在等待信号量 :  true
大约还有 : 2 个线程在等待
是否有线程正在等待信号量 :  true
大约还有 : 1 个线程在等待
是否有线程正在等待信号量 :  true
大约还有 : 0 个线程在等待
是否有线程正在等待信号量 :  false
```

## 5. tryAcquire() 和 tryAcquire(int permits)

无参方法`tryAcquire()`的作用是尝试地获得一个许可,如果获取不到则返回false

有参方法`tryAcquire(int permits)` 的作用是尝试获取x个许可

```
tryAcquire(long timeout, TimeUnit unit) 在指定的时间内尝试地获得1个许可,如果获取不到则返回false
tryAcquire(int permits, long timeout, TimeUnit unit) 在指定的时间内尝试地获得x个许可,如果获取不到则返回false
```

```java
public class SemaphoreTest1 {
    public static void main(String[] args) {
        ServiceInterface service = new Service();
        ThreadA a = new ThreadA(service);
        a.setName("A");
        a.start();
        ThreadA b = new ThreadA(service);
        b.setName("B");
        b.start();
    }
}
class Service implements ServiceInterface{
    //构造函数参数permits表示同一时间允许几个线程同时执行acquire和release之间的代码
    private Semaphore semaphore = new Semaphore(1);
    public void testMethod(){
        try {
            if (semaphore.tryAcquire()){
                System.out.println(Thread.currentThread().getName()+" 首选进入 ");
                Thread.sleep(500);
                semaphore.release();
            }else {
                System.out.println(Thread.currentThread().getName()+" 未成功进入");

            }
        } catch (InterruptedException e) {
//            System.out.println("线程 : "+Thread.currentThread().getName()+"进入catch  "+System.currentTimeMillis());
            e.printStackTrace();
        }
    }
}

//结果
A 首选进入 
B 未成功进入
```

