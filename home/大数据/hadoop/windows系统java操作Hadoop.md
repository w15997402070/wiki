# windows系统 idea 工具 java访问hadoop

## 创建java项目,添加hadoop依赖

maven依赖

```xml
      <properties>
        <java.version>1.8</java.version>
        <hadoop.version>2.8.5</hadoop.version>
      </properties>
    <dependencies>
       <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-mapreduce-client-core</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
    <dependencies>
```

## windows系统解压hadoop-2.8.5.tar.gz

## 将[winutils](https://github.com/steveloughran/winutils)下选相应的版本的bin目录下的文件复制到hadoop解压出来的bin目录下

## 报错处理

## hadoop权限问题

```shell
org.apache.hadoop.ipc.RemoteException: Permission denied: user=administration, access=WRITE, inode="/":root:supergroup:drwxr-xr-x
```

解决 :

设置名称

```java
System.setProperty("HADOOP_USER_NAME","root");
```

## 找不到hadoop.home.dir

```shell
16:42:22.156 [main] DEBUG org.apache.hadoop.util.Shell - Failed to detect a valid hadoop home directory
java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset.
```

解决 :  
设置访问路径

```java
//就是hadoop的HADOOP_HOME路径,可能是设置hadoop的path没有生效需要这个设置
System.setProperty("hadoop.home.dir", "D:\\user\\hadoop\\");
```

_注意_: 远程的hadoop的配置文件中不能使用localhost或者127.0.0.1,需要换成对应的ip

```xml
   <property>
        <name>fs.defaultFS</name>
        <value>hdfs://127.0.0.1:9000</value>
    </property>
<!--改成下面-->
   <property>
        <name>fs.defaultFS</name>
        <value>hdfs://172.21.0.15:9000</value>
    </property>

```

在java代码运行的本机上使用hostname访问

```hosts
 private final static String HDFS_PATH = "hdfs://VM015centos:9000";
 
 然后在host文件中配置
(公网地址)62.123.56.78 VM015centos
```



## 运行WordCount程序

新建maven项目

​	`pom.xml`中添加hadoop依赖

```xml
<properties>
    <java.version>1.8</java.version>
    <hadoop.version>3.1.2</hadoop.version>
  </properties>
<dependencies>
<dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-common</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-hdfs</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-mapreduce-client-core</artifactId>
      <version>${hadoop.version}</version>
    </dependency>
</dependencies>
```



编写WordCount代码

```java
package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 相当于一个yarn集群的客户端
 * 需要在此封装mr程序的相关运行参数,指定jar包
 * 最后提交给YARN
 */
public class WordCountDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();
//        configuration.set("mapreduce.framework.name","yarn");
//        configuration.set("yarn.resoucemanager.hostname","");
        Job job = Job.getInstance(configuration);

        //设置jar包路径
        job.setJarByClass(WordCountDriver.class);

        //指定本业务job使用的mapper/reducer业务类
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        //指定mapper输出数据的k/v类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //指定最终输出的数据的k/v类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        //将job中配置的相关参数,以及job所用的java类所在的jar包,提交给YARN去执行
//        job.submit();
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);

    }
}
```

```java
package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
 * KEYIN: 默认情况下,是mr框架所读到的一行文本的起始偏移量,Long
 *        hadoop中有自己的序列化接口,用LongWritable
 * VALUEIN: 默认情况下,是mr框架所读到的一行文本的内容,String,hadoop中用text
 *
 * KEYOUT : 是用户自定义逻辑处理完之后输出数据中的key
 * VALUEOUT: 是用户自定义逻辑处理完之后输出数据中的value
 */
public class WordCountMapper extends Mapper<LongWritable, Text,Text, IntWritable> {
    /**
     * map阶段的逻辑
     * map task会对每一行输入数据调用一次自定义的map方法
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();
        String[] worldArr = line.split(" ");
        //将单词输出为<单词,1>
        for (String word : worldArr) {
            //将单词作为key,将次数1作为value
            context.write(new Text(word),new IntWritable(1));
        }

    }
}
```

```java 
package org.example;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
 *
 *     KEYIN, VALUEIN 对应 WordCountMapper 输出的 KEYOUT, VALUEOUT
 *     KEYOUT, VALUEOUT 是自定义reduce逻辑处理结果的输出数据类型
 */
public class WordCountReducer extends Reducer<Text, IntWritable,Text,IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<IntWritable> its = values.iterator();
        int sum = 0;
        while (its.hasNext()){
            sum += its.next().get();
        }
        context.write(new Text(key),new IntWritable(sum));
    }
}

```



运行命令

```bash
[root@master hadoop-3.2.1]# hdfs dfs -mkdir -p /wordcount/input


[root@master hadoop-3.2.1]# hdfs dfs -put NOTICE.txt README.txt LICENSE.txt /wordcount/input


[root@master hadoop-3.2.1]#  hadoop jar data-1.0.jar org.example.WordCountDriver  /wordcount/input /wordcount/output

[root@master hadoop-3.2.1]# hdfs dfs -ls /wordcount/output
[root@master hadoop-3.2.1]# hdfs dfs -cat /wordcount/output/part-r-00000

```

