# Function

[toc]

## 源码

```java
package java.util.function;

import java.util.Objects;

/**
 * 一个方法接收一个参数,并且返回一个结果
 * Represents a function that accepts one argument and produces a result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(Function)
     * 返回一个 先执行before函数对象apply方法，再执行当前函数对象apply方法的 函数对象。
     */
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     * 返回一个 先执行当前函数对象apply方法， 再执行after函数对象apply方法的 函数对象。
     * @see #compose(Function)
     */
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
```

简单示例

```java
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunctionTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("apple");
        list.add("orange");
        list.add("red");
        list.add("a");

//        List<Integer> lengthList = getElementLength(list,(String s) -> s.length());

        List<Integer> lengthList = getElementLength(list, new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                System.out.println("Function : "+s);
                return s.length();
            }
        }.compose(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        System.out.println("compose : "+s);
                        return s+"==";
                    }

        }).andThen(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println("andThen : "+integer);
                return integer+10;
            }
        }));
        lengthList.forEach( i -> {
            System.out.println(i);
        });
    }

    public static List<Integer> getElementLength(List<String> list, Function<String,Integer> function){
        List<Integer> lengthList = new ArrayList<>();
        list.forEach(s -> {
            lengthList.add(function.apply(s));
        });
        return lengthList;
    }
}
```

执行结果

```java
compose : apple
Function : apple==
andThen : 7
compose : orange
Function : orange==
andThen : 8
compose : red
Function : red==
andThen : 5
compose : a
Function : a==
andThen : 3
17
18
15
13
```

## 相关类

`BiFunction<T, U, R>`

```java
import java.util.Objects;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R apply(T t, U u);

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }
}
```

示例

```java
    @Test
    public void testBiFunction(){
        BiFunction<String, String, String> function1 = (s1, s2) -> {
            String s3 = s1 + s2;
            return s3;
        };
        System.out.println(function1.apply("aa", "bb"));

        BiFunction<Integer, Integer, Integer> function2 = (a, b) -> a + b;
        System.out.println(function2.apply(100, 200));
    }

//结果
aabb
300
    
    
    @Test
    public void testBiFunctionAndThen(){
        BiFunction<Integer, Integer, Integer> function1 = (a, b) -> a + b;
        Function<Integer, Integer> function2 = (n) -> n*n;

        //Using andThen()
        System.out.println(function1.andThen(function2).apply(5, 3));
        System.out.println(function1.andThen(function2).apply(12, 2));
    }

//结果
64
196
```

基础类型

* `IntFunction<R>`

* `DoubleFunction<R>`

* `IntToDoubleFunction`

  ```java
  //入参为int ,出参为double
  double applyAsDouble(int value);
  ```

* `IntToLongFunction`

* `LongFunction<R>`

* `LongToDoubleFunction`

* `LongToIntFunction`

* `ToDoubleBiFunction<T, U>`

  ```java
  //入参为T,U 结果为double
  double applyAsDouble(T t, U u);
  ```

* `ToDoubleFunction<T>`

  ```java
  double applyAsDouble(T value);
  ```

* `ToIntBiFunction<T, U>`

* `ToIntFunction<T>`

* `ToLongBiFunction<T, U>`

* `ToLongFunction<T>`