# Type接口简介

Type有四个接口和一个实现类 :

```
java.lang.Class 实现类,表示原始类型,可以通过`类名.class`,`对象.getClass()`或`Class.forName("类名")`得到

java.lang.reflect.ParameterizedType 表示参数化类型,例如List<String>,Map<Integer，String>、Service<User>这种带有泛型的类型。
    ParameterizedType接口中常用的方法有三个，分别是：
    - Type getRawType() ——返回参数化类型中的原始类型，例如List<String>的原始类型为List。
    - Type[]getActualTypeArguments() ——获取参数化类型的类型变量或是实际类型列表，例如Map<Integer，String>的实际泛型列表Integer和String。需要注意的是，该列表的元素类型都是Type，也就是说，可能存在多层嵌套的情况。
    - Type getOwnerType() ——返回是类型所属的类型，例如存在A<T>类，其中定义了内部类InnerA<I>，则InnerA<I>所属的类型为A~T>，如果是顶层类型则返回null。这种关系比较常见的示例是Map<K，V>接口与Map.Entry<K，V>接口，Map<K，V>接口是Map.Entry<K，V>接口的所有者。
    
java.lang.reflect.TypeVariable 表示的是类型变量，它用来反映在JVM编译该泛型前的信息。例如List<T>
中的T就是类型变量，它在编译时需被转换为一个具体的类型后才能正常使用。
该接口中常用的方法有三个，分别是：
- Type [] getBounds()—获取类型变量的上边界，如果未明确声明上边界则默认为Object。例如class Test<K extends Person>中K的上界就是Person。
- D getGenericDeclaration() ——获取声明该类型变量的原始类型，例如class Test<K extends Person>中的原始类型是Test。
- String getName()——获取在源码中定义时的名字，上例中为K。

java.lang.reflect.GenericArrayType 表示的是数组类型且组成元素是ParameterizedType 或TypeVariable。
例如List<String>[] 或 T[]。该接口只有Type getGenericComponentType()一个方法，它返回数组的组成元素。

java.lang.reflect.WildcardType 表示的是通配符泛型，例如？extends Number和？super Integer。
WildcardType 接口有两个方法，分别是：
- Type [] getUpperBounds() ——返回泛型变量的上界。
- Type [] getLowerBounds() ——返回泛型变量的下界。
```

