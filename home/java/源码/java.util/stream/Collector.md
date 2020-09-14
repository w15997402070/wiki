# Collector

[toc]

## 源码

```java
/**
* T是流中要收集的项目的泛型
* A是累加器的类型
* R是收集操作得到的对象的类型
*/
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    BinaryOperator<A> combiner();
    Function<A, R> finisher();
    Set<Characteristics> characteristics();
}
```

