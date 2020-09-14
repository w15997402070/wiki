//TODO  
javaP命令

![classFile结构](assets/markdown-img-paste-20171226152827147.png)

## ClassFile结构

```java
ClassFile {
       u4 magic;//魔数  CA FE BA BE
       u2 minor_version;//副版本号
       u2 major_version;//主版本号
       u2 constant_pool_count;//常量池
       cp_info constant_pool[constant_pool_count-1];//常量表
       u2 access_flags;//访问标志
       u2 this_class;//类索引
       u2 super_class; //父类索引
       u2 interfaces_count;//接口计数器
       u2 interfaces[interfaces_count];//接口表
       u2 fields_count;//字段计数器
       field_info fields[fields_count];//字段表
       u2 methods_count;//方法计数器
       method_info methods[methods_count];//方法表
       u2 attributes_count;//属性计数器
       attribute_info attributes[attributes_count];//属性表
}
```

*  u1、u2、u4 : 就是分别表示1个字节，2个字节和4个字节。 
*  cp_info :  常量池

## 举例说明 : 

### Demo.java类

创建一个java类`Demo.java`

```java
public class Demo {
    public static void main (String [] args){
        int i = 20;
        String s = "hello";
        System.out.println(i);
        System.out.println(s);
    }
}
```

### class文件信息

使用`javap`查看class文件信息 `javap -v -l -c Demo `

```java
Classfile /com/test/Demo.class
  Last modified 2019-11-22; size 631 bytes
  MD5 checksum 3de857e247d319f5762d58aeb34a8dc0
  Compiled from "Demo.java"
public class com.test.Demo
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #7.#25         // java/lang/Object."<init>":()V
   #2 = String             #26            // hello
   #3 = Fieldref           #27.#28    //java/lang/System.out:Ljava/io/PrintStream;
   #4 = Methodref          #29.#30        // java/io/PrintStream.println:(I)V
   #5 = Methodref          #29.#31        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #6 = Class              #32            // com/hbsc/test/Demo
   #7 = Class              #33            // java/lang/Object
   #8 = Utf8               <init>
   #9 = Utf8               ()V
  #10 = Utf8               Code
  #11 = Utf8               LineNumberTable
  #12 = Utf8               LocalVariableTable
  #13 = Utf8               this
  #14 = Utf8               Lcom/hbsc/test/Demo;
  #15 = Utf8               main
  #16 = Utf8               ([Ljava/lang/String;)V
  #17 = Utf8               args
  #18 = Utf8               [Ljava/lang/String;
  #19 = Utf8               i
  #20 = Utf8               I
  #21 = Utf8               s
  #22 = Utf8               Ljava/lang/String;
  #23 = Utf8               SourceFile
  #24 = Utf8               Demo.java
  #25 = NameAndType        #8:#9          // "<init>":()V
  #26 = Utf8               hello
  #27 = Class              #34            // java/lang/System
  #28 = NameAndType        #35:#36        // out:Ljava/io/PrintStream;
  #29 = Class              #37            // java/io/PrintStream
  #30 = NameAndType        #38:#39        // println:(I)V
  #31 = NameAndType        #38:#40        // println:(Ljava/lang/String;)V
  #32 = Utf8               com/hbsc/test/Demo
  #33 = Utf8               java/lang/Object
  #34 = Utf8               java/lang/System
  #35 = Utf8               out
  #36 = Utf8               Ljava/io/PrintStream;
  #37 = Utf8               java/io/PrintStream
  #38 = Utf8               println
  #39 = Utf8               (I)V
  #40 = Utf8               (Ljava/lang/String;)V
{
  public com.test.Demo();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/hbsc/test/Demo;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: bipush        20
         2: istore_1
         3: ldc           #2                  // String hello
         5: astore_2
         6: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         9: iload_1
        10: invokevirtual #4                  // Method java/io/PrintStream.println:(I)V
        13: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
        16: aload_2
        17: invokevirtual #5                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        20: return
      LineNumberTable:
        line 6: 0
        line 7: 3
        line 8: 6
        line 9: 13
        line 10: 20
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      21     0  args   [Ljava/lang/String;
            3      18     1     i   I
            6      15     2     s   Ljava/lang/String;
}
SourceFile: "Demo.java"

```

### 十六进制信息

如何查看 `.class`文件的十六进制信息 ?

​    可以用 `UltraEdit`打开`Demo.class`文件就可以看十六进制信息

如何将十六进制信息复制出来?

1. 在 `UltraEdit`打开`Demo.class`文件
2. 点击 编辑 -> 十六进制模式(十六进制模式编辑)(右上角) -> 然后 选择全部 -> 点击 十六进制复制选定视图
3. 创建新文件 `demo.txt` 将复制的内容粘贴到`demo.txt`中

十六进制查看如下 :

```txt
 CA FE BA BE 00 00 00 34 00 29 0A 00 07 00 19 08 
 00 1A 09 00 1B 00 1C 0A 00 1D 00 1E 0A 00 1D 00 
 1F 07 00 20 07 00 21 01 00 06 3C 69 6E 69 74 3E 
 01 00 03 28 29 56 01 00 04 43 6F 64 65 01 00 0F 
 4C 69 6E 65 4E 75 6D 62 65 72 54 61 62 6C 65 01 
 00 12 4C 6F 63 61 6C 56 61 72 69 61 62 6C 65 54 
 61 62 6C 65 01 00 04 74 68 69 73 01 00 14 4C 63 
 6F 6D 2F 68 62 73 63 2F 74 65 73 74 2F 44 65 6D 
 6F 3B 01 00 04 6D 61 69 6E 01 00 16 28 5B 4C 6A 
 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 
 29 56 01 00 04 61 72 67 73 01 00 13 5B 4C 6A 61 
 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 01 
 00 01 69 01 00 01 49 01 00 01 73 01 00 12 4C 6A 
 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 
 01 00 0A 53 6F 75 72 63 65 46 69 6C 65 01 00 09 
 44 65 6D 6F 2E 6A 61 76 61 0C 00 08 00 09 01 00 
 05 68 65 6C 6C 6F 07 00 22 0C 00 23 00 24 07 00 
 25 0C 00 26 00 27 0C 00 26 00 28 01 00 12 63 6F 
 6D 2F 68 62 73 63 2F 74 65 73 74 2F 44 65 6D 6F 
 01 00 10 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 
 65 63 74 01 00 10 6A 61 76 61 2F 6C 61 6E 67 2F 
 53 79 73 74 65 6D 01 00 03 6F 75 74 01 00 15 4C 
 6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 72 
 65 61 6D 3B 01 00 13 6A 61 76 61 2F 69 6F 2F 50 
 72 69 6E 74 53 74 72 65 61 6D 01 00 07 70 72 69 
 6E 74 6C 6E 01 00 04 28 49 29 56 01 00 15 28 4C 
 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 
 3B 29 56 00 21 00 06 00 07 00 00 00 00 00 02 00 
 01 00 08 00 09 00 01 00 0A 00 00 00 2F 00 01 00 
 01 00 00 00 05 2A B7 00 01 B1 00 00 00 02 00 0B 
 00 00 00 06 00 01 00 00 00 03 00 0C 00 00 00 0C 
 00 01 00 00 00 05 00 0D 00 0E 00 00 00 09 00 0F 
 00 10 00 01 00 0A 00 00 00 63 00 02 00 03 00 00 
 00 15 10 14 3C 12 02 4D B2 00 03 1B B6 00 04 B2 
 00 03 2C B6 00 05 B1 00 00 00 02 00 0B 00 00 00 
 16 00 05 00 00 00 06 00 03 00 07 00 06 00 08 00 
 0D 00 09 00 14 00 0A 00 0C 00 00 00 20 00 03 00 
 00 00 15 00 11 00 12 00 00 00 03 00 12 00 13 00 
 14 00 01 00 06 00 0F 00 15 00 16 00 02 00 01 00 
 17 00 00 00 02 00 18                            
```

### 十六进制解析 

下面为解析加注释的

```txt
 CA FE BA BE  //u4 魔数
 00 00 //u2 副版本号 0
 00 34 //u2 主版本号 转换为十进制为52 对应jdk1.8
 00 29 // u2 常量池计数器 十进制41
 
 -----------------常量池 start -----------------------
 -- 常量池索引1
 0A    //u1 tag = 10 代表 CONSTANT_Methodref
 00 07 //u2 class_index = 7 对应常量池的索引为7
 00 19 // u2 name_and_type_index = 	25 对应常量池索引为25
 -- 常量池索引2
 08    // u1 tag = 8 代表 CONSTANT_String
 00 1A // u2 string_index = 26 对应常量池索引为 26
 -- 常量池索引3
 09    //u1 tag = 9 代表  CONSTANT_Fieldref
 00 1B // u2 class_index = 27 对应常量池索引为 27
 00 1C // u2 name_and_type_index = 28 对应常量池索引为 28
 -- 常量池索引4
 0A    //u1 tag = 10 代表 CONSTANT_Methodref
 00 1D //u2 class_index = 29 对应常量池的索引为29
 00 1E // u2 name_and_type_index = 	30 对应常量池索引为30
 -- 常量池索引5
 0A    // u1 tag = 10 代表 CONSTANT_Methodref
 00 1D // u2 class_index = 29 对应常量池的索引为29
 00 1F // u2 name_and_type_index = 	31 对应常量池索引为31
 -- 索引6
 07    // u1 tag = 7 代表 CONSTANT_Class
 00 20 // u2 name_index = 32 对应常量池索引 32
 -- 索引7
 07    //  u1 tag = 7 代表 CONSTANT_Class
 00 21 // u2 name_index = 33 对应常量池索引 33
 -- 索引8
 01    // u1 tag = 1 代表 CONSTANT_Utf8
 00 06 //u2 length = 6 代表 bytes[]数组长度为6
 3C 69 6E 69 74 3E // u1 bytes[6] 通过十六进制Hex解码得到 <init>
 -- 索引9
 01    // u1 tag = 1 代表 CONSTANT_Utf8
 00 03 // u2 length = 3 代表 bytes[]数组长度为3
 28 29 56 // u1 bytes[3] 通过十六进制Hex解码得到 ()V
  -- 索引10
 01    // u1 tag = 1 代表 CONSTANT_Utf8
 00 04 // u2 length = 4 代表 bytes[]数组长度为4
 43 6F 64 65 // u1 bytes[4] 通过十六进制Hex解码得到 Code
  -- 索引11
 01   // u1 tag = 1 代表 CONSTANT_Utf8
 00 0F // u2 length = 15 代表 bytes[]数组长度为15
 4C 69 6E 65 4E 75 6D 62 65 72 54 61 62 6C 65 // u1 bytes[15] 通过十六进制Hex解码得到 LineNumberTable
 -- 索引12
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 12 // u2 length = 18 代表 bytes[]数组长度为18
 4C 6F 63 61 6C 56 61 72 69 61 62 6C 65 54 
 61 62 6C 65 // u1 bytes[18] 通过十六进制Hex解码得到 LocalVariableTable
  -- 索引13
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 04 // u2 length = 4 代表 bytes[]数组长度为4
 74 68 69 73 // u1 bytes[4] 通过十六进制Hex解码得到 this
  -- 索引14
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 14 // u2 length = 20 代表 bytes[]数组长度为20
 4C 63 6F 6D 2F 68 62 73 63 2F 74 65 73 74 2F 44
 65 6D 6F 3B // u1 bytes[20] 通过十六进制Hex解码得到 Lcom/hbsc/test/Demo;
  -- 索引15
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 04 // u2 length = 4 代表 bytes[]数组长度为4
 6D 61 69 6E // u1 bytes[4] 通过十六进制Hex解码得到 main
  -- 索引16
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 16 // u2 length = 22 代表 bytes[]数组长度为22
 28 5B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72
 69 6E 67 3B 29 56 // u1 bytes[22] 通过十六进制Hex解码得到 ([Ljava/lang/String;)V
  -- 索引17
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 04 // u2 length = 4 代表 bytes[]数组长度为4
 61 72 67 73 // u1 bytes[4] 通过十六进制Hex解码得到 args
  -- 索引18
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 13 // u2 length = 19 代表 bytes[]数组长度为 19
 5B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 
 69 6E 67 3B // u1 bytes[19] 通过十六进制Hex解码得到 [Ljava/lang/String;
  -- 索引19
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 01 // u2 length = 1 代表 bytes[]数组长度为 1
 69 // u1 bytes[1] 通过十六进制Hex解码得到 i
  -- 索引20
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 01 // u2 length = 1 代表 bytes[]数组长度为 1
 49 // u1 bytes[1] 通过十六进制Hex解码得到 I
  -- 索引21
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 01 // u2 length = 1 代表 bytes[]数组长度为 1
 73 // u1 bytes[1] 通过十六进制Hex解码得到 s
  -- 索引22
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 12 // u2 length = 18 代表 bytes[]数组长度为 18
 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E
 67 3B // u1 bytes[18] 通过十六进制Hex解码得到 Ljava/lang/String;
  -- 索引23
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 0A // u2 length = 10 代表 bytes[]数组长度为 10
 53 6F 75 72 63 65 46 69 6C 65 // u1 bytes[10] 通过十六进制Hex解码得到 SourceFile
  -- 索引24
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 09 // u2 length = 9 代表 bytes[]数组长度为 9
 44 65 6D 6F 2E 6A 61 76 61 // u1 bytes[9] 通过十六进制Hex解码得到 Demo.java
  -- 索引25
 0C // u1 tag = 12 代表 CONSTANT_NameAndType
 00 08// u2 name_index = 8 对应常量池索引 8
 00 09 // u2 descriptor_index = 9 对应常量池索引为9
-- 索引26
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 05 // u2 length = 5 代表 bytes[]数组长度为 5
 68 65 6C 6C 6F  // u1 bytes[5] 通过十六进制Hex解码得到 hello
 -- 索引27
 07 //u1 tag = 7 代表 CONSTANT_Class
 00 22 // u2 name_index = 34 对应常量池索引 34
  -- 索引28
 0C // u1 tag = 12 代表 CONSTANT_NameAndTyp
 00 23 // u2 name_index = 35 对应常量池索引 35
 00 24 // u2 descriptor_index = 36 对应常量池索引为36
  -- 索引29
 07 //u1 tag = 7 代表 CONSTANT_Class
 00 25 // u2 name_index = 37 对应常量池索引 37
  -- 索引30
 0C // u1 tag = 12 代表 CONSTANT_NameAndTyp
 00 26 // u2 name_index = 38 对应常量池索引 38
 00 27 // u2 descriptor_index = 39 对应常量池索引为39
  -- 索引31
 0C // u1 tag = 12 代表 CONSTANT_NameAndTyp
 00 26 // u2 name_index = 38 对应常量池索引 38
 00 28 // u2 descriptor_index = 40 对应常量池索引为40
 -- 索引32
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 12 // u2 length = 18 代表 bytes[]数组长度为 18
 63 6F 6D 2F 68 62 73 63 2F 74 65 73 74 2F 44 65
 6D 6F // u1 bytes[18] 通过十六进制Hex解码得到 com/hbsc/test/Demo
 -- 索引33
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 10 // u2 length = 16 代表 bytes[]数组长度为 16
 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74 
 // u1 bytes[16] 通过十六进制Hex解码得到 java/lang/Object
 -- 索引34
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 10 // u2 length = 16 代表 bytes[]数组长度为 16
 6A 61 76 61 2F 6C 61 6E 67 2F 53 79 73 74 65 6D 
 // u1 bytes[16] 通过十六进制Hex解码得到 java/lang/System
 -- 索引35
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 03 // u2 length = 3 代表 bytes[]数组长度为 3
 6F 75 74 // u1 bytes[3] 通过十六进制Hex解码得到 out
-- 索引36
 01  // u1 tag = 1 代表 CONSTANT_Utf8
 00 15 // u2 length = 21 代表 bytes[]数组长度为 21
 4C 6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 
 72 65 61 6D 3B // u1 bytes[21] 通过十六进制Hex解码得到 Ljava/io/PrintStream;
 -- 索引37
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 13 // u2 length = 19 代表 bytes[]数组长度为 19
 6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 72 
 65 61 6D // u1 bytes[19] 通过十六进制Hex解码得到 java/io/PrintStream
 -- 索引38
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 07 // u2 length = 7 代表 bytes[]数组长度为 7
 70 72 69 6E 74 6C 6E // u1 bytes[7] 通过十六进制Hex解码得到 println
 -- 索引39
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 04 // u2 length = 4 代表 bytes[]数组长度为 4
 28 49 29 56 // u1 bytes[4] 通过十六进制Hex解码得到 (I)V
 -- 索引40
 01 // u1 tag = 1 代表 CONSTANT_Utf8
 00 15 // u2 length = 21 代表 bytes[]数组长度为 21
 28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 
 6E 67 3B 29 56 // u1 bytes[21] 通过十六进制Hex解码得到 (Ljava/lang/String;)V
 -----------------常量池 end -----------------------
 
 00 21 // u2 access_flags  0x0021，代表含有ACC_PUBLIC和ACC_SUPER标志，即 public class。
 00 06 // u2 this_class 类索引 指向常量池索引6
 00 07 // u2 super_class 父类索引 指向常量池索引7
 00 00 // u2 interfaces_count 接口计数器 0 // u2 interfaces[interfaces_count] 接口计数器 0
 00 00 // u2 fields_count (字段计数器) 0 
 00 02   // u2 method_count(方法计数器) 2
 
 ---- methods[] ---- 
 ---- method_info 1 ---- 
 00 01 // u2 access_flags 0001 = public
 00 08 // u2 name_index 对应常量池索引8 <init>
 00 09 // u2 descriptor_index 对应常量池索引9  ()V
 00 01 // u2 attributes_count 1
 ....... attributes[1] attribute_info .....
 00 0A // u2 attribute_name_index 对应常量池索引10  Code
 00 00 00 2F //u4 attribute_length 2F = 47 
 00 01 // u2 max_stack 1 栈的最大深度
 00 01 // u2 max_locals 1 局部变量个数
 00 00 00 05 // u4 code_length 5 code[] 数组的字节数
 2A //0 2A = aload_0      (oracle官网对照) aload_0 = 42 (0x2a)
 B7 //1  B7 = invokespecial             invokespecial = 183 (0xb7)
 00 //   invokespecial 的第一个操作数
 01 //   invokespecial 的第二个操作数 1 指向常量池索引1  java/lang/Object.<init>
 B1 //4  B1 = return                     return = 177 (0xb1)
 00 00 //u2 exception_table_legth 0
 00 02 // attributes_count = 2
         ----- attribute_info[1](第一个属性) LineNumberTable --------
 00 0B // u2 attribute_name_index 对应常量池11 LineNumberTable
 00 00 00 06 // u4 attribute_length = 6
 00 01 // u2 line_number_table_length = 1 
 00 00 //u2 start_pc = 0
 00 03 //u2 line_number  = 3 
        ----- attribute_info[2](第二个属性) LocalVariableTable --------
 00 0C // u2 attribute_name_index 对应常量池12 LocalVariableTable
 00 00 00 0C // u4 attribute_length 12
 00 01 //u2 local_varibute_table_length 1
 00 00 // u2 start_pc 0  aload_0
 00 05 // u2 length 5 
 00 0D //u2 name_index 13 对应常量池13 this
 00 0E // u2 descriptor_index 对应常量池14 Lcom/hbsc/test/Demo;
 00 00 // u2 index 0
  ---- method_info 1 end ---- 
  
 ---- method_info 2 start ---- 
 00 09 //u2 access_flags 0009 = (0001+0008) public + static
 00 0F // u2 name_index = 15 对应常量池索引15 main
 00 10 // u2 descriptor_index 对应常量池索引10  Code
 00 01 u2 attributes_count 1
 ....... attributes[1] attribute_info .....
 00 0A // u2 attribute_name_index 对应常量池索引10  Code
 00 00 00 63 // u2 attribute_length 99
 00 02 // u2 max_stack 2 
 00 03 //u2 max_lcoals 3
 00 00 00 15 // u4 code_length 21
 10 // 0  bipush = 16 (0x10) 
 14 //  bipush的操作数 20 
 3C // 2   istore_1 = 60 (0x3c)
 12 // 3   ldc = 18 (0x12)
 02 //  对应常量池索引2 hello
 4D // 5   astore_2 = 77 (0x4d)
 B2 // 6   getstatic = 178 (0xb2)   getstatic  indexbyte1 indexbyte2
 00 //     indexbyte1
 03 //     indexbyte2 指向常量池索引3 java/lang/System.out
 1B // 9   iload_1 = 27 (0x1b)
 B6 // 10  invokevirtual = 182 (0xb6)  invokevirtual indexbyte1 indexbyte2
 00 //     indexbyte1 0
 04 //     indexbyte2 指向常量池索引4 java/io/PrintStream.println
 B2 // 13  getstatic = 178 (0xb2)   getstatic  indexbyte1 indexbyte2
 00 //     indexbyte1
 03 //     indexbyte2 指向常量池索引3 java/lang/System.out
 2C // 16  aload_2 = 44 (0x2c)
 B6 // 17  invokevirtual = 182 (0xb6)  invokevirtual indexbyte1 indexbyte2
 00 //     indexbyte1
 05 //     indexbyte2 指向常量池索引5 java/io/PrintStream.println
 B1 // 20  return = 177 (0xb1)
 
 00 00 // u2 exception_table_length 0
 00 02 // u2 attributes_counts 2
  ----- attribute_info[1](第一个属性) LineNumberTable --------
 00 0B // u2 attribute_name_index 对应常量池11 LineNumberTable
 00 00 00 16 // u4 attribute_length 22
 00 05 //u2 line_number_table_length 5
   ------  line_number_table[0]
 00 00 // u2 start_pc 0
 00 06 // u2 line_number 6
    ------ line_number_table[1]
 00 03 // u2 start_pc 3
 00 07 // u2 line_number 7
     ------ line_number_table[2]
 00 06 // u2 start_pc 6
 00 08 // u2 line_number 8
      ------ line_number_table[3]
 00 0D // u2 start_pc 13
 00 09 // u2 line_number 9
      ------ line_number_table[4]
 00 14 // u2 start_pc 20
 00 0A // u2 line_number 10
 ----- attribute_info[2](第二个属性) LocalVariableTable --------
 00 0C // u2 attribute_name_index  对应常量池12 LocalVariableTable
 00 00 00 20 // u4 attribute_length 32
 00 03 // u2 local_variable_table_length 3
 ----  local_variable_table[0]
 00 00 // u2 start_pc 0
 00 15 // u2 length 21 
 00 11 // u2 name_index 17 指向常量池索引17 args
 00 12 // u2 descriptor_index 18
 00 00 // u2 index 
 ----  local_variable_table[1]
 00 03 // u2 start_pc 3
 00 12 // u2 length 18
 00 13 // u2 name_index 19 指向常量池索引19 i
 00 14 // u2 descriptor_index 20
 00 01 // u2 index 1
  ----  local_variable_table[2]
 00 06 // u2 start_pc 6
 00 0F // u2 length 15
 00 15 // u2 name_index 21 指向常量池索引21 s
 00 16 // u2 descriptor_index 22
 00 02 // u2 index 2
  ---- method_info 2 end ---- 
 00 01 // u2 attributes_count 1
 00 17 // u2 attribute_name_index 指向常量池索引23  SourceFile
 00 00 00 02 // u4 attribute_length 2
 00 18 // u2 sourcefile_index 24 指向常量池索引24   Demo.java                                             
```

## 1. 魔数(Magic)

`Magic` 的唯一作用是确定这个文件是否为一个能被虚拟机所接受的class文件。魔数值固定为`0xCAFEBABE`，不会改变。

## 2. 副版本(minor version)

## 3. 主版本(major version)

## 4. 常量计数器(constant_pool_count)

`constant_pool_count`的值等于常量池表中的成员数加1。常量池表的索引值只有在大于0且小于constant_pool_count时才会认为是有效的，对于long和double类型有例外情况.

## 5.常量池(constant_pool [])

常量池所有项都有通用格式 : 

```java
cp_info{
    u1 tag
    u1 info[]
}
```

`tag`决定`info[]`的值,每个`tag`字节后必须有两个或更多的字节,这些字节用于指定这个常量的信息,信息的格式由`tag`的值决定.有效的值和对应的表如下 : 

| tag值 | 常量类型                    |
| ----- | --------------------------- |
| 7     | CONSTANT_Class              |
| 9     | CONSTANT_Fieldref           |
| 10    | CONSTANT_Methodref          |
| 11    | CONSTANT_InterfaceMethodref |
| 8     | CONSTANT_String             |
| 3     | CONSTANT_Integer            |
| 4     | CONSTANT_Float              |
| 5     | CONSTANT_Long               |
| 6     | CONSTANT_Double             |
| 12    | CONSTANT_NameAndType        |
| 1     | CONSTANT_Utf8               |
| 15    | CONSTANT_MethodHandle       |
| 16    | CONSTANT_MethodType         |
| 18    | CONSTANT_InvokeDynamic      |

### 1. CONSTANT_Class_info 结构

用于表示类或接口 ,格式如下 : 

```java
CONSTANT_Class_info{
    u1 tag ;  //CONSTANT_Class(7)
    u2 name_index //
}
```

* tag : 值为 CONSTANT_Class(7)
* name_index : 对常量池表的一个有效索引

### 2.  CONSTANT_Methodref_info 结构

方法的结构格式 : 

```java
CONSTANT_Methodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
```

* `tag` : `tag`的值为`CONSTANT_Methodref(10)`
* `class_index` : `class_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_Class_info`结构表示一个类或接口,当前方法是这个类或接口的成员,方法的这个 `class_index`必须是类(而不能是接口)
* `name_and_type_index` : `name_and_type_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_NameAndType_info`结构,表示当前方法的名字和方法描述符.如果一个`CONSTANT_Methodref_info`结构的方法名以`"<('\u003c')"`开头,那么方法名必须是特殊的`<init>`,即这个方法是实例初始化方法,它的返回类型必须为void.

### 3. CONSTANT_String_info 结构

String 类型的常量对象,格式如下 : 

```java
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
```

* `tag` : `tag` 项的值为 `CONSTANT_String(8)`
* `string_index` : `string_index` 项的值必须是对常量池表的有效索引,常量池表在该索引处的成员必须是 `CONSTANT_Utf8_info` 结构,此结构表示 Unicode 码点序列,这个序列最终会被初始化为一个String 对象

### 4. CONSTANT_Fieldref_info 结构

字段的结构如下 : 

```java
CONSTANT_Fieldref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
```

* `tag` : `tag`的值为`CONSTANT_Fieldref(9)`
* `class_index` : `class_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_Class_info`结构表示一个类或接口,当前字段是这个类或接口的成员,`class_index`项既可以是类也可以是接口(对比`CONSTANT_Methodref_info ,CONSTANT_InterfaceMethodref_info`的`class_index`)
* `name_and_type_index` : `name_and_type_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_NameAndType_info`结构,表示当前方法的名字和方法描述符.如果一个CONSTANT_Fieldref_info结构中,给定的描述符必须是字段描述符

### 5. CONSTANT_InterfaceMethodref_info 结构

接口结构如下 : 

```java
CONSTANT_InterfaceMethodref_info{
  u1 tag;
  u2 class_index; 
  u2 name_and_type_index;
}
```

* `tag` : `tag`的值为`CONSTANT_InterfaceMethodref(11)`
* `class_index` : `class_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_Class_info`结构表示一个类或接口,当前字段是这个类或接口的成员,`class_index`项必须是接口(对比`CONSTANT_Methodref_info ,CONSTANT_Fieldref_info `的`class_index`)
* `name_and_type_index` : `name_and_type_index`的值必须是对常量池表的有效索引,常量池表在该索引处的项必须是`CONSTANT_NameAndType_info`结构,表示当前方法的名字和方法描述符.如果一个`CONSTANT_InterfaceMethodref_info` 结构中,给定的描述符必须是方法描述符

### 6. CONSTANT_Utf8_info 结构

用于表示字符常量的值 结构如下:

```java
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length]
}
```

* `tag` : `tag`的值为`CONSTANT_Utf8(1)`
* `length` : `length` 项的值指明了 `bytes []` 数组的长度(注意不能等同于当前结构所表示的String 对象的长度)
* `bytes []` : `bytes []` 是表示字符串值得 byte 数组,`bytes []` 中每个成员的 byte 值都不会是0,也不在 `0xf0 ~ 0xff`范围内

### 7. CONSTANT_NameAndType_info 结构

用于表示字段或方法,结构如下 : 

```java
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
```

* `tag` : `tag`的值为`CONSTANT_NameAndType_info(12)`
* `name_index` :  `name_index`项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是`CONSTANT_Utf8_info`结构，这个结构要么表示特殊的方法名<init>，要么表示一个有效的字段或方法的非限定名。
* `descriptor_index` : `descriptor_index`项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是`CONSTANT_Utf8_info`（见4.4.7小节）结构，这个结构表示一个有效的字段描述符或方法描述符。

### 8.CONSTANT_Integer_info 和CONSTANT_Float_info结构

表示4字节的(int 和 float)的数值常量,结构如下:

```java
CONSTANT_Integer_info {
    u1 tag;
    u4 bytes;
}
```

```java
CONSTANT_Float_info {
    u1 tag;
    u4 bytes;
}
```

* `tag` : `CONSTANT_Integer_info` 结构的 `tag` 项的值是 `CONSTANT_Integer(3)` 。
  `CONSTANT_Float_info` 结构的 `tag` 项的值是 `CONSTANT_Float(4)`。
* `bytes` : `CONSTANT_Integer_info` 结构的 `bytes` 项表示 `int` 常量的值，该值按照`big-endian`的顺序存储（也就是先存储高位字节）。
  `CONSTANT_Float_info` 结构的 `bytes` 项按照 IEEE754 单精度浮点格式来表示`float`常量的值，该值按照`big-endian`的顺序存储（也就是先存储高位字节）。

### 9.CONSTANT_Long_info 和CONSTANT_Double_info结构

表示8字节(long 和 double )的数值常量,结构如下 : 

```java
CONSTANT_Long_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}	
```

```java
CONSTANT_Double_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
```

在class文件的常量池表中，所有的8字节常量均占两个表成员（项）的空间。如果一个`CONSTANT_Long_info`或`CONSTANT_Double_info`结构的项在常量池表中的索引为n，则常量池表中下一个可用项的索引为n+2，此时常量池表中索引为n+1的项仍然有效但必须视为不可用。

* `tag` :` CONSTANT_Long_info` 结构的 `tag` 项的值是 `CONSTANT_Long(5)` 。
  `CONSTANT_Double_info` 结构的 `tag` 项的值是 `CONSTANT_Double(6)`。

* `high_bytes` 和 `low_bytes` : `CONSTANT_Long_info`结构中的无符号的`high_bytes`和`low_bytes`项，用于共同表示 long 类型的常量;`CONSTANT_Double_info` 结构所表示的值将按照下列方式来决定，

  `high_bytes`和 `low_bytes` 首先被转换成一个`long` 常量bits：

### 10 . CONSTANT_MethodHandle_info 结构

表示方法句柄 结构如下 :

```java
CONSTANT_MethodHandle_info{
    u1 tag;
    u1 reference_kind;
    u2 reference_index;
}
```

* `tag` :  `CONSTANT_MethodHandle_info` 结构的 `tag` 项的值为`CONSTANT_MethodHandle(15)`。
* `reference_kind` :  `reference_kind` 项的值必须在范围1~9（包括1和9）之内，它表示方法句柄的类型（kind）。方法句柄类型决定句柄的字节码行为（bytecode behavior）。
* `reference_index` :  `reference_index`项的值必须是对常量池表的有效索引。该位置上的常量池表项，必须符合下列规则：
  * 如果`reference_kind`项的值为 1（REF_getField）、2（REF_getStatic）、
    3（REF_putField）或 4（REF_putStatic），那么常量池表在`reference_index`索引处的成员必须是`CONSTANT_Fieldref_info`结构，此结构表示某个字段，本方法句柄，正是为这个字段而创建的。
  * 如果`reference_kind`项的值是 5（REF_invokeVirtual）或 8（REF_newInvokeSpecial），那么该索引处的常量池成员必须是`CONSTANT_Methodref_info`结构，此结构表示类中的某个方法或构造器），本方法句柄正是为了这个方法或构造器而创建的。
  * 如果`reference_kind`项的值是 6（REF_invokeStatic）或 7（REF invokeSpecial），且 class 文件的版本号小于52.0，那么该索引处的常量池成员必定是`CONSTANT_Methodref_info`结构，此结构表示类中的某个方法，本方法句柄正是为了这个方法而创建的；如果class文件的版本号大于或等于52.0，那么该索引处的常量池成员必须是`CONSTANT_Methodref_info`结构或`CONSTANT_InterfaceMethodref_info`结构，此结构表示类或接口中的某个方法，本方法句柄正是为了这个方法而创建的。
  * 如果`reference_kind`项的值是 9（REF_invokeInterface），那么常量池表在 `reference_index`索引处的成员必须是`CONSTANT_InterfaceMethodref_info`结构，此结构表示接口中的某个方法，本方法句柄正是为了这个方法而创建的。
  * 如果`reference_kind`项的值是 5（REF_invokeVirtual）、6（REF invokeStatic）、
    7（REF_invokeSpecial）或9（REF_invokeInterface），那么由 `CONSTANT_Methodref_info` 结构或 `CONSTANT_InterfaceMethodref_info` 结构所表示的方法，其名称不能为<init>或<clinit>。
  * 如果`reference_kind`项的值是8（REF_newInvokeSpecial），那么由`CONSTANT_Methodref_info` 结构所表示的方法，其名称必须是<init>。

### 11.CONSTANT_MethodType_info结构

表示方法类型 : 

```java
CONSTANT_MethodType_info{
    u1 tag;
    u2 descriptor_index;
}
```

* `tag` :  `CONSTANT_MethodType_info` 结构的tag项的值为`CONSTANT_MethodType(16)`。
* `descriptor_index` :  `descriptor_index`项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是`CONSTANT_Utf8_info`结构，此结构表示方法的描述符。

### 12. CONSTANT_InvokeDynamic_info结构

`CONSTANT_InvokeDynamic_info`用于表示`invokedynamic`指令所用到的引导方法（bootstrap method）、引导方法所用到的动态调用名称（dynamic invocation name）、参数和返回类型，并可以给引导方法传入一系列称为静态参数（static argument）的常量。

```java
CONSTANT_InvokeDynamic_info {
    u1 tag;
    u2 bootstrap_method_attr_index;
    u2 name_and_type_index;
}
```

* `tag` : `tag` 的值为 `CONSTANT_InvokeDynamic(18)`
* `bootstrap_method_attr_index` :  `bootstrap_method_attr_index`项的值必须是对当前class文件中引导方法表的bootstrap_methods数组的有效索引。
* `name_and_type_index` :  `name_and_type_index`项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是`CONSTANT_NameAndType_info`结构，此结构表示方法名和方法描述符）。

## 6. 访问标志(access_flag)

`access_flags`是一种由标志所构成的掩码，用于表示某个类或者接口的访问权限及属性。每个标志的取值及其含义如表所示。

| 标志名         | 值     | 含义                                              |
| -------------- | ------ | ------------------------------------------------- |
| ACC_PUBLIC     | 0x0001 | 声明为public,可以从包外访问                       |
| ACC_FINAL      | 0x0010 | final,不允许有子类                                |
| ACC_SUPER      | 0x0020 | 当用到invokespecial指令时需要对父类方法做特殊处理 |
| ACC_INTERFACE  | 0x0200 | 接口                                              |
| ACC_ABSTRACT   | 0x0400 | abstract抽象的,不能被实例化                       |
| ACC_SYNTHETIC  | 0x1000 | synthetic,表示该class文件并非由java源代码所生成   |
| ACC_ANNOTATION | 0x2000 | 注解                                              |
| ACC_ENUM       | 0x4000 | 枚举                                              |

 `0x0021`，代表含有`ACC_PUBLIC`和`ACC_SUPER`标志，即 `public class`。 

## 7. 类索引(this_class)

this_class的值必须是对常量池表中某项的一个有效索引值。常量池在这个索引处的成员必须为`CONSTANT_Class_info`类型结构体，该结构体表示这个class文件所定义的类或接口。

## 8. 父类索引(super_class)

对于类来说，`super_class`的值要么是0，要么是对常量池表中某项的一个有效索引值。如果它的值不为0，那么常量池在这个索引处的成员必须为`CONSTANT_Class_info`类型常量，它表示这个class文件所定义的类的直接超类。在当前类的直接超类，以及它所有间接超类的ClassFile结构体中，`access_flags`里面均不能带有`ACC_FINAL`标志。
如果class文件的super class的值为0，那这个class文件只可能用来表示object类，因为它是唯一没有父类的类。
对于接口来说，它的class 文件的 `super_class` 项必须是对常量池表中某项的一个有效索引值。常量池在这个索引处的成员必须为代表 Object 类的`CONSTANT_Class_info`结构

## 9. 接口计数器( interfaces_count )

表示当前类或接口的直接超接口数量

## 10. 接口表(interfaces [] )

`interfaces []` 中每个成员的值必须是对常量池表中某项的有效索引值，它的长度为`interfaces_count`。每个成员 interfaces[i] 必须为`CONSTANT_Class_info`结构，其中`0 <= i < interfaces_count`。在 `interfaces []` 中，各成员所表示的接口顺序和对应的源代码中给定的接口顺序（从左至右）一样，即`interfaces[0]` 对应的是源代码中最左边的接口。

## 11. 字段计数器(fields_count)

`fields_count` 的但表示当前Class文件 `fields` 表的成员个效。`fields` 表中每个成员都是一个`field_info`结构，用于表示该类或接口所声明的类字段或者实例字段。

## 12. 字段表(fields [])

`fields` 表中的每个成员都必须是一个`fields_info`结构的数据项，用于表示当前类或接口中某个字段的完整描述。`fields` 表描述当前类或接口声明的所有字段，但不包括从父类或父接口继承的那些字段。

### 字段

每个字段（field）都由fie1d_info结构所定义。
在同一个class文件中，不会有两个字段同时具有相同的字段名和描述符。
field_info结构格式如下：

```java
field_info{
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

* `access_flags` : `access_flags`项的值是个由标志构成的掩码，用来表示字段的访问权限和基本属性。`access_flags`中的每个标志，开启后的含义如表所示。

  | 标志名        | 值     | 说明                                                     |
  | ------------- | ------ | -------------------------------------------------------- |
  | ACC_PUBLIC    | 0x0001 | public                                                   |
  | ACC_PRIVATE   | 0x0002 | private                                                  |
  | ACC_PROTECTED | 0x0004 | protected                                                |
  | ACC_STATIC    | 0x0008 | static                                                   |
  | ACC_FINAL     | 0x0010 | final,对象构造好之后,就不能直接设置                      |
  | ACC_VOLATILE  | 0x0040 | volatile,被标识得字段无法缓存                            |
  | ACC_TRANSIENT | 0x0080 | transient 被标识得字段不会为持久化的对象管理器写入或读取 |
  | ACC_SYNTHETIC | 0x1000 | 该字段由编译器产生,而没有写入源代码中                    |
  | ACC_ENUM      | 0x4000 | 枚举enum的成员                                           |

* name_index : 值必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是`CONSTANT_Utf8_info`结构，此结构表示一个有效的非限定名，这个名称对应于本字段

* descriptor index : 值必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是`CONSTANT_Utf8_info`结构，此结构表示一个有效的字段的描述符。

* attributes_count : 值表示当前字段的附加属性的数量。

* attributes [] : 属性表（attributes表）中的每个成员，其值必须是`attribute_info`结构。
  一个字段可以关联任意多个属性。

## 13.方法计数器(methods_count)

表示当前class文件 methods 表的成员个数。methods 表中每个成员都是一个`method_info`结构。

## 14.方法表(methods [])

methods表中的每个成员都必须是一个`method_info`结构，用于表示当前类或接口中某个方法的完整描述。如果某个`method_info`结构的`access_flags` 项既没有设置`ACC_NATIVE`标志也没有设置`ACC_ABSTRACT`标志，那么该结构中也应包含实现这个方法所用的Java虚拟机指令。
`method_info`结构可以表示类和接口中定义的所有方法，包括实例方法、类方法、实例初始化方法和类或接口初始化方法。methods表只描述当前类或接口中声明的方法，不包括从父类或父接口继承的方法。

## 15. 属性计数器(attributes_count)

表示当前class文件属性表的成员个数。属性表中每一项都是一个`attribute_info`结构

## 16. 属性表(attributes [])

属性表的每个项的值必须是attribute_info结构

属性（attribute）在class文件格式中的ClassFile（见4.1节）结构、field_
info（见4.5节）结构、method info（见4.6节）结构和Code attribute（见4.7.3小节）结构都有使用。
所有属性的通用格式如下：

```java
attribute info{
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
```

对于任意属性，

* `attribute_name_index`必须是对当前class文件的常量池的有效16位无符号索引。常量池在该索引处的成员必须是`CONSTANT_Utf8_info`结构，用以表示当前属性的名字。
* `attribute_length`项的值给出了跟随其后的信息字节的长度，这个长度不包括`attribute_name_index`和`attribute_length`项的6个字节。

### 一. 对java虚拟机正确解读class文件起关键作用的5个属性

#### 1. ConstantValue

`ConstantValue` 属性是定长属性,位于 `field_info`结构的属性表中,表示一个常量表达式的值.

用法如下 : 

如果该字段为静态字段（即 `field_info` 结构的 `access_flags` 项设置了`ACC_STATIC`标志），则说明这个`field_info`结构所表示的字段，将赋值为它的`ConstantValue`属性所表示的值，这个过程也是该字段所在类或接口初始化阶段的一部分。这个过程发生在调用类或接口的类初始化方法之前。

* 如果`field_info`结构表示的非静态字段包含了`ConstantValue`属性，那么这个属性必须被虚拟机所忽略。

  `ConstantValue`属性格 : 

  ```java
  ConstantValue_attribute{
      u2 attribute_name_index;
      u4 attribute_length;
      u2 constantvalue_index;
  }
  ```

  * `attribute_name_index` : 它的值必须是对常量池的一个有效索引,该索引处的成员必须是 `CONSTANT_Utf8_info`
  * `attribute_length` : 该值固定为2
  * `constantvalue_index` : 该值必须是对常量池的一个有效索引.常量池表在该索引处的成员给出了该属性所表示的常量值

#### 2. Code

`Code` 属性是变长属性,位于 `method_info` 结构的属性表中.

如果方法声明为 `native` 或者 `abstract` 方法,那么 `method_info` 结构的属性不能有 `Code` 属性.其他情况下,`method_info` 必须**有且只能有一个** `Code` 属性.

Code 属性的格式 : 

```java
Code_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[length];
    u2 exception_table_length;
    {
        u2 start_pc;
        u2 end_pc;
        u2 handler_pc;
        u2 catch_type;
    } exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

* attribute_name_index : 该值必须是对常量池表的一个有效索引,该索引处必须是 CONSTANT_Utf8_info 结构,用以表示字符串 "Code";

* attribute_length : 该值表示当前属性的长度,不包括初始的6个字节

* max_stack : 该值表示当前方法的操作数栈在方法执行的任何时间点的最大深度

* max_locals : 该值表示分配在当前方法引用的局部变量表中的局部变量个数,包括调用此方法时用于传递参数的局部变量.long 和 double 类型的局部变量的最大索引是 max_locals-2 其他类型的局部变量的最大索引是 max_locals-1

* code_length : 该值表示当前方法 code [] 数组的字节,值必须大于0

* code [] : 表示实现了当前方法的 java虚拟机代码的实际字节内容.

* exception_table_length : 该值表示 exception_table 表的成员个数

* exception_table [] : 数组的每个成员表示 code [] 数组中的一个异常处理器. exception_table [] 的异常处理器是有意义的(不能随意更改).

  * start_pc 和 end_pc : 

    这两项的值表示了异常处理器在 code [] 中的有效范围.

    start_pc 的值必须是对当前 code [] 中某一个指令操作码的有效索引,

    end_pc 的值要么是对当前 code [] 中某一指令操作码的有效索引,要么等于 code_length的值.start_pc 的值必须必 end_pc 小.

* handler_pc :  该值表示一个异常处理器的起点。handler_pc 的值必须同时是对当前 code [] 和其中某一指令操作码的有效索引。

* catch_type : 如果 catch_type 项的值不为0，那么它必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是 CONSTANT_Class_info 结构，用以表示当前异常处理器需要捕捉的异常类型。只有当抛出的异常是指定的类或其子类的实例时，才会调用异常处理器。

  如果 catch_type 项的值为0，那么将会在所有异常抛出时都调用这个异常处理器。
  这可以用于实现 finally 语句。

* attributes_count : attributes_count 项的值给出了 Code 属性中 attributes [] 数组的成员个数。

* attributes [] : 属性表（attributes表）中的每个值都必须是 attribute_info 结构体。
  Code 属性可以关联任意多个属性。

#### 3. StackMapTable

StackMapTable 属性是变长属性，位于Code属性的属性表中。这个属性用在虚拟机的类型检查验证阶段）。
       Code 属性的属性表（attributes表）里面最多可以包含1个StackMapTable属性。

StackMapTable 属性结构 : 

```java
StackMapTable_attribute{
    u2 attribute_name_index;
    u4 attribute_length; 
    u2 number_of_entries; 
    stack_map_frame entries [number_of_entries]
};
```

* attribute_name_index :  attribute_name_index项的值必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是CONSTANT_Utf8_info（见4.4.7小节）结构，用以表示字符串“StackMapTable”。
* attribute_length : attribute_1ength项的值表示当前属性的长度，不包括开头的6个字节。
* number_of_entries : number_of_entries项的值给出了entries表中的成员数量。entries表中每个成员都是一个stack_map_frame结构。
* entries [] : 
  entries表中的每一项都表示本方法的一个栈映射帧（stack map frame）。entries表中各栈映射帧之间的顺序是很重要的。

#### 4. Exceptions

Exceptions 属性是变长属性，位于method_info 结构的属性表中。
        Exceptions 属性指出了一个方法可能抛出的受检异常（checked exception）。
一个method_info 结构的属性表中最多只能有一个Exceptions属性。

Exceptions属性的格式如下：

```java
Exceptions attribute{
    u2 attribute_name_index；
    u4 attribute_length；
    u2 number_of_exceptions；
    u2 exception_index_table[number_of_exceptions]；
}
```

* attribute_name_index :  attribute_name_index项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是CONSTANT_Utf8_info结构，用以表示字符串“Exceptions”。
* attribute length attribute_1ength项的值给出了当前属性的长度，不包括初始的6个字节。
* number_of_exceptions : number_of_exceptions项的值给出了exception_index_table []中成员的数量。
* exception_index_table [] : 
  exception_index_table 数组的每个成员的值都必须是对常量池表的一个有效索引。常量池表在这些索引处的成员都必须是CONSTANT_Class_info结构，表示这个方法声明要抛出的异常所属的类的类型。

#### 5. BootstrapMethods

BootstrapMethods 属性是变长属性，位于 ClassFile 结构的属性表中。它用于保存由 invokedynamic 指令引用的引导方法限定符。
如果某个ClassFile结构的常量池表中有至少一个CONSTANT_InvokeDynamic_info成员，那么这个ClassFile结构的属性表就必须包含，且只能包含一个BootstrapMethods属性。
ClassFile 结构的属性表中最多只能有一个BootstrapMethods属性。

BootstrapMethods属性的格式如下：

```java
BootstrapMethods attribute{
    u2 attribute_name_index；
    u4 attribute_length；
    u2 num_bootstrap_methods；
    { 
        u2 bootstrap_method_ref；
        u2 num_bootstrap_arguments；
        u2 bootstrap_arguments[num_bootstrap_arguments]；
     }  bootstrap_methods[num_bootstrap_methods]；
}
```

* attribute_name_index : attribute_name_index项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是 CONSTANT_Utf8_info结构，用以表示字符串“BootstrapMethods”。

* attribute_length : attribute_length 项的值给出了当前属性的长度，不包括初始的6个字,attribute_length项的值由ClassFile结构中invokedynamic指令的数量决定。

* num_bootstrap_methods : num_bootstrap_methods 项的值给出了 bootstrap_methods 数组中的引导方法限定符的数量。

* bootstrap_methods [] : 
  bootstrap_methods [] 数组的每个成员包含一个指向 CONSTANT_MethodHandle info 结构的索引值，该结构指明了一个引导方法，并指明了一个由索引组成的序列（可能是空序列），此序列里的索引指向该引导方法的静态参数（static argument）。

  bootstrap_methods [] 数组每个成员必须包含以下3项：
  bootstrap_method_ref : bootstrap_method_ref 项的值必须是对常量池表的一个有效索引。常量池在该索引处的值必须是一个CONSTANT_MethodHandle_info结构。

* num_bootstrap_arguments : num_bootstrap_arguments 项的值给出了 bootstrap_arguments数组的元素个数。
* bootstrap_arguments  : bootstrap_arguments数组的每个成员必须是对常量池表的一个有效索引。常量池表在该索引处必须是下列结构之一：CONSTANT_String_info、CONSTANT_Class info）、CONSTANT_Integer_info、CONSTANT_Long_info、CONSTANT_Float_info，CONSTANT_Double_info、CONSTANT_MethodHandle_info或CONSTANT_MethodType_info。

### 二. 对Java SE 平台的类库正确就读class文件起关键作用的12个属性 : 

#### 1.InnerClass

InnerClasses 属性是变长属性，位于 ClassFile 结构的属性表中。

InnerClasses属性格式 : 

```java
InnerClasses_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {
        u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes [number_of_classes]
}
```

* attribute_name_index : attribute_name_index项的值必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，用以表示字符串“InnerClasses”。

* attribute_length : attribute_length 项的值给出了当前属性的长度，不包括初始的6个字节。

* number of_classes : number_of_classes 项的值表示 classes [] 数组的成员数量。

* classes [] : 

  classes [] 数组中每个成员包含以下4项：

  * inner_class_info_index : inner_class info_index项的值必须是对常量池表的一个有效索引。常量池表在该索引处的成员必须是 CONSTANT_Class_info结构，用以表示类或接口C。class数组的另外3项用于描述C的信息。
  * outer_class_info_index : 如果不是类或接口的成员，那么outer_class_info_index项的值为0。
    否则这个项的值必须是对常量池表的一个有效索引，常量池表在该索引处的成员必须是CONSTANT_Class_info 结构，代表一个类或接口。
  * inner_name_index : 如果是匿名类，inner_name_index项的值则必须为0。
    否则这个项的值必须是对常量池表的一个有效索引，常量池表在该索引处的成员必须是CoNSTANT_Utf8_info结构。

* inner_class_access_flags : inner_class_access_flags项的值是一个标志掩码，用于定义访问权和基本属性。

#### 2. EnclosingMethod

当且仅当class为局部类或者匿名类时，才能具有EnclosingMethod属性。

属性格式 : 

```java
EnclosingMethod_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 class_index;
    u2 method_index;
}
```

* attribute_name_index : 对应常量池索引
* attribute_length : 固定值为4
* class_index : 对应一个 CONSTANT_Class_info  结构的常量池索引
* method_index : 如果当前类不是直接包含（enclose）在某个方法或构造器中，那么method_index项的值必须为0。

#### 3. Synthetic

如果一个类成员没有在源文件中出现，则必须标记带有Synthetic属性，或者设置ACC_SYNTHETIC标志。

Synthetic属性的格式如下：

```java
Synthetic_attribute{
       u2 attribute_name_index；
       u4 attribute_length
}
```

* attribute_name_index :  attribute_name_index项的值必须是对常量池表的一个有效索引，常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，用以表示字符串“Synthetic”。
* attribute_length : 值固定为0。

#### 4. Signature

在Java语言中，任何类、接口、构造器方法或字段的声明如果包含了类型变量（type variable）或参数化类型（parameterized type），则Signature属性会为它记录泛型签名信息。

Signature 属性结构 : 

```java
Signature_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 signature_index;
}
```

* attribute_name_index : attribute_name_index 项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是CONSTANT_Utf8_info结构，用以表示字符串“Signature”。
* attribute_length : signature_attribute结构的attribute_length项的值必须为2。
* signature index signature_index项的值必须是对常量池表的一个有效索引。常量池表在该索引必须是CONSTANT_Utf8_info结构用以表示类签名,方法类型签名或字段类型签名.

#### 5. RuntimeVisibleAnnotations

RuntimeVisibleAnnotations 属性记录了添加在类声明、字段声明或方法声明上面，且于运行时可见的注解。Java虚拟机必须令这些注解可供取用，以便使某些合适的反射API能够把它们返回给调用者

RuntimeVisibleAnnotations属性的格式 : 

```java
RuntimeVisibleAnnotations_attribute{
    u2 attribute_name_index; 
    u4 attribute_length; 
    u2 num_annotations; 
    annotation annotations [num_annotations];
}
```

* attribute_name_index :  attribute_name_index项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是 CONSTANT_Utf8_info 结构，用以表示字符串“RuntimeVisibleAnnotations”。

* attribute_length : attribute_length 项的值给出了当前属性的长度，不包括初始的6个字节。
  attribute_length项的值由当前结构的运行时可见注解的数量和值决定。

* num_annotations : num_annotations项的值给出了当前结构所表示的运行时可见注解的数量。

* annotations [] : 
  annotations数组的每个成员，都表示一条添加在声明上面的运行时可见注解。
  annotation结构的格式如下：

  ```java
  annotation{
      u2 type_index; 
      u2 num_element_value_pairs;
      {
          u2  element_name_index; 
          element_value value;
      }   element_value _pairs [num_element_value_pairs];
  }
  ```

  * type_index : type_index项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是CONSTANT_Utf8_info结构，用来表示一个字段描述符，这个字段描述符表示一个注解类型，它和当前 annotation 结构所表示的注解一致。

  * num_element_value_pairs : 该项的值给出了当前 annotation结构所表示的注解中的键值对的个数

  * element_value_pairs [] : 数组每个成员的值都对应于当前annotation结构所表示的注解中的一个键值对。element_value_pairs数组的每个成员都包含如下两个项：

    * element_name_index : 该项的值必须是对常量池表的一个有效索引。常量池在该索引处的成员必须是CONSTANT_Utf8_info结构，此结构用来指代 element_value pairs数组成员所表示的键值对中那个键的名字。换句话说，这个CONSTANT_Utf8_info结构用来指代由type_index所表示的那个注解类型中的一个元素（element）名称。
    * value : 该项的值给出了由 element_value_pairs 成员所表示的键值对中的那个element_value值。

    element_value 结构 : 

    ```java
    element_value{
        ul tag; 
        union{
           u2 const_value_index;
           {
               u2 type_name_index;
               u2 const_name_index;
            }  enum_const_value; 
            u2 class_info_index; 
            annotation annotation_value;
            {
                u2 num_values; 
                element_value values [num_values];
            }  array_value;
         } vaiue;
    }
    ```


#### 6. RuntimeInvisibleAnnotations

属性结构 : 

```java
RuntimeInvisibleAnnotations_attribute{
    u2 attribute_name_index; 
    u4 attribute_length; 
    u2 num_annotations; 
    annotation annotations [num_annotations];
}
```

#### 7. RuntimeVisibleParameterAnnotations

属性结构 : 

```java
RuntimeVisibleParameterAnnotations_attribute{
    u2 attribute_name_index;
    u2 attribute_length;
    u1 num_parameters;
    {
        u2 num_annotations;
        annoyayion annotations[num_annotations];
    } parameter_annotations[num_parameters]
}
```

#### 8. RuntimeInvisibleParameterAnnotations

属性结构 : 

```java
RuntimeInvisibleParameterAnnotations_attribute{
    u2 attribute_name_index;
    u2 attribute_length;
    u1 num_parameters;
    {
        u2 num_annotations;
        annoyayion annotations[num_annotations];
    } parameter_annotations[num_parameters]
}
```

#### 9. RuntimeVisibleTypeAnnotations

属性结构 : 

```java
RuntimeVisibleTypeAnnotations_attribute{
    u2 attribute_name_index;
    u2 attribute_length;
    u2 num_annotations;
    type_annotation annotations[num_annotations]
}
```

#### 10. RuntimeInvisibleTypeAnnotations

属性结构 : 

```java
RuntimeInvisibleTypeAnnotations_attribute{
    u2 attribute_name_index;
    u2 attribute_length;
    u2 num_annotations;
    type_annotation annotations[num_annotations]
}
```

#### 11. AnnotationDefault

属性结构 : 

```java
AnnotationDefault_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    element_value default_value;
}
```

#### 12. MethodParameters

属性结构 : 

```java
MethodParameters_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u1 parameters_count;
    {
        u2 name_index;
        u2 access_flags;
    } parameters[parameters_count];
}
```

### 三. 对Java虚拟机或JavaSE平台类库能够正确解读class文件虽然不起关键作用，但却可以作为实用工具来使用的6个属性：

#### 1. SourceFile

属性结构 : 

```java
SourceFile_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 sourcefile_index;
}
```

#### 2. SourceDebugExtension

属性结构 : 

```java
SourceDebugExtension_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u1 debug_extension[attribute_length];
}
```

#### 3. LineNumberTable

它被调试器用于确定源文件中由给定的行号所表示的内容，对应于Java虚拟机code[]数组中的哪一部分。

LineNumberTable 属性结构 : 

```java
LineNumberTable_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 line_number_table_length;
    {
        u2 start_pc;
        u2 line_number;
    } line_number_table[line_number_table_length]
}
```

* attribute_name_index : 该值对应常量池有效索引
* attribute_length : 当前属性的长度
* line_number_table_length :line_number_table [] 数组长度
* line_number_table [] : 数组的每个成员都表明源文件中的行号会在code数组中的哪一条指令处发生变化
  * start_pc : code [] 数组的一个索引,该索引处的指令码，表示源文件中新的行的起点。
  * line_number : 该值必须与源文件中对应的行号相匹配。

#### 4. LocalVariableTable

调试器在执行方法的过程中可以用它来确定某个局部变量的值。

LocalVariableTable 属性结构 : 

```java
LocalVariableTable_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length;
    {
        u2 start_pc;
        u2 length;
        u2 name_index;
        u2 descriptor_idnex;
        u2 idnex
    } local_variable_table[lcoal_variable_table_length]
}
```

* attribute_name_index : 对应常量池的有效索引
* attribute_length : 该值给出当前属性的长度,不包括初始的6个字节
* local_variable_table_length :  local_variable_table [] 数组的成员数量
* local_variable_table []  : 
  * start_pc 和 length : start_pc的值必须是对当前Code属性的 code []的一个有效索引，code []
    在这个索引处必须是一条指令的操作码。
    start_pc+length要么是当前Code属性的code[]数组的有效索引，且code[]在该索引处必须是一条指令的操作码，要么是刚超过code[]数组末尾的首个索引值。
  * name_index : 常量池的有效索引,表示一个有效的非限定名
  * descriptor_idnex : 常量池的有效索引,表示源程序中局部变量类型的字段描述符
  * index : 表示此局部变量在当前栈帧的局部变量表中的索引

#### 5. LocalVariableTypeTable

结构属性 : 

```java
LocalVariableTypeTable_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_type_table_length;
    {
        u2 start_pc;
        u2 length;
        u2 name_index;
        u2 signature_index;
        u2 index;
    } local_variable_type_table[local_variable_type_table_length];
}
```

#### 6.Deprecated

属性结构 : 

```java
Deprecated_attribute{
    u2 attribute_name_index;
    u4 attribute_length;
}
```

## jclasslib查看字节码

# **[jclasslib](https://github.com/ingokegel/jclasslib)**可以以图形化界面查看字节码

下载地址( https://github.com/ingokegel/jclasslib/releases )

![image-20191126155559776](D:\data\notes\notes\java\java虚拟机\classFile结构.assets\image-20191126155559776.png)

正常选择版本下载安装就可以了.安装之后打开,把`.class`文件拖进去就可以查看信息

![image-20191126155427355](D:\data\notes\notes\java\java虚拟机\classFile结构.assets\image-20191126155427355.png)

点击上面指令可以查看指令信息.

## 指令对照表

```js
var section_executionArr = document.getElementsByClassName('section-execution');
// console.log(section_executionArr);
for(var i= 0;i < section_executionArr.length;i++ ){
   var section_execution = section_executionArr[i];
   var instruction = section_execution.getAttribute('title');
   var sectionArr = section_execution.getElementsByClassName("section");
   for(var j = 0;j < sectionArr;j++){
      var section = sectionArr[j];
      var section_title = section.getAttribute("title");
      if(title === 'Operation'){
         var normArr = section.getElementsByClassName("norm");
         var operation = "";
         for(var k = 0;k < normArr.length;k++){
            var norm = normArr[k];
            operation += norm.innerHTML;
         }
         // console.log(operation);
         instruction += (" | "+operation);
      }
      if(title === 'Format'){
         var emphasisArr = section.getElementsByClassName("emphasis");
         var format = "";
         for(var k = 0;k < emphasisArr.length;k++){
            var emphasis = emphasisArr[k];
            format += emphasis.getElementsByTagName("em").innerHTML;
         }
         // console.log(format);
         instruction += (" | "+format);
      }
      if(title === 'Forms'){
         var forms_normArr = section.getElementsByClassName("norm");
         var forms_operation = "";
         for(var k = 0;k < forms_normArr.length;k++){
            var forms_norm = forms_normArr[k];
            forms_operation += norm.innerHTML;
         }
         // console.log(forms_operation);
         instruction += (" | "+forms_operation);
      }
   }
   console.log(instruction);
}
```

