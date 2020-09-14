# Java虚拟机结构

JVM内存结构 : 

* 方法区(线程共享)
* java堆(线程共享)
* pc寄存器(线程私有)
* java虚拟机栈(线程私有)
* 本地方法栈(线程私有)

![jvm内存结构](D:\data\notes\notes\java\java虚拟机\Java虚拟机结构.assets\image-20191128162731985.png)

## java代码执行顺序

1. java代码编译器编译代码成class文件 (javac)

2. java虚拟机执行引擎执行字节码

   1. 类加载

      加载生命周期 : 加载 -> 验证 -> 准备 -> 解析 -> 初始化 -> 使用 -> 卸载

      1.  **Bootstrap ClassLoader** :  负责加载`$JAVA_HOME中jre/lib/rt.jar`里所有的 class，由 C++ 实现，不是 ClassLoader 子类 
      2.  **Extension ClassLoader** :  负责加载Java平台中扩展功能的一些 jar 包，包括`$JAVA_HOME中jre/lib/*.jar`或`-Djava.ext.dirs`指定目录下的 jar 包。 
      3.  **App ClassLoader** :  负责记载 classpath 中指定的 jar 包及目录中 class。 
      4.  **Custom ClassLoader** : 

   2. 类执行

3. 

