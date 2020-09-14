# Iterator接口默认方法

```java
   //这个方法只能用一次
    default void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (hasNext())
            action.accept(next());
    }

    public static void main (String [] args){
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        //获取迭代器
        Iterator<String> it = list.iterator();
        //打印值
        it.forEachRemaining(s -> System.out.println(s));//1 2 3 4
        logger.info("-------------");
        it.forEachRemaining(s -> System.out.println(s));//这里什么都不会打印出来
        logger.info("-------------");
        list.forEach(s -> System.out.println(s));//这里会打印 1 2 3 4
        list.forEach(s -> System.out.println(s));//这里也会打印 1 2 3 4
    }
```