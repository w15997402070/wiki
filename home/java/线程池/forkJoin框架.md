# 分支/合并框架

在JDK中并行执行框架Fork-Join使用了“工作窃取（work-stealing）”算法，它是指某个线程从其他队列里窃取任务来执行，那这样做有什么优势或者目的是什么呢？

```
比如要完成一个比较大的任务，完全可以把这个大的任务分割为若干互不依赖的子任务/小任务，为了更加方便地管理这些任务，于是把这些子任务分别放到不同的队列里，这时就会出现有的线程会先把自己队列里的任务快速执行完毕，而其他线程对应的队列里还有任务等待处理，完成任务的线程与其等着，不如去帮助其他线程分担要执行的任务，于是它就去其他线程的队列里窃取一个任务来执行，这就是所谓的“工作窃取（work-stealing）”算法。
```

分支/合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任务的结果合并起来生成整体结果。它是Executorservice接口的一个实现，它把子任务分配给线程池（称为ForkJoinPoo1）中的工作线程。首先来看看如何定义任务和子任务。

## RecursiveTask 可以获取返回值

```java
package com.test;

import java.util.concurrent.RecursiveTask;

/**
 * 分支并行框架进行求和
 *
 *  继承 RecursiveTask 来创建可以用于分支/合并框架的任务
 */
public class ForkJoinSumCaculator extends RecursiveTask<Long> {
    //处理的数组
    private final long [] numbers;
    //子任务处理的数组的起始和终止位置
    private final int start;
    private final int end;
    //不再将任务分解为子任务的数组大小
    private static final long THRESHOLD = 10_000;

    /**
     * 公共构造函数用于创建主任务
     * @param numbers
     */
    public ForkJoinSumCaculator(long [] numbers){
        this(numbers,0,numbers.length);
    }

    /**
     * 私有构造函数用于以递归方式为主任务创建子任务
     * @param numbers
     * @param start
     * @param end
     */
    private ForkJoinSumCaculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    /**
     * The main computation performed by this task.
     *
     * @return the result of the computation
     */
    @Override
    protected Long compute() {
        int length = end -start;
        if (length <= THRESHOLD){
            return computeSequentially();
        }
        //创建一个子任务来为数组的前一半求和
        ForkJoinSumCaculator leftTask = new ForkJoinSumCaculator(numbers,start,start+length/2);
        //利用另一个 ForkJoinPool 线程异步执行新创建的子任务
        leftTask.fork();
        //创建一个子任务为数组的后一半求和
        ForkJoinSumCaculator rightTask = new ForkJoinSumCaculator(numbers,start+length/2,end);
        //同步执行第二个子任务,有可能允许进一步递归划分
        Long rightResult = rightTask.compute();
        //读取第一个子任务的结果,如果尚未完成就等待
        Long leftResult = leftTask.join();
        return leftResult+rightResult;
    }

    /**
     * 在子任务不再可分时计算结果的简单算法
     * @return
     */
    private Long computeSequentially() {
        long sum = 0L;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}

```

```java
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

public class ForkJoinTest {

    public static void main (String [] args){
        long n = 100000000;
        long [] numbers = LongStream.rangeClosed(1, n).toArray();
        long t1 = System.currentTimeMillis();
//        System.out.println("forkjoin begin : "+t1);
        ForkJoinSumCaculator task = new ForkJoinSumCaculator(numbers);
        long result =  new ForkJoinPool().invoke(task);
//        System.out.println(result);
        long t2 = System.currentTimeMillis();
        System.out.println("result : "+result +" forkjoin time : "+(t2-t1));

        long t3 = System.currentTimeMillis();
        long result2 = caculateSum(numbers);
        long t4 = System.currentTimeMillis();
        System.out.println("result : "+result2 +" thread time : "+(t4-t3));
    }

    private static long caculateSum(long [] numbers){
        long sum = 0;
        for (int i = 0; i < numbers.length ; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
```

## RecursiveAction 没有返回值

```java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinTest2 {

    public static void main (String [] args) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(new MyRecursiveAction(1,10));
        Thread.sleep(3000);
    }
}


class MyRecursiveAction extends RecursiveAction{

    private int beginValue;
    private int endValue;

    public MyRecursiveAction(int beginValue,int endValue){
        super();
        this.beginValue = beginValue;
        this.endValue = endValue;
    }
    @Override
    protected void compute() {
        System.out.println(Thread.currentThread().getName() + " begin -----");
        if (endValue - beginValue > 2){
            int mediumValue = (beginValue+endValue)/2;
            MyRecursiveAction leftAction = new MyRecursiveAction(beginValue, mediumValue);
            MyRecursiveAction rightAction = new MyRecursiveAction(mediumValue + 1, endValue);
            this.invokeAll(leftAction,rightAction);
        }else {
            System.out.println("打印组合为 : "+beginValue + " ==== "+endValue);
        }
    }
}
//结果
ForkJoinPool-1-worker-1 begin -----
ForkJoinPool-1-worker-1 begin -----
ForkJoinPool-1-worker-1 begin -----
打印组合为 : 1 ==== 3
ForkJoinPool-1-worker-1 begin -----
打印组合为 : 4 ==== 5
ForkJoinPool-1-worker-2 begin -----
ForkJoinPool-1-worker-2 begin -----
ForkJoinPool-1-worker-3 begin -----
打印组合为 : 6 ==== 8
打印组合为 : 9 ==== 10
```



## 使用分支/合并框架的最佳做法

虽然分支/合并框架还算简单易用，不幸的是它也很容易被误用。以下是几个有效使用它的最佳做法。

- 对一个任务调用join方法会阻塞调用方，直到该任务做出结果。因此，有必要在两个子任务的计算都开始之后再调用它。否则，你得到的版本会比原始的顺序算法更慢更复杂，因为每个子任务都必须等待另一个子任务完成才能启动。
- 不应该在RecursiveTask内部使用ForkJoinPool的invoke方法。相反，你应该始终直接调用compute或fork方法，只有顺序代码才应该用invoke来启动并行计算。
  口对子任务调用fork方法可以把它排进ForkJoinpoo1。同时对左边和右边的子任务调用它似乎很自然，但这样做的效率要比直接对其中一个调用compute低。这样做你可以为其中一个子任务重用同一线程，从而避免在线程池中多分配一个任务造成的开销。
- 调试使用分支/合并框架的并行计算可能有点棘手。特别是你平常都在你喜欢的IDE里面看栈跟踪（stack trace）来找问题，但放在分支一合并计算上就不行了，因为调用compute的线程并不是概念上的调用方，后者是调用fork的那个。
- 和并行流一样，你不应理所当然地认为在多核处理器上使用分支/合并框架就比顺序计算快。我们已经说过，一个任务可以分解成多个独立的子任务，才能让性能在并行化时有所提升。所有这些子任务的运行时间都应该比分出新任务所花的时间长；一个惯用方法是把输入/输出放在一个子任务里，计算放在另一个里，这样计算就可以和输入/输出同时进行。此外，在比较同一算法的顺序和并行版本的性能时还有别的因素要考虑。就像任何其他Java代码一样，分支/合并框架需要“预热”或者说要执行几遍才会被JIT编译器优化。这就是为什么在测量性能之前跑几遍程序很重要，我们的测试框架就是这么做的。同时还要知道，编译器内置的优化可能会为顺序版本带来一些优势（例如执行死码分析——删去从未被使用的计算）。
  对于分支/合并拆分策略还有最后一点补充：你必须选择一个标准，来决定任务是要进一步拆分还是已小到可以顺序求值。我们会在下一节中就此给出一些提示。

