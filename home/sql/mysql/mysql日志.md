# Mysql文件

[toc]



## 日志文件

### 错误日志文件

错误日志记录MySQL运行过程中较为严重的警告和错误信息,以及每次启动和关闭的详细信息.

默认情况,记录错误日志的功能是关闭的.

#### 查询日志情况

```sql
show variables like 'log_%'; 

```

`log_err[=file_name]`选项控制错误日志

### 二进制日志

binlog也就是二进制日志

`log_bin[=file_name]`控制二进制日志

如果未指定file_name,则会在数据目录下记录为mysql_bin.***(*代表0-9之间的某一个数字,表示日志的序号)

binlog附加参数:

* `--max_binlog_size` : binlog的最大存储上限,达到该上限时,重新创建一个日志开始继续记录
* `--binlog-do-db=db_name`:参数表示某个(db_name)数据库记录binlog
* `--binlog-ignore-db=db_name`显示指定忽略某个(db_name)数据库的binlog

binlog不限制大小,追加写入

binlog是逻辑日志,就是记录一条语句的原始逻辑,binlog 记录的是100+100也就是200的来源逻辑

redo log 重做日志,是物理日志,记录InnoDB存储引擎的事务日志

记录的是结果,例如张三存储200元redo log记录的就是200

MySQL的WAL机制(Write-Ahead Logging),先写日志再写磁盘

redo log有大小,不会无限写入

### 查询日志

查询日记记录Mysql中所有的Query

通过`--log[=file_name]`打开该日志

### 慢查询日志

记录执行时间较长的查询

通过`--log-slow-queries[=file_name]`打开该功能并设置记录位置和文件名,默认文件名为`hostname-slow.log`

```
# my.ini文件

slow_query_log_file=D:\data\slow-query.log           
long_query_time=2
slow_query_log=1
```

### InnoDB的在线REDO日志

InnoDB的事务安全性主要通过在线REDO日志和记录在表空间的UNDO信息来保证的.

REDO日志中记录了InnoDB所做的所有物理变更和事务信息,通过REDO和UNDO信息,InnoDB保证了在任何情况下的事务安全性

## 数据文件

### 1. ".frm"文件

与表相关的元数据(meta)信息都存放在".frm"文件,包括表结构的定义信息

### 2. ".MYD"文件

MyISAM存储引擎专用的,存放表的数据

### 3. ".MYI"文件

也是MyISAM存储引擎专用的,存储索引相关信息

### 4. ".ibd"文件和ibdata文件

这两种文件都是存放InnoDB数据的文件，之所以用两种文件来存放InnoDB的数据（包括索引），是因为lnnoDB的数据存储方式能够通过配置来决定是使用共享表空间存放存储数据，还是用独享表空间存放存储数据。独享表空间存储方式使用“ibd"文件来存放数据，且每个表一个“.ibd"文件，文件存放在和MyISAM数据相同的位置。如果选用共享存储表空间来存放数据，则会使用ibdata文件，所有表共同使用一个（或者多个，可自行配置）ibdata文件。