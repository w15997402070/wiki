# flume配置

[toc]

## 监控文件夹输出到控制台

配置文件`spool-logger.conf`

```conf
#name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /usr/local/userdata/flumedata/flumespool
a1.sources.r1.fileHeader = true

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

启动服务

```bash
$ bin/flume-ng agent -c ./conf -f ./conf/spool-logger.conf -n a1 -Dflume.root.logger=INFO,console
```

向这个文件夹中添加文件

```bash
$ cp test.txt /usr/local/userdata/flumedata/flumespool
```

控制台就会输出了



## 监控文件夹输出到hdfs

配置文件`spooldir-hdfs.conf`

```conf
#name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
##注意：不能往监控目中重复丢同名文件
## 通过spooldir来监控文件内容的变化
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /usr/local/userdata/flumedata/flumespool
a1.sources.r1.fileHeader = true

# Describe the sink
## 表示下沉到hdfs，下面配置的类型不同，type下面的参数就不同
a1.sinks.k1.type = hdfs
#sinks.k1只能连接一个channel，source可以配置多个
a1.sinks.k1.channel = c1
#下面的配置告诉用hdfs去写文件的时候写到什么位置，下面的表示不是写死的，而是动态变化的。表示输出的目录名称是可变的
a1.sinks.k1.hdfs.path = /flume/events/%y-%m-%d/%H%M/
#表示文件的前缀
a1.sinks.k1.hdfs.filePrefix = events-
#表示到了需要触发的时间时，是否要更新文件夹，true:表示要更新
a1.sinks.k1.hdfs.round = true
##表示每隔1分钟改变一下文件夹
a1.sinks.k1.hdfs.roundValue = 10
##切换文件的时候单位是分钟
a1.sinks.k1.hdfs.roundUnit = minute
##表示只要过了3秒钟，就切换生成一个新的文件
a1.sinks.k1.hdfs.rollInterval = 3
##如果记录的文件大于20字节时切换一次
a1.sinks.k1.hdfs.rollSize = 4096
##当写了5个事件时触发
a1.sinks.k1.hdfs.rollCount = 5
##收到了多少条消息往hdfs中追加内容
a1.sinks.k1.hdfs.batchSize = 1
#使用本地时间戳
a1.sinks.k1.hdfs.useLocalTimeStamp = true
#生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
a1.sinks.k1.hdfs.fileType = DataStream

# Use a channel which buffers events in memory
##使用内存的方式
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

启动

```bash
$ bin/flume-ng agent -c ./conf -f ./conf/spooldir-hdfs.conf -n a1 -Dflume.root.logger=INFO,console

```

复制文件进`/usr/local/userdata/flumedata/flumespool`

可以看到hdfs中产生了文件

```bash
$ hdfs dfs -ls /flume/events/20-04-16/1140
Found 4 items
-rw-r--r--   1 root supergroup        175 2020-04-16 11:49 /flume/events/20-04-16/1140/events-.1587008940462
-rw-r--r--   1 root supergroup        160 2020-04-16 11:49 /flume/events/20-04-16/1140/events-.1587008940463
-rw-r--r--   1 root supergroup        171 2020-04-16 11:49 /flume/events/20-04-16/1140/events-.1587008940464
-rw-r--r--   1 root supergroup        165 2020-04-16 11:49 /flume/events/20-04-16/1140/events-.1587008940465

```





## 相关文章

https://www.cnblogs.com/dengrenning/articles/9508909.html