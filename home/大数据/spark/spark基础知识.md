# Spark基础知识

![image-20200419195425913](D:\data\notes\notes\大数据\spark\spark基础知识\image-20200419195425913.png)

## Spark Core

实现了基本功能,包括任务调度,内存管理,错误恢复,与存储系统交互等模块

还包含对弹性分布式数据集(RDD)的API定义

## Spark SQL

操作结构化数据的程序包

## Spark Streaming

提供对实时数据进行流式计算的组件

## MLLib

机器学习功能的程序库

提供了很多机器学习算法,包括分类,回归,聚类,协同过滤等,还提供了模型评估,数据导入等额外的支持功能

## Graphx

操作图的程序库,可以进行并行的图计算



## RDD分布式数据集

RDD Resilient Distributed Dataset 弹性分布式数据集,其实就是分布式元素的集合

创建RDD,转化已有的RDD,调用RDD求值

### 创建RDD

1. 读取外部数据集

   从外部存储中读取

   ```python
   //python方式
   lines = sc.textFile("README.md")
   ```

   ```java
   //java方式
   JavaRDD<String> lines = sparkContext.textFile("README.md");
   ```

   

2. 在驱动程序中对一个集合进行并行化

   ```python
   //pyhton创建RDD
   lines = sc.parallelize(["pandas"," i like pansas"])
   ```

   ```java 
   //java创建RDD
   JavaRDD<String> lines = sparkContext.parallelize(Arrays.asList("pandas", "i like pandas "));
   ```

### RDD操作

1. 转化操作

   转化操作返回一个新的RDD,比如map()和filter()

2. 行动操作

   向驱动器程序返回结果或把结果写入外部系统的操作,会触发实际的计算,比如count()和first()

![image-20200419221826144](D:\data\notes\notes\大数据\spark\spark基础知识\image-20200419221826144.png)

## 命令行运行Python

spark默认带python,无需另外安装

```bash
$ bin/pyspark

# 如果用的是HDFS,在hdfs 的 /user/root/ 目录下要有README.md文件
# 如果没有可以复制一份进去测试
# hdfs dfs -put /usr/local/userdata/spark-3.0/README.md /user/root
>>> lines = sc.textFile("README.md")
>>> lines.count()
109
>>> lines.first()
u'# Apache Spark'
>>> sc
<SparkContext master=local[*] appName=PySparkShell>


```

`sc`s是SparkContext