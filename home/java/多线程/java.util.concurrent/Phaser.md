# Phaser

```
arriveAndAwaitAdvance() 方法arriveAndAwaitAdvance()的作用与CountDownLatch类中的await()方法大体一样，通过从方法的名称解释来看，arrive是到达的意思，wait是等待的意思，而advance是前进、促进的意思，所以执行这个方法的作用就是当前线程已经到达屏障，在此等待一段时间，等条件满足后继续向下一个屏障继续执行。
arriveAndDeregister() 使线程退出,并且 parties 值减1
getPhase()  获取已经到达第几个屏障
onAdvance(int phase, int registeredParties) 通过新的屏障时被调用
     Phaser phaser = new Phaser() {
       protected boolean onAdvance(int phase, int parties) {
          //返回true 不等待了,Phase 呈无效/销毁状态
          //返回false 则 Phase继续工作
          return  super.onAdvance(phase, registeredParties); 
       }
      }
 getRegisteredParties() 获得注册的 parties 数量
 register() 没执行一次这个方法,就动态添加一个 parties 值
 bulkRegister(int parties) 批量增加 parties 数量
 getArrivedParties() 获得已经被使用的 parties 数量
 getUnarrivedParties()  获得还未使用的 parties 数量
 arrive() 是 parties 值加1,并且不在屏障处等待,直接向下面的代码继续运行
 awaitAdvance(int phase) 如果传入参数 phase 值和当前 getPhase() 方法返回值一样,则在屏障处等待,否则继续向下面运行
 forceTermination() 使Phaser 对象的屏障功能失效
 isTerminated() 判断 Phaser 对象是否已经是销毁状态
```

```java
import java.util.concurrent.Phaser;
/**
*   arriveAndAwaitAdvance()
* 类似体育竞赛中"赛段"的作用,运动员第一赛段结束后,开始休整准备,然后集体到达第二赛段的起跑点,等待比赛开始后运动员又继续比赛
*/
public class PhaseTest {

    public static void main (String [] args){
        Phaser phaser = new Phaser(3);
        PrintTools.phaser = phaser;
        Thread threadA = new PhaseThreadA(phaser);
        threadA.setName("A");
        threadA.start();
        Thread threadB = new PhaseThreadB(phaser);
        threadB.setName("B");
        threadB.start();
        Thread threadC = new PhaseThreadC(phaser);
        threadC.setName("C");
        threadC.start();
    }

}

class PrintTools{

    public  static Phaser phaser;

    public static void methodA(){
        System.out.println(Thread.currentThread().getName() + " A1 begin = "+System.currentTimeMillis());
        phaser.arriveAndAwaitAdvance();
        System.out.println(Thread.currentThread().getName() + " A1 end = "+System.currentTimeMillis());
        System.out.println(Thread.currentThread().getName() + " A2 begin = "+System.currentTimeMillis());
        phaser.arriveAndAwaitAdvance();
        System.out.println(Thread.currentThread().getName() + " A2 end = "+System.currentTimeMillis());
    }

    public static void methodB(){
        try {
            System.out.println(Thread.currentThread().getName() + " B1 begin = "+System.currentTimeMillis());
            Thread.sleep(5000);
            phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getName() + " B1 end = "+System.currentTimeMillis());
            System.out.println(Thread.currentThread().getName() + " B2 begin = "+System.currentTimeMillis());
            Thread.sleep(5000);
            phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getName() + " B2 end = "+System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class PhaseThreadA extends Thread{

    Phaser phaser;

    public PhaseThreadA(Phaser phaser){
        super();
        this.phaser = phaser;
    }

    @Override
    public void run() {
        PrintTools.methodA();
    }
}

class PhaseThreadB extends Thread{

    Phaser phaser;

    public PhaseThreadB(Phaser phaser){
        super();
        this.phaser = phaser;
    }

    @Override
    public void run() {
        PrintTools.methodA();
    }
}

class PhaseThreadC extends Thread{

    Phaser phaser;

    public PhaseThreadC(Phaser phaser){
        super();
        this.phaser = phaser;
    }

    @Override
    public void run() {
        PrintTools.methodB();
    }
}
```

