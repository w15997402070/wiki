# Exchanger

类 `Exchanger`的功能可以使2个线程之间传输数据,它比生产者/消费者模式使用的wait/notify要更加方便.
`Exchanger` 主要学习点就是 `exchange()`方法.

## 方法 `exchange()` 阻塞的特性

类 `Exchanger` 中的 `exchange()` 方法具有阻塞的特色,此方法被调用后等待其他线程来取得数据,如果没有其他线程取得数据,则一直阻塞等待.

```java
import java.util.concurrent.Exchanger;

public class ExchangerTest {
    public static void main (String [] args){
        Exchanger<String> exchanger = new Exchanger<>();
        ThreadExchangerA threadExchangerA = new ThreadExchangerA(exchanger);
        threadExchangerA.start();
        System.out.println("main end!");
    }
}
class ThreadExchangerA extends Thread{
    
    private Exchanger<String> exchanger;
    
    public ThreadExchangerA(Exchanger<String> exchanger){
        super();
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        try {
            System.out.println("在线程A中得到线程B的值 : "+exchanger.exchange("值A"));
            System.out.println("A end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//结果
main end!
```

## 方法 `exchange()` 传递数据

```java
import java.util.concurrent.Exchanger;

public class ExchangerTest {
    public static void main (String [] args){
        Exchanger<String> exchanger = new Exchanger<>();
        ThreadExchangerA threadExchangerA = new ThreadExchangerA(exchanger);
        ThreadExchangerB threadExchangerB = new ThreadExchangerB(exchanger);
        threadExchangerA.start();
        threadExchangerB.start();
        System.out.println("main end!");
    }
}

class ThreadExchangerA extends Thread{

    private Exchanger<String> exchanger;

    public ThreadExchangerA(Exchanger<String> exchanger){
        super();
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        try {
            System.out.println("在线程A中得到线程B的值 : "+exchanger.exchange("值A"));
            System.out.println("A end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class ThreadExchangerB extends Thread{

    private Exchanger<String> exchanger;

    public ThreadExchangerB(Exchanger<String> exchanger){
        super();
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        try {
            System.out.println("在线程B中得到线程A的值 : "+exchanger.exchange("值B"));
            System.out.println("B end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//结果
main end!
在线程A中得到线程B的值 : 值B
A end
在线程B中得到线程A的值 : 值A
B end
```

## V exchange(V x, long timeout, TimeUnit unit) 与超时

当调用 exchange(V x,long timeout,TimeUnit unit) 方法后在指定的时间内没有其他线程获取数据,则出现超时异常

```java
  @Override
    public void run() {
        try {
            //设置了超时时间
            System.out.println("在线程A中得到线程B的值 : "+exchanger.exchange("值A",5,TimeUnit.SECONDS));
            System.out.println("A end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

//结果
main end!
java.util.concurrent.TimeoutException
	at java.util.concurrent.Exchanger.exchange(Exchanger.java:626)
	at com.test.ThreadExchangerA.run(ExchangerTest.java:29)

```

