# synchronized 关键字

[toc]

## synchronized关键字介绍

synchronized块是Java提供的一种原子性`内置锁`，Java中的每个对象都可以把它当作一个同步锁来使用，这些Java内置的使用者看不到的锁被称为内部锁，也叫作`监视器锁`。线程的执行代码在进入synchronized代码块前会自动获取内部锁，这时候其他线程访问该同步代码块时会被阻塞挂起。拿到内部锁的线程会在正常退出同步代码块或者抛出异常后或者在同步块内调用了该内置锁资源的wait系列方法时释放该内置锁。内置锁是排它锁，也就是当一个线程获取这个锁后，其他线程必须等待该线程释放锁后才能获取该锁。另外，由于Java中的线程是与操作系统的原生线程一一对应的，所以当阻塞一个线程时，需要从用户态切换到内核态执行阻塞操作，这是很耗时的操作，而synchronized的使用就会导致上下文切换。

在Java对象头里，有一块数据叫Mark Word。在64位机器上，Mark Word是8字节（64位）的，这64位中有2个重要字段：锁标志位和占用该锁的thread ID。

## synchronized的内存语义

![image-20200705203515084](D:\data\notes\notes\java\并发\synchronized关键字\image-20200705203515084.png)

共享变量内存可见性问题主要是由于线程的工作内存导致的,内存语义就可以解决共享变量内存可见性问题
进入synchronized块的内存语义是把在synchronized块内使用到的变量从线程的工作内存中清除，这样在synchronized块内使用到该变量时就不会从线程的工作内存中获取，而是直接从主内存中获取。退出synchronized块的内存语义是把在synchronized块内对共享变量的修改刷新到主内存。

其实这也是加锁和释放锁的语义，当获取锁后会清空锁块内本地内存中将会被用到的共享变量，在使用这些共享变量时从主内存进行加载，在释放锁时将本地内存中修改的共享变量刷新到主内存。除可以解决共享变量内存可见性问题外，synchronized经常被用来实现原子性操作。另外请注意，synchronized关键字会引起线程上下文切换并带来线程调度开销。


## Synchronized 学习

* 内置锁
* synchronized代码块
* 加在方法上,同步方法
* 方法内部?加在对象上
对于非静态成员函数，锁其实是加在对象a上面的；对于静态成员函数，锁是加在A.class上面的。当然，class本身也是对象
非静态成员函数 : 
```java
class A {
    public void synchronized f1(){
        
    }
}
//等价于
class A {
    pubic void f1() {
        synchronized(this){
            
        }
    }
}
```

静态成员函数 : 
```java
class A {
    public static void synchronized f2(){
        
    }
}
//等价于
class A {
    pubic void f2() {
        synchronized(A.class){
            
        }
    }
}
```

* 可重入锁?为什么要是重入锁
* 排它锁
* 文档：锁.md
链接：http://note.youdao.com/noteshare?id=60315bdbe24361afcdfbb37d5bd3e6db&sub=WEB9787f1f207f6729d1f9013c18841052f
* 内存语义: 参见 上面的内存语义
* 副作用

