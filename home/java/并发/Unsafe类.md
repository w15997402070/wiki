# Unsafe类

[toc]


* long objectFieldOffset（Field field）方法：返回指定的变量在所属类中的内存偏移地址，该偏移地址仅仅在该Unsafe函数中访问指定字段时使用。如下代码使用Unsafe类获取变量value在AtomicLong对象中的内存偏移。

* int arrayBaseOffset（Class arrayClass）方法：获取数组中第一个元素的地址。
* int arrayIndexScale（Class arrayClass）方法：获取数组中一个元素占用的字节。
* boolean compareAndSwapLong（Object obj, long offset, long expect, longupdate）方法：比较对象obj中偏移量为offset的变量的值是否与expect相等，相等则使用update值更新，然后返回true，否则返回false。
* public native long getLongvolatile（Object obj, long offset）方法：获取对象obj中偏移量为offset的变量对应volatile语义的值。
* void putLongvolatile（Object obj, long offset, long value）方法：设置obj对象中offset偏移的类型为long的field的值为value，支持volatile语义。
* void putOrderedLong（Object obj, long offset, long value）方法：设置obj对象中offset偏移地址对应的long型field的值为value。这是一个有延迟的putLongvolatile方法，并且不保证值修改对其他线程立刻可见。只有在变量使用volatile修饰并且预计会被意外修改时才使用该方法。
* void park（boolean isAbsolute, long time）方法：阻塞当前线程，其中参数isAbsolute等于false且time等于0表示一直阻塞。time大于0表示等待指定的time后阻塞线程会被唤醒，这个time是个相对值，是个增量值，也就是相对当前时间累加time后当前线程就会被唤醒。如果isAbsolute等于true，并且time大于0，则表示阻塞的线程到指定的时间点后会被唤醒，这里time是个绝对时间，是将某个时间点换算为ms后的值。另外，当其他线程调用了当前阻塞线程的interrupt方法而中断了当前线程时，当前线程也会返回，而当其他线程调用了unPark方法并且把当前线程作为参数时当前线程也会返回
* void unpark（Object thread）方法：唤醒调用park后阻塞的线程。下面是JDK8新增的函数，这里只列出Long类型操作。
*  long getAndSetLong（Object obj, long offset, long update）方法：获取对象obj中偏移量为offset的变量volatile语义的当前值，并设置变量volatile语义的值为update。
*  long getAndAddLong（Object obj, long offset, long addValue）方法：获取对象obj中偏移量为offset的变量volatile语义的当前值，并设置变量值为原始值+addValue

## CAS详解

```java
public final native boolean compareAndSwapInt(Object var1, long valueOffset, int expect, int update);

public final native boolean compareAndSwapLong(Object var1, long valueOffset, long expect, long update);
```

* expect是指变量的旧值（是读出来的值，写回去的时候，希望没有被其他线程修改，所以称为expect）
* update是指变量的新值（修改过的，希望写入的值）
* valueOffset是某个成员变量在对应的类中的内存偏移量（该变量在内存中的位置），表示该成员变量本身

       ```java
//把成员变量转化成偏移量
public native long objectFieldOffset(Field var1);
       ```



* 当expect等于变量当前的值时，说明在修改的期间，没有其他线程对此变量进行过修改，所以可以成功写入，变量被更新为update，返回true；否则返回false。

### 内存操作

*  堆外内存,unsafe类的allocateMemory(long var1)方法可以直接在堆外分配一些大对象,这些内存对GC和JVM不可见,不再使用的时候需要通过freeMemory(long var1)方法回收内存;
//分配内存,相当于C++的malloc函数
* public native long allocateMemory(long var1);  
//扩充内存
* public native long reallocateMemory(long var1, long var3);
//在给定的内存中设置值
* public native void setMemory(Object var1, long var2, long var4, byte var6);
//内存拷贝
* public native void copyMemory(Object var1, long var2, Object var4, long var5, long var7);
//释放内存
* public native void freeMemory(long var1);
* 

例如 : DirectByteBuffer是实现堆外内存的重要类

### 获取给定地址值
//获取给定地址值,忽略修饰限定符的访问限制
* public native Object getObject(Object var1, long offset);
* public native int getInt(Object var1, long var2);
* public native boolean getBoolean(Object var1, long var2);
* public native byte getByte(Object var1, long var2);
* public native short getShort(Object var1, long var2);
* public native char getChar(Object var1, long var2);
* public native long getLong(Object var1, long var2);
* public native float getFloat(Object var1, long var2);
* public native double getDouble(Object var1, long var2);

### 为给定地址设置值

//为给定地址设置
* public native void putObject(Object var1, long offset, Object var4);
* public native void putInt(Object var1, long var2, int var4);
* public native void putBoolean(Object var1, long var2, boolean var4);
* public native void putByte(Object var1, long var2, byte var4);
* public native void putShort(Object var1, long var2, short var4);
* public native void putChar(Object var1, long var2, char var4);
* public native void putLong(Object var1, long var2, long var4);
* public native void putFloat(Object var1, long var2, float var4);
* public native void putDouble(Object var1, long var2, double var4);