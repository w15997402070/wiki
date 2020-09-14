# Java并发包中原子操作类原理剖析
[toc]

## AtomicLong

源码:

```java
public class AtomicLong extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 1927816293512124184L;

     // setup to use Unsafe.compareAndSwapLong for updates
     //获取unsafe实例
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    //存放value的偏移量
    private static final long valueOffset;
    //判断JVM是否支持Long类型的CAS  
    static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();
    
    private static native boolean VMSupportsCS8();

    static {
        try {
            //获取偏移量
            valueOffset = unsafe.objectFieldOffset
                (AtomicLong.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile long value;
    
    public AtomicLong(long initialValue) {
        value = initialValue;
    }
    
    public AtomicLong() {
    }

    public final long get() {
        return value;
    }
    
    public final void set(long newValue) {
        value = newValue;
    }
    //递增
    public final long incrementAndGet() {
        return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
    }
    //递减
    public final long decrementAndGet() {
        return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
    }
    //调用unsafe方法,原子性设置value值为原始值+1,返回值为原始值
    public final long getAndIncrement() {
        return unsafe.getAndAddLong(this, valueOffset, 1L);
    }
    //调用unsafe方法,原子性设置value值为原始值-1,返回值为原始值
    public final long getAndDecrement() {
        return unsafe.getAndAddLong(this, valueOffset, -1L);
    }

}
```

## LongAdder

AtomicLong通过CAS提供了非阻塞的原子性操作，相比使用阻塞算法的同步器来说它的性能已经很好了，但是JDK开发组并不满足于此。使用AtomicLong时，在高并发下大量线程会同时去竞争更新同一个原子变量，但是由于同时只有一个线程的CAS操作会成功，这就造成了大量线程竞争失败后，会通过无限循环不断进行自旋尝试CAS的操作，而这会白白浪费CPU资源。

JDK 8新增了一个原子性递增或者递减类LongAdder用来克服在高并发下使用AtomicLong的缺点。既然AtomicLong的性能瓶颈是由于过多线程同时去竞争一个变量的更新而产生的，那么如果把一个变量分解为多个变量，让同样多的线程去竞争多个资源，是不是就解决了性能问题？是的，LongAdder就是这个思路

## AtomicStampedReference和AtomicMarkableReference

### ABA问题 : 

CAS都是基于“值”来做比较的。但如果另外一个线程把变量的值从A改为B，再从B改回到A，那么尽管修改过两次，可是在当前线程做CAS操作的时候，却会因为值没变而认为数据没有被其他线程修改过，这就是所谓的ABA问题。

### 解决 : 

要解决ABA 问题，不仅要比较“值”，还要比较“版本号”，而这正是AtomicStamped-Reference做的事情

```java
public boolean compareAndSet(V   expectedReference,
                                 V   newReference,
                                 int expectedStamp,
                                 int newStamp)
```

前的CAS只有两个参数，这里的CAS有四个参数，后两个参数就是版本号的旧值和新值。当expectedReference！=对象当前的reference时，说明该数据肯定被其他线程修改过；当expectedReference==对象当前的reference时，再进一步比较expectedStamp是否等于对象当前的版本号，以此判断数据是否被其他线程修改过。

## 解决AtomicInteger或AtomictLong的ABA问题

要解决Integer或者Long型变量的ABA问题，为什么只有AtomicStampedReference，而没有AtomicStampedInteger或者AtomictStampedLong呢？

因为这里要同时比较数据的“值”和“版本号”，而Integer型或者Long型的CAS没有办法同时比较两个变量，于是只能把值和版本号封装成一个对象，也就是这里面的Pair 内部类，然后通过对象引用的CAS来实现

## AtomicXXXFieldUpdater

如果是一个已经有的类，在不能更改其源代码的情况下，要想实现对其成员变量的原子操作，就需要AtomicIntegerFieldUpdater、AtomicLongFieldUpdater 和AtomicReferenceFieldUpdater

## AtomicIntegerArray

Concurrent包提供了AtomicIntegerArray、AtomicLongArray、AtomicReferenceArray三个数组元素的原子操作.注意，这里并不是说对整个数组的操作是原子的，而是针对数组中一个元素的原子操作而言。