# Buffer简介

Buffer 是抽象类,有`IntBuffer`,`FloatBuffer`,`CharBuffer`,`DoubleBuffer`,`ShortBuffer`,`LongBuffer`,`ByteBuffer`子类,这几个子类也是抽象类.(没有`BooleanBuffer`),这几个抽象类通过静态的`wrap()`方法构建类的对象.

## 四个核心技术点

* capacity (容量) : 代表包含元素的数量,不能为负
* limit (限制) : 代表第一个不应该读取或写入元素的index
* position (位置)
* mark (标记)  类似于爬山时在关键路口设置的路标,目的在于原路返回时找到回去的路

这四个之间的大小关系为 : 

`0 <= mark <= position <= limit <= capacity`

```
int capacity() 获取容量
int limit() 返回缓冲区限制
Buffer limit(int newLimit) 设置缓冲区限制
int position() 返回缓冲区的位置
Buffer position(int newPosition) 设置缓冲区新的位置
int remaining() 剩余空间获取,返回limit与position之间的元素数
Buffer mark() 在此缓冲区位置设置标记

boolean isReadOnly() 判断只读
boolean isDirect() 判断是否是直接缓冲区
Buffer clear() 还原缓冲区的初始状态
Buffer flip() 反转缓冲区,首先将限制设置为当前位置,然后将位置设置为0(类似于String.subString(0,endIndex))

boolean hasArray() 判断是否有底层实现的数组
boolean hasRemaining() 判断当前位置与限制之间是否有元素
Buffer rewind() 重绕缓冲区,将position设为0,并丢弃mark值
```

mark()用法 : 

```java
byte [] bytes = new byte[] {1,2,3};
ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
System.out.println("capacity : "+byteBuffer.capacity());
byteBuffer.position(1);//设置position
byteBuffer.mark();//设置标记
System.out.println("position : "+byteBuffer.position());//1
byteBuffer.position(2);//设置position
byteBuffer.reset();//调用reset()方法时,会将缓冲区的position重置为mark标记的值
System.out.println("position : "+byteBuffer.position());//1
```

## 直接缓冲区

`boolean isDirect()`:判断此缓冲区是否为直接缓冲区

```java
ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
System.out.println("是否直接缓冲区 : "+byteBuffer.isDirect());
```



![image-20200316183934644](D:\data\notes\notes\java\nio\Buffer简介\image-20200316183934644.png)

