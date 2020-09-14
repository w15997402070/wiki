# Consumer

[toc]

## 源码

```java
package java.util.function;

import java.util.Objects;

/**
 * 接收一个参数并且不反回结果
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

    /**
     * Returns a composed(组合) {@code Consumer} that performs(执行), in sequence(按顺序), this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
```



## 相关类

### `BiConsumer<T, U>`

接收两个参数并且没有返回值

#### 源码

```java
package java.util.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.  This is the two-arity specialization of {@link Consumer}.
 * Unlike most other functional interfaces, {@code BiConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u);

    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
```

`BiConsumer<T, U>`在`java.util.Map<K,V>`中遍历map有用到

```java
//map中遍历 map的方法  
default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch(IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }
```

简单示例

```java
public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("test1","1");
        map.put("test2","2");
        map.put("test3","3");
        map.put("test4","4");
//        map.forEach((k,v) -> {
//            System.out.println("key : "+k+" --- value : "+v);
//        });

        map.forEach(((BiConsumer<String, String>) (k, v) -> {
            System.out.println("key : " + k + " --- value : " + v);
        }).andThen(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                System.out.println("value : "+s2+" --- key : "+s);
            }
        }));
    }
```

### 基本类型的Consumer接口

* `DoubleConsumer`

* `IntConsumer`

* `LongConsumer`

* `ObjDoubleConsumer`

* `ObjIntConsumer`

  接收两个参数,第一个参数为Object,第二个参数为Int

  ```java
  @FunctionalInterface
  public interface ObjIntConsumer<T> {
  
      /**
       * Performs this operation on the given arguments.
       *
       * @param t the first input argument
       * @param value the second input argument
       */
      void accept(T t, int value);
  }
  ```

  

* `ObjLongConsumer`