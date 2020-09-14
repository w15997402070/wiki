# optional简介

`optional<T>`类（`java.util.Optional`）是一个容器类，代表一个值存在或不存在。在上面的代码中，findAny可能什么元素都没找到。Java8的库设计人员引入了`optional<T>`，这样就不用返回众所周知容易出问题的null了。我们在这里不会详细讨论optional，因为第10章会详细解释你的代码如何利用optiona1，避免和nu11检查相关的bug。不过现在，了解一下optiona1里面几种可以迫使你显式地检查值是否存在或处理值不存在的情形的方法也不错。

- isPresent（）将在optional包含值的时候返回true，否则返回false。
- ifPresent（`Consumer<T>block`）会在值存在的时候执行给定的代码块。我们在第3章介绍了consumer函数式接口；它让你传递一个接收T类型参数，并返回void的Lambda表达式。
- T get（）会在值存在时返回值，否则抛出一个NosuchElement异常。
- T orElse（T other）会在值存在时返回值，否则返回一个默认值。
例如，在前面的代码中你需要显式地检查optiona1对象中是否存在一道菜可以访问其名称：

```java
menu.stream()
    .filter（Dish:：isVegetarian）|optional<pish，|
    .findAny（）//返回一个Optional<Dish>
    .ifPresent（d -> System.out.printin（d.getName（））；//如果包含一个值就打印它，否s则什么都不做
```
