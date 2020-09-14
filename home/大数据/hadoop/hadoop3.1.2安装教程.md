# hadoop3.1.2安装

[toc]



## 下载hadoop压缩包

   [官网下载地址](https://hadoop.apache.org/releases.html)
下载hadoop-3.1.2.tar.gz 文件

## 安装好java环境

```shell
# java -version
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) Client VM (build 25.121-b13, mixed mode)
```

## 解压hadoop

将hadoop-3.1.2.tar.gz 上传到/usr/local 目录下
解压压缩包

`tar -zxvf hadoop-3.1.2.tar.gz`

## 配置hadoopPATH

编辑`/etc/profile`

```profile
export HADOOP_HOME=/usr/local/hadoop-3.1.2
export PATH=$PATH:$HADOOP_HOME/bin
```

重新载入配置

```bash
source /etc/profile
```



## 配置 etc/hadoop/hadoop-env.sh文件

```shell
 # set to the root of your Java installation
  export JAVA_HOME=/usr/java/latest
  # 例如 export JAVA_HOME=/usr/java/java-14
```

## 配置ssh免登陆,不然需要一直输入登录密码

```shell
# ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
# cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
# chmod 0600 ~/.ssh/authorized_keys
```

## 配置etc/hadoop/core-site.xml文件

```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

## 配置etc/hadoop/hdfs-site.xml文件

```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```

## 格式化文件系统

```shell
# bin/hdfs namenode -format
```

## 启动hadoop

```shell
# sbin/start-dfs.sh

Starting namenodes on [localhost]
ERROR: Attempting to operate on hdfs namenode as root
ERROR: but there is no HDFS_NAMENODE_USER defined. Aborting operation.
Starting datanodes
ERROR: Attempting to operate on hdfs datanode as root
ERROR: but there is no HDFS_DATANODE_USER defined. Aborting operation.
Starting secondary namenodes [admin]
ERROR: Attempting to operate on hdfs secondarynamenode as root
ERROR: but there is no HDFS_SECONDARYNAMENODE_USER defined. Aborting operation

```

启动报上面的错误处理

在start-dfs.sh 和 stop-dfs.sh文件中加入

```sh
HDFS_DATANODE_USER=root
HADOOP_SECURE_DN_USER=hdfs
HDFS_NAMENODE_USER=root
HDFS_SECONDARYNAMENODE_USER=root
```

## 查看启动是否成功

- 可以用jps命令

```shell
# jps
10563 Jps
10442 SecondaryNameNode
10090 NameNode
10220 DataNode
```

- 通过浏览器查看状态<http://localhost:9870/>

## 配置YARN

### 修改配置文件 etc/hadoop/mapred-site.xml

```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

### 配置etc/hadoop/yarn-site.xml:

```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```

### 启动YARN报错

```shell
# sbin/start-yarn.sh
Starting resourcemanager
ERROR: Attempting to operate on yarn resourcemanager as root
ERROR: but there is no YARN_RESOURCEMANAGER_USER defined. Aborting operation.
Starting nodemanagers
ERROR: Attempting to operate on yarn nodemanager as root
ERROR: but there is no YARN_NODEMANAGER_USER defined. Aborting operation.

```

修改 `sbin/start-yarn.sh` 和 `sbin/stop-yarn.sh` 文件

```sh

HADOOP_SECURE_DN_USER=yarn  --这一行不要也可以
YARN_RESOURCEMANAGER_USER=root
YARN_NODEMANAGER_USER=root
```

### 验证启动是否成功

- 用 `jps` 命令查看

```shell
# jps
11264 NodeManager
11138 ResourceManager
11588 Jps
10442 SecondaryNameNode
10090 NameNode
10220 DataNode
```

- 从浏览器看web页面<http://localhost:8088>

## 相关命令



### 启动dfs

```bash
# 先进入hadoop目录/usr/local/userdata/hadoop-3.2.1
# 启动
sbin/start-dfs.sh
# 停止
sbin/stop-dfs.sh

```

### 启动yarn

```bash
# 先进入hadoop目录/usr/local/userdata/hadoop-3.2.1
# 启动
sbin/start-yarn.sh
# 停止
sbin/stop-yarn.sh
```

