# IdentityHashMap

[toc]



1. IdentityHashMap 使用 == 做判断key相等的条件

IdentityHashMap的用法和HashMap的用法差不多，他们之间最大的区别就是IdentityHashMap判断两个key是否相等，是通过严格相等即（key1==key2）来判读的，而HashMap是通过equals()方法和hashCode（）这两个方法来判断key是否相等的

```java
public static void main(String[] args) {
        Map<String,String> hashMap = new HashMap<String,String>();
        //hashmap中通过equals判断相等
        hashMap.put(new String("test"),"test1");
        hashMap.put(new String("test"),"test2");
        hashMap.forEach((k,v) -> {
            System.out.println("key : "+k+" | "+"value : "+v);//所以这里只会打印key : test | value : test2
        });
    
    // IdentityHashMap 通过 == 判断相等
        IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put(new String("test"),"test1");
        identityHashMap.put(new String("test"),"test2");
        identityHashMap.forEach((k,v) -> {
            System.out.println("key : "+k+" | "+"value : "+v);
            //打印结果
            //key : test | value : test1
            //key : test | value : test2

        });
}
```

2. IdentityHashMap 可以保存key为null的数据

   ```java
   public static void main(String[] args) {
       IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
   
       identityHashMap.put(new String("test"),"test1");
       identityHashMap.put(new String("test"),"test2");
       identityHashMap.put(null,"test3");
       identityHashMap.forEach((k,v) -> {
           System.out.println("key : "+k+" | "+"value : "+v);
       });
   }
   //结果
   key : test | value : test1
   key : null | value : test3
   key : test | value : test2
   ```

   

3. 