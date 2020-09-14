# LockSupport

[toc]

LoclSupport是工具类,主要作用是挂起和唤醒线程,该工具类是创建锁和其它同步类的基础.

LockSupport类与每个使用它的线程都会关联一个许可证,在默认情况下调用LockSupport类的方法的线程是不持有许可证的.

## 1. void park()方法

如果调用park方法的线程已经拿到了与LockSupport关联的许可证，则调用LockSupport.park（）时会马上返回，否则调用线程会被禁止参与线程的调度，也就是会被阻塞挂起。
如在线程t1中,打印了"begin lockSupport"后会一直阻塞,解开 `//LockSupport.unpark(t1);`再次运行就可以运行完
```java
public class LockSupportTest {

    public static void main(String [] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("begin lockSupport");
                LockSupport.park();
                System.out.println("end lockSupport");
            }
        });
        t1.start();
        //Thread.sleep(1000);
        System.out.println("main begin");

        //LockSupport.unpark(t1);
        //Thread.sleep(1000);
        System.out.println("main end");
    }
}
```

在其他线程调用unpark（Thread thread）方法并且将当前线程作为参数时，调用park方法而被阻塞的线程会返回。另外，如果其他线程调用了阻塞线程的interrupt（）方法，设置了中断标志或者线程被虚假唤醒，则阻塞线程也会返回。所以在调用park方法时最好也使用循环条件判断方式。需要注意的是，因调用park（）方法而被阻塞的线程被其他线程中断而返回时并不会抛出InterruptedException异常。

## 2. void unpark(Thread thread)方法

当一个线程调用unpark时，如果参数thread线程没有持有thread与LockSupport类关联的许可证，则让thread线程持有。如果thread之前因调用park（）而被挂起，则调用unpark后，该线程会被唤醒。如果thread之前没有调用park，则调用unpark方法后，再调用park方法，其会立刻返回  


park方法返回时不会告诉你因何种原因返回，所以调用者需要根据之前调用park方法的原因，再次检查条件是否满足，如果不满足则还需要再次调用park方法。

例如，根据调用前后中断状态的对比就可以判断是不是因为被中断才返回的。为了说明调用park方法后的线程被中断后会返回，我们修改上面的例子代码，删除`LockSupport.unpark（thread）`;，然后添加`thread.interrupt（）;`，具体代码如下。
```java
public class LockSupportTest {

    public static void main(String [] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("begin lockSupport");
                //调用park()方法挂起自己,只有被中断才会退出循环
                while (!Thread.currentThread().isInterrupted()){
                    LockSupport.park();  
                }
                System.out.println("end lockSupport");
            }
        });
        t1.start();
//        Thread.sleep(1000);
        System.out.println("main begin");
        t1.interrupt();
//      LockSupport.unpark(t1);
//      Thread.sleep(1000);
        System.out.println("main end");
    }
}
```

在如上代码中，只有中断子线程，子线程才会运行结束，如果子线程不被中断，即使你调用unpark（thread）方法子线程也不会结束。

## 3. void parkNanos(long nanos)方法

和park方法类似，如果调用park方法的线程已经拿到了与LockSupport关联的许可证，则调用LockSupport.parkNanos（long nanos）方法后会马上返回。该方法的不同在于，如果没有拿到许可证，则调用线程会被挂起nanos时间后修改为自动返回。

另外park方法还支持带有blocker参数的方法void park（Object blocker）方法，当线程在没有持有许可证的情况下调用park方法而被阻塞挂起时，这个blocker对象会被记录到该线程内部。使用诊断工具可以观察线程被阻塞的原因，诊断工具是通过调用getBlocker（Thread）方法来获取blocker对象的，所以JDK推荐我们使用带有blocker参数的park方法，并且blocker被设置为this，这样当在打印线程堆栈排查问题时就能知道是哪个类被阻塞了。

## 4. park(Object blocker)方法

## 5. void parkNanos(Object blocker, long nanos)方法

## 6. void parkUntil(Object blocker, long deadline)方法