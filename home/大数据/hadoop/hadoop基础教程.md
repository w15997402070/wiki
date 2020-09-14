# hadoop 基础教程

## `Writable` 接口和 `WritableComparable` 接口

`org.apache.hadoop.io` 包含了 `Writable` 接口的定义 :

```java
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Writable {
    void write(DataOutput out) throws IOException;
    void readFields(DataInput in) throws IOException;
}
```

`Writable` 接口的主要目的是当数据在网上传输或从硬盘读写时,提供数据的序列化和反序列化机制.
所有用作mapper或reduce输入或输出值的数据类型(也就是说V1,V2或V3)都必须实现这个接口.

用作键数据(K1,K2,K3)的有着更为严格的要求 : 除`Writable` 接口之外,它必须实现java中的 `Comparable` 接口 :

```java
public interface Comparable<T> {

    public int compareTo(T o);
}
```

Hadoop在 `org.apache.hadoop.io` 包里提供了一个 `WritableComparable` 接口

```java
public interface WritableComparable extends Writable,Comparable{

}
```

## `Wrapper` 类介绍

### 1. 原始包装类

- BooleanWritable
- ByteWritable
- DoubleWritable
- FloatWritable
- IntWritable
- LongWritable
- VIntWritable  可变长度的整数类型
- VlongWritable 可变长度的长整数类型

### 2.数组包装类

- ArrayWritable
- TwoDArrayWritable

### 3.Map 包装类

*他们被定义为 `Map<Writable,Writable>` 并有效管理部分内部运行时类型检查.这就意味着弱化了编译类型检查,所以要当心*

- AbatractMapWritable : 这是其他具体的Writable map包装类的基类
- MapWritable : 这是通用的map包装类,将 `Writable` 键映射为 `Writable` 值
- SortedMapWritable : 这是 `MapWritable` 类的一个特殊实现,它同时实现了 `SortedMap` 接口

### 4.其他包装类

- CompressedWritable : 这是一个基类,它允许大型对象保持压缩,直到其属性被显示访问
- ObjectWritable : 这是多用途的通用对象包装
- NullWritable : 这是一个表示空值的对象
- VersionedWritable : 这是一个允许Writable类跟踪版本号的基本实现

## `Mapper` 类

### Mapper类是一个泛型类,分别指定map函数的输入键,输入值,输出键和输出值的类型

```java
class MaxTemperatureMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable> {

    @Override
    public void map(LongWritable key,Text vlaue,CommandContext context) {

    }
}
```

### Reducer类类似,reduce函数也有四个形式参数类型分别指定输入和输出类型.reduce函数的输入类型必须匹配map函数的输出类型,即 Text 类型和 IntWritable 类型

```java
class MaxTemperatureReducer  implements Reducer<Text,IntWritable,Text,IntWritable> {

    @Override
    public void reduce(Text key,Iterable<IntWritable> vlaues,Context context) {
        int maxValue = Integer.MAX_VALUE;
        for (IntWritable value : vlaues) {
            maxValue = Math.max(maxValue, maxValue.get());
        }
        context.write(key,new IntWritable(maxValue));
    }
}
```