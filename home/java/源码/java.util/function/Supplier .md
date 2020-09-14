# Supplier

[toc]

## 源码

```java
package java.util.function;

/**
 * Represents a supplier of results.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
```

示例

```java
    public static void main(String[] args) {

        Supplier<String> uuidSupplier = () -> {
            return UUID.randomUUID().toString();
        };

        System.out.println(uuidSupplier.get());
    }
```

## 相关类



* `BooleanSupplier`: `boolean getAsBoolean();`
* `DoubleSupplier`: `double getAsDouble();`
* `IntSupplier`: `int getAsInt();`
* `LongSupplier`:`long getAsLong();`