```class
 Last modified 2019-11-15; size 862 bytes
  MD5 checksum 55acb827cb8daac33944cb130fbc7d45
  Compiled from "Test.java"
public class com.hbsc.test.Test
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#29         // java/lang/Object."<init>":()V
   #2 = Fieldref           #30.#31        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = Methodref          #5.#32         // com/hbsc/test/Test.test1:(I)I
   #4 = Methodref          #33.#34        // java/io/PrintStream.println:(I)V
   #5 = Class              #35            // com/hbsc/test/Test
   #6 = Class              #36            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Lcom/hbsc/test/Test;
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               args
  #17 = Utf8               [Ljava/lang/String;
  #18 = Utf8               num
  #19 = Utf8               I
  #20 = Utf8               test1
  #21 = Utf8               (I)I
  #22 = Utf8               a
  #23 = Utf8               StackMapTable
  #24 = Class              #37            // java/lang/Throwable
  #25 = Utf8               test2
  #26 = Utf8               b
  #27 = Utf8               SourceFile
  #28 = Utf8               Test.java
  #29 = NameAndType        #7:#8          // "<init>":()V
  #30 = Class              #38            // java/lang/System
  #31 = NameAndType        #39:#40        // out:Ljava/io/PrintStream;
  #32 = NameAndType        #20:#21        // test1:(I)I
  #33 = Class              #41            // java/io/PrintStream
  #34 = NameAndType        #42:#43        // println:(I)V
  #35 = Utf8               com/hbsc/test/Test
  #36 = Utf8               java/lang/Object
  #37 = Utf8               java/lang/Throwable
  #38 = Utf8               java/lang/System
  #39 = Utf8               out
  #40 = Utf8               Ljava/io/PrintStream;
  #41 = Utf8               java/io/PrintStream
  #42 = Utf8               println
  #43 = Utf8               (I)V
{
  public com.hbsc.test.Test();
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
            0       5     0  this   Lcom/hbsc/test/Test;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: bipush        10
         2: istore_1
         3: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         6: iload_1
         7: invokestatic  #3                  // Method test1:(I)I
        10: invokevirtual #4                  // Method java/io/PrintStream.println:(I)V
        13: return
      LineNumberTable:
        line 12: 0
        line 13: 3
        line 15: 13
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      14     0  args   [Ljava/lang/String;
            3      11     1   num   I

  public static int test1(int);
    descriptor: (I)I
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=3, args_size=1
         0: iinc          0, 20
         3: iload_0
         4: istore_1
         5: iinc          0, 30
         8: iload_0
         9: ireturn
        10: astore_2
        11: iinc          0, 30
        14: iload_0
        15: ireturn
      Exception table:
         from    to  target type
             0     5    10   any
      LineNumberTable:
        line 19: 0
        line 20: 3
        line 22: 5
        line 23: 8
        line 22: 10
        line 23: 14
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      16     0     a   I
      StackMapTable: number_of_entries = 1
        frame_type = 74 /* same_locals_1_stack_item */
          stack = [ class java/lang/Throwable ]

  public static int test2(int);
    descriptor: (I)I
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: iinc          0, 20
         3: iload_0
         4: istore_1
         5: iinc          0, 30
         8: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
        11: iload_0
        12: invokevirtual #4                  // Method java/io/PrintStream.println:(I)V
        15: iload_1
        16: ireturn
        17: astore_2
        18: iinc          0, 30
        21: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
        24: iload_0
        25: invokevirtual #4                  // Method java/io/PrintStream.println:(I)V
        28: aload_2
        29: athrow
      Exception table:
         from    to  target type
             0     5    17   any
      LineNumberTable:
        line 29: 0
        line 30: 3
        line 32: 5
        line 33: 8
        line 30: 15
        line 32: 17
        line 33: 21
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      30     0     b   I
      StackMapTable: number_of_entries = 1
        frame_type = 81 /* same_locals_1_stack_item */
          stack = [ class java/lang/Throwable ]
}
SourceFile: "Test.java"

```



```java
public class com.hbsc.test.Demo {
  public com.hbsc.test.Demo();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: return

  public void spin();
    Code:
       0: iconst_0     //表示把 int 类型的 0 值压入操作数栈
       1: istore_1    //表示从操作数栈中弹出一个 int 类型的值,并保存到局部变量中
       2: iload_1    //表示将第一个局部变量的值压入操作数栈
       3: bipush        100 //表示把 int 类型的100压入栈中
       5: if_icmpge     14   //表示将栈中值100弹出并与`i`作比较,如果满足条件就转移到索引14的指令继续执行
       8: iinc          1, 1  //表示对局部变量1(第一个操作数)的值增加1(第二个数)
      11: goto          2
      14: return
}
```



iconst_0 : 表示把 int 类型的 0 值压入操作数栈

bipush 100 : 表示把 int 类型的100压入栈中

if_icmpge 14 :  表示将栈中值弹出并与`i`作比较,如果满足条件就转移到索引14的指令继续执行

istore_1 : 表示从操作数栈中弹出一个 int 类型的值,并保存到局部变量中

iload_1 : 表示将第一个局部变量的值压入操作数栈

iinc          1, 1 : 表示对局部变量1(第一个操作数)的值增加1(第二个数)

## 常量池类型

| 类型                                                | 说明                                                         |      |
| --------------------------------------------------- | ------------------------------------------------------------ | ---- |
| Integer                                             | A 4 byte int constant                                        |      |
| Long                                                | An 8 byte long constant                                      |      |
| Float                                               | A 4 byte float constant                                      |      |
| Double                                              | A 8 byte double constant                                     |      |
| String                                              | 指向常量池的另一个Utf8的string常量                           |      |
| Utf8                                                | utf8编码的字符序列                                           |      |
| Class                                               | 指向另一个包含完整类名的Utf8常量                             |      |
| NameAndType                                         | 以`:`分隔的键值对,左边指向另一个方法或属性的Utf8常量<br />右边指向一个类型的Utf8常量.如果左边是属性,右边是类的完整限定名<br />如果左边是方法,右边是每一个参数的完整限定类名 |      |
| Fieldref, <br />Methodref,<br /> InterfaceMethodref | 以`.`分隔的键值对,左边是`Class`,右边是`NameAndType`          |      |

