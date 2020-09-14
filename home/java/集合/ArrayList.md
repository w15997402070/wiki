1. ArrayList实现了RandomAcess接口
 ```java

      for (int i=0, n=list.size(); i &lt; n; i++)
          list.get(i);

// * runs faster than this loop:*

      for (Iterator i=list.iterator(); i.hasNext(); )
         i.next();
```

2. 通用判断
```java
      if (list instanceof RandomAccess){
           for (int i = 0; i < list.size(); i++){
           }
       }else{
           Iterator<?> iterator = list.iterator();
           while (iterator.hasNext()){
               iterator.next();
           }
       }
```
3. ArrayList和LinkedList的区别

- ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
- 对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
- 对于新增和删除操作add和remove，LinkedList比较占优势，因为ArrayList要移动数据。

  4. ArrayList和Vector的区别

- Vector和ArrayList几乎是完全相同的,唯一的区别在于Vector是同步类(synchronized)，属于强同步类。因此开销就比ArrayList要大，访问要慢。正常情况下,大多数的Java程序员使用ArrayList而不是Vector,因为同步完全可以由程序员自己来控制。
- Vector每次扩容请求其大小的2倍空间，而ArrayList是1.5倍。
- Vector还有一个子类Stack 
