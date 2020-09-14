# 安装配置

[toc]

## 单机配置

### 1. 下载安装包

`kafka_2.13-2.5.0.tgz`

### 2. 解压缩

`tar -zxvf kafka_2.13-2.5.0.tgz`

### 3. 配置环境变量

```profile
export KAFKA_HOME=/usr/local/userdata/kafka
export PATH=$PATH:$KAFKA_HOME/bin

```

### 配置文件

`config/server.properties`

```properties
broker.id=0

log.dirs=/usr/local/userdata/data/kafka-data/kafka-logs

```

### 启动

```bash
$ bin/kafka-server-start.sh -daemon config/server.properties
$ jps
29908 Kafka

```

## 伪分布式配置

在前面的基础上

将`server.properties`文件复制一份并命名为`server-0.properties`，在`server-1.properties`文件中修改配置如下：

```properties
broker.id=1
log.dirs=/usr/local/userdata/data/kafka-data/kafka-logs/borker-1
port=9093
```

默认端口是9092,` server.properties`没有设置端口则采用默认设置，因此在`server-1.properties`将port设置为9093

