# HIVE基础知识

[toc]

表:

Hive中主要包含四类数据模型：表（Table）、外部表（External Table）、分区（Partition）和桶（Bucket）

* 内部表: 

  * 与数据库中Table在概念上类似
  * 每一个Table在hive中都有一个相应的目录存储数据
  * 所有的Table数据都保存在这个目录中
  * 删除表时,元数据与数据都会被删除

  创建表时如果不指定就会默认存储在hdfs中的`/user/hive/warehouse `目录下

  ```sql
  hive> create table test (tid int,tname string);
  ```

  ![image-20200414142455384](D:\data\notes\notes\大数据\Hive\Hive基础知识\image-20200414142455384.png)

  指定创建表的位置

  ```sql
  hive> create table test2 
      > (sid int, tname string, age int)
      > location '/mytable/hive/test2';
      
  hive> dfs -ls / ;
  ```

  ![image-20200414142820343](D:\data\notes\notes\大数据\Hive\Hive基础知识\image-20200414142820343.png)

* 分区表( partition):

  * partition对应数据库的partition列的密集索引
  * 在hive中,表中的一个partition对应于表下的一个目录,所有的partition的数据都存储在对应的目录中

  ```sql
  --创建分区表,以性别分区
  hive> create table partition_test
      > (sid int, sname string)
      > partitioned by (gender string)
      > row format delimited fields terminated by ',';
  ```

  

* 外部表(External Table)

  * 指向已经在HDFS中存在的数据,可以创建Partition
  * 他和内部表在元数据的组织上是相同的,而实际数据的存储则有较大的差异
  * 外部表只有一个过程,加载数据和创建表同时完成,并不会移动到数据仓库目录中,只是与外部数据建立一个链接.当删除一个外部表时,仅删除该链接

  ```sql
  hive> create external table external_test
      > (sid int, sname string, age int)
      > row format delimited fields terminated by ','
      > location '/input';
  ```

  

* 桶表

  根据哈希值将数据存储在不同的文件中

  ```sql
  -- 根据sname的哈希值存储在5个桶中
  hive> create table bucket_table
      > (sid int, sname string, age int)
      > clustered by (sname) into 5 buckets;
  ```

  

  

* 

视图: 	

`bin/hive -S`静默模式,不打印出调试信息

`hive -e 'select * from text' `直接在linux命令行执行SQL语句

## Hive的SQL语句

```sql
-- 查看表
hive> show tables;
-- 查看表结构
hive> desc 表名;

--创建表
hive> create table test (tid int,tname string);

-- 创建表指定存储位置
hive> create table test2 
    > (sid int, tname string, age int)
    > location '/mytable/hive/test2';

--创建表指定分隔符为","
hive> create table test3
    > (tid int, tname string, age int)
    > row format delimited fields terminated by ',';

--从查询数据创建表
hive> create table test4
    > as
    > select * from test
--添加列    
hive> alter table test add columns(sex string);
-- 查看内置函数
hive> show functions;
```

执行SQL语句

`select * from tablenaame`

```sql
hive> select * from test;
```

执行SQl的脚本

`source SQL文件`



查看dfs文件

```bash
-- 查看根目录
hive> dfs -ls /
```

执行操作系统的命令

`!命令`

```bash
hive> !ls /usr;
```



hive 远程服务

-端口号 10000

- 启动方式: # hive --service hiveserver &



## HIve 数据类型

### 基本数据类型

* tinyint/smallint/int/bigint: 整数类型
* float/double: 浮点数类型
* boolean: 布尔类型
* string: 字符串类型

### 复杂数据类型

* Array : 数组类型,由一系列相同数据类型的元素组成

  ```bash
  hive> create table student 
      > (sid int ,
      > sname string,
      > grade array<float>);
  
  -- 插入数据格式
  {1, tom,[80.5,90.5]}
  ```

* Map:集合类型,包含key -> value ,可以通过key来访问元素

  ```bash
  hive> create table student1
      > (sid int ,
      > sname string,
      > grade map<string,float>);
      
  -- 插入数据格式
  {1, tom, <'语文',90> }
  ```

  ```bash
  hive> create table student1
      > (sid int ,
      > sname string,
      > grades array<map<string,float>>);
      
  --插入数据格式
  {1, tom, [<'语文',90>,<'数学',95>]}
  ```

  

* Struct: 结构类型,可以包含不同数据类型的元素.这些元素可以通过"点语法"的方式来得到所需要的元素

  ```bash
  hive> create table student4
      > (sid int ,
      > info struct<name:string,age:int, sex:string>);
      
  --插入数据类型
  {1, {'tom', 20, '男'}}
  ```

  

* 

### 时间类型

* Date : 
* Timestamp:





```json
<div>城通网盘<br></div><div>15997402070@163.com</div><div>密码 : G4P6PJ19GAnd<br></div><div><br></div><div>吾爱程序网站</div><div>pengpeng</div><div>15997402070@163.com</div><div>AbIugiXcqInh1<br></div><div><br></div><div>谷歌访问助手</div><div>15997402070@163.com</div><div>M8wYwjyW3RF5<br></div><div><br></div><div><br></div><div>gitbook</div><div>15997402070@163.com</div><div>1445656228</div><div><br></div><div>mysql</div><div><div>ALTER USER 'root'@'localhost' IDENTIFIED BY 'My12345678!';</div></div><div><br></div><div><br></div><div>W24ogZlzVhZy<br></div><div><br></div>
```

