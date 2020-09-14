# BinaryOperator 

[toc]

## 源码

二元运算符

继承自`BiFunction`



```java
package java.util.function;

import java.util.Objects;
import java.util.Comparator;

/**
 * 接收两个相同的参数,结果返回一个相同的参数,是 BiFunction 的一个特例
 * Represents an operation upon two operands of the same type, producing a result
 * of the same type as the operands.  This is a specialization of
 * {@link BiFunction} for the case where the operands and the result are all of
 * the same type.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the operands and result of the operator
 *
 * @see BiFunction
 * @see UnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T,T,T> {
    /**
     * Returns a {@link BinaryOperator} which returns the lesser of two elements
     * according to the specified {@code Comparator}.
     *
     * @param <T> the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code BinaryOperator} which returns the lesser of its operands,
     *         according to the supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    public static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
    }

    /**
     * Returns a {@link BinaryOperator} which returns the greater of two elements
     * according to the specified {@code Comparator}.
     *
     * @param <T> the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code BinaryOperator} which returns the greater of its operands,
     *         according to the supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
    }
}
```

示例

```java
public static void main(String[] args) {
       BinaryOperator add =  new BinaryOperator<Integer>(){
            @Override
            public Integer apply(Integer integer, Integer integer2) {
                return integer+integer2;
            }
        };
       System.out.println(add.apply(5,6));//11

        BinaryOperator<Integer> comparator = (Integer param1,Integer param2) -> {
            return param2 > param1 ? param2:param1;
        };

        System.out.println(comparator.apply(8,2));//8
    
    
       Comparator<Integer> comparator1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? 1:-1;
            }
        };

        BinaryOperator<Integer> maxBy = BinaryOperator.maxBy(comparator1);
        System.out.println(maxBy.apply(2,3));//3
    }
```

## 相关类

* `DoubleBinaryOperator`

* `IntBinaryOperator`

* `LongBinaryOperator`

* `UnaryOperator`: 一元运算符

  ```java
  //接收一个参数,返回一个同样类型的数据
  @FunctionalInterface
  public interface UnaryOperator<T> extends Function<T, T> {
      /**
       * Returns a unary operator that always returns its input argument.
       *
       * @param <T> the type of the input and output of the operator
       * @return a unary operator that always returns its input argument
       */
      static <T> UnaryOperator<T> identity() {
          return t -> t;
      }
  }
  ```

  

* 