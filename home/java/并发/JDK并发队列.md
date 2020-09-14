# JDK并发队列
[toc]

## AQS抽象同步队列

AQS - AbstractQueuedSynchronizer,实现同步队列的基础组件

AQS是一个FIFO的双向队列，其内部通过节点head和tail记录队首和队尾元素，队列元素的类型为Node thread变量用来存放进入AQS队列里面的线程 SHARED用来标记该线程是获取共享资源时被阻塞挂起后放入AQS队列的 EXCLUSIVE用来标记线程是获取独占资源时被挂起后放入AQS队列的 waitStatus记录当前线程等待状态，可以为CANCELLED（线程被取消了）、SIGNAL（线程需要被唤醒）、CONDITION（线程在条件队列里面等待）、PROPAGATE（释放共享资源时需要通知其他节点） prev记录当前节点的前驱节点 next记录当前节点的后继节点

AbstractQueuedSynchronizer 单一的状态信息state,可以通过getState、setState、compareAndSetState函数修改其值 对于ReentrantLock的实现来说，state可以用来表示当前线程获取锁的可重入次数； 对于读写锁ReentrantReadWriteLock来说，state的高16位表示读状态，也就是获取该读锁的次数，低16位表示获取到写锁的线程的可重入次数； 对于semaphore来说，state用来表示当前可用信号的个数； 对于CountDownlatch来说，state用来表示计数器当前的值

内部类ConditionObject，用来结合锁实现线程同步 ConditionObject可以直接访问AQS对象内部的变量，比如state状态值和AQS队列。ConditionObject是条件变量，每个条件变量对应一个条件队列（单向链表队列），其用来存放调用条件变量的await方法后被阻塞的线程，如类图所示，这个条件队列的头、尾元素分别为firstWaiter和lastWaiter

对于AQS来说，线程同步的关键是对状态值state进行操作。根据state是否属于一个线程，操作state的方式分为独占方式和共享方式。 在独占方式下获取和释放资源使用的方法为： voidacquire（int arg）void acquireInterruptibly（int arg）boolean release（int arg）。 在共享方式下获取和释放资源的方法为： void acquireShared（int arg）voidacquireSharedInterruptibly（int arg）boolean releaseShared（int arg）

## ConcurrentLinkedQueue 无界非阻塞队列

ConcurrentLinkedQueue是线程安全的无界非阻塞队列，其底层数据结构使用单向链表实现，对于入队和出队操作使用CAS来实现线程安全

## `ConcurrentLinkedDeque`类来实现一个非阻塞并发双端队列

`ConcurrentLinkedDeque`类还提供其他方法来获取双端队列元素。

* `getFirst()`和`getLast()`：这两个方法会分别返回双端队列的首端和末端元素。它们不会删除双端队列中返回的元素。如果队列中没有任何元素，则它们会抛出`NoSuchElementException`异常。
*  `peek()`、`peekFirst()`和`peekLast()`：这些方法会分别返回双端队列的首末端元素。它们不会删除返回的元素。如果队列中没有任何元素，则它们返回null值。
*  `remove()`、`removeFirst()`和`removeLast()`：这些方法会分别返回双端队列的首末端元素，都会删除返回的元素。如果队列中没有任何元素，则它们会抛出`NoSuchElementException`异常。

## LinkedBlockingQueue 无界链表阻塞队列

使用独占锁实现的阻塞队列LinkedBlockingQueue

`LinkedBlockingDeque`类也提供了其他增删元素的方法，它们通过抛出异常或者直接返回null值来代替阻塞行为。这些方法如下所示。

* `takeFirst()`和`takeLast()`：这些方法分别返回双端队列中的第一个和最后一个元素。同时它们会移除队列中已经返回的元素。如果队列是空的，则它们会阻塞线程直到队列中有元素时为止。
* `getFirst()`和`getLast()`：这些方法分别返回双端队列中的第一个和最后一个元素。但它们不会移除队列中已经返回的元素。如果队列是空的，则它们会抛出一个`NoSuchElementException`异常。
* `peek()`、`peekFirst()`和`peekLast()`：`peekFirst()`和`peekLast()`方法分别返回队列中的第一个和最后一个元素。但它们不会移除队列中已经返回的元素。如果队列是空的，则它们会返回null值。
* `poll()`、`pollFirst()`和`pollLast()`：`pollFirst()`和`pollLast()`方法分别返回队列中的第一个和最后一个元素。同时它们会移除队列中已经返回的元素。如果队列是空的，则它们会返回null值。
*  `add()`、`addFirst()`和`addLast()`：`addFirst()`和`addLast()`方法会分别添加一个元素到队头和队尾。如果双端队列是满的（在创建队列的时候指定了队列的长度），则它们会抛出一个`IllegalStateException`异常。

## ArrayBlockingQueue 有界阻塞队列

有界数组方式实现的阻塞队列

## PriorityBlockingQueue 带优先级的无界阻塞队列
PriorityBlockingQueue是带优先级的无界阻塞队列，每次出队都返回优先级最高或者最低的元素。其内部是使用平衡二叉树堆实现的，所以直接遍历队列元素不保证有序

所有插入PriorityBlockingQueue中的元素都必须要实现Comparable接口，或者可以在队列的构造方法中传入Comparator对象。由于该接口的compareTo()方法可以传入一个同类型的对象，因此可以对两个对象进行比较：一个对象负责调用方法，另一个则作为参数传入到该方法。如果对象值小于参数值，则该方法必须返回一个负数；如果对象值大于参数值，则该方法应该返回正数；如果两者的值相等，则该方法必须返回零。

当插入一个元素到PriorityBlockingQueue中的时候，它会通过compareTo()方法来决定该元素在队列中的位置。值较大的元素将会插入队头或队尾，这取决于compareTo()方法的实现。



* clear()：该方法会移除所有队列中的元素。
*  take()：该方法会返回并移除队列中的首个元素。如果队列是空的，则它会阻塞线程直到不为空为止。
*  put(E e)：这是PriorityBlockingQueue的泛型参数。该方法会将元素插入到队列中。
*  peek()：该方法会返回且不移除队列中的首个元素。

## DelayQueue 无界阻塞延迟队列

DelayQueue并发队列是一个无界阻塞延迟队列，队列中的每个元素都有个过期时间，当从队列获取元素时，只有过期元素才会出队列。队列头元素是最快要过期的元素。

存储DelayQueue类中的元素时就必须实现一个Delayed接口。该接口不仅让元素具备延迟出现的特性，还提供getDelay()方法来获取当前时间与激活时间之差，同时还会要求实现以下两个方法

* compareTo(Delayed o)：Delayed接口继承了Comparable接口。如果调用该方法的对象延迟值比方法的参数更小，则该方法会返回负数；反之，如果调用该方法的对象延迟值比方法的参数更大，则该方法会返回正数；如果调用该方法的对象延迟值与方法的参数相等；则该方法会返回0。
*  getDelay(TimeUnit unit)：该方法必须返回一个当前时间与激活时间的差值。TimeUnit类是一个枚举类，用来指定该方法返回值的时间单位——它包含若干个枚举常量，如DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS和SECONDS。

```java
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
public class Event implements Delayed {

    private final Date startDate;

    public Event(Date startDate){
        this.startDate = startDate;
    }
    @Override
    public long getDelay(TimeUnit unit) {
        Date now = new Date();
        long diff = startDate.getTime() - now.getTime();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        long result = this.getDelay(TimeUnit.NANOSECONDS)-delayed.getDelay(TimeUnit.NANOSECONDS);
        if (result < 0){
            return -1;
        }else if (result > 0){
            return 1;
        }
        return 0;
    }
}
```



```java
import java.util.Date;
import java.util.concurrent.DelayQueue;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
public class Task implements Runnable {

    private final int id;
    private final DelayQueue<Event> delayQueue;

    public Task(int id, DelayQueue<Event> delayQueue){
        this.id = id;
        this.delayQueue = delayQueue;
    }
    @Override
    public void run() {
        Date now = new Date();
        Date delay = new Date();
        delay.setTime(now.getTime() + (id * 1000));
        System.out.printf("Thread %s: %s\n", id, delay);
        for (int i = 0; i < 100 ; i++) {
            Event event = new Event(delay);
            delayQueue.add(event);
        }
    }
}
```

```java
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020/7/19
 *
 * @author wang
 */
public class TestMain {
    public static void main(String[] args) throws InterruptedException {
        DelayQueue<Event> delayQueue = new DelayQueue<>();
        Thread [] threads = new Thread [5];
        for (int i = 0; i < threads.length; i++) {
            Task task = new Task(i + 1, delayQueue);
            threads[i] = new Thread(task);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        do {
            int counter = 0;
            Event event;
            do {
                event = delayQueue.poll();
                if (event != null){
                    counter++;
                }
            }while (event != null);
            System.out.printf("At %s you have read %d events\n", new Date(), counter);
            TimeUnit.MILLISECONDS.sleep(500);
        }while (delayQueue.size() > 0);
    }
}

//结果
Thread 1: Sun Jul 19 17:21:18 CST 2020
Thread 4: Sun Jul 19 17:21:21 CST 2020
Thread 5: Sun Jul 19 17:21:22 CST 2020
Thread 3: Sun Jul 19 17:21:20 CST 2020
Thread 2: Sun Jul 19 17:21:19 CST 2020
At Sun Jul 19 17:21:17 CST 2020 you have read 0 events
At Sun Jul 19 17:21:17 CST 2020 you have read 0 events
At Sun Jul 19 17:21:18 CST 2020 you have read 100 events
At Sun Jul 19 17:21:18 CST 2020 you have read 0 events
At Sun Jul 19 17:21:19 CST 2020 you have read 100 events
At Sun Jul 19 17:21:19 CST 2020 you have read 0 events
At Sun Jul 19 17:21:20 CST 2020 you have read 100 events
At Sun Jul 19 17:21:20 CST 2020 you have read 0 events
At Sun Jul 19 17:21:21 CST 2020 you have read 100 events
At Sun Jul 19 17:21:21 CST 2020 you have read 0 events
At Sun Jul 19 17:21:22 CST 2020 you have read 100 events
```

DelayQueue类还有其他的方法，如下所示。

* clear()：该方法会移除所有队列中的元素。
* offer(E e)：在该方法中，E代表DelayQueue类的泛型参数。该方法会插入一个新元素。
* peek()：该方法会获取但不移除队列中的首个元素。
* take()：该方法会获取并移除队列中的首个元素。如果队列中没有已激活元素，则执行的线程会阻塞直到有已激活元素可以被获取为止。