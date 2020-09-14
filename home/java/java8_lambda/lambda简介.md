# lambda简介

可以把Lambda表达式理解为简洁地表示可传递的匿名函数的一种方式：它没有名称，但它有参数列表、函数主体、返回类型，可能还有一个可以抛出的异常列表。这个定义够大的，让我们慢慢道来。

- 匿名我们说匿名，是因为它不像普通的方法那样有一个明确的名称：写得少而想得多！
- 函数—我们说它是函数，是因为Lambda函数不像方法那样属于某个特定的类。但和方法一样，Lambda有参数列表、函数主体、返回类型，还可能有可以抛出的异常列表。
- 传递—Lambda表达式可以作为参数传递给方法或存储在变量中。
- 简洁——无需像匿名类那样写很多模板代码。

```java
  //lambda 参数     箭头     lambda主体
  (Apple a,Apple b) -> a.getWeight().compareTo(b.getWeight());
```

*java8中有效的 lambda 表达式*

```java
//1. 具有一个 String 类型的参数并且返回一个 int .lambda 没有return 语句,因为已经隐含了return
(String s) -> s.length();
//2. 有一个 Apple 类型的参数并且返回一个 Boolean 
(Apple a) -> a.getWeight() > 150
//3. 具有两个 int 类型的参数而没有返回值(void返回).
(int x,int y) -> {
    System.out.println("Result : "+(x+y));
    }
//4. 没有参数,返回一个 int 
() -> 42;
//5. 具有两个 Apple 类型的参数,返回一个 int : 比较两个 Apple 的重量 
(Apple a1,Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```