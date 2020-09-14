# sqoop 安装与配置

## sqoop下载[下载地址](http://mirrors.tuna.tsinghua.edu.cn/apache/sqoop/1.4.7)

使用sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz这个

## 解压压缩包

    # tar -zxvf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz

## 配置环境变量

    //这里对解压的sqoop文件夹做了重命名 sqoop-1.4.7
    export SQOOP_HOME=/usr/local/sqoop-1.4.7
    export PATH=$PATH:$SQOOP_HOME/bin

## 查看是否配置成功

    # sqoop version
    Warning: /usr/local/sqoop-1.4.7/../hbase does not exist! HBase imports will fail.
    Please set $HBASE_HOME to the root of your HBase installation.
    Warning: /usr/local/sqoop-1.4.7/../hcatalog does not exist! HCatalog jobs will fail.
    Please set $HCAT_HOME to the root of your HCatalog installation.
    Warning: /usr/local/sqoop-1.4.7/../accumulo does not exist! Accumulo imports will fail.
    Please set $ACCUMULO_HOME to the root of your Accumulo installation.
    Warning: /usr/local/sqoop-1.4.7/../zookeeper does not exist! Accumulo imports will fail.
    Please set $ZOOKEEPER_HOME to the root of your Zookeeper installation.
    2019-05-11 21:31:40,411 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
    Sqoop 1.4.7
    git commit id 2328971411f57f0cb683dfb79d19d4d19d185dd8
    Compiled by maugli on Thu Dec 21 15:59:58 STD 2017

## 更改配置文件

进入conf目录

    cp sqoop-env-template.sh sqoop-env.sh

配置变量

    export HADOOP_COMMON_HOME=/usr/local/hadoop-3.1.2
    export HADOOP_MAPRED_HOME=/usr/local/hadoop-3.1.2
    //这有就可以配置,没有就可以不配置
    #export HBASE_HOME=
    //同上
    export HIVE_HOME=/usr/local/apache-hive-2.3.4
    #export ZOOCFGDIR=

从mysql导入数据到hadoop

将mysql的驱动程序包`mysql-connector-java-5.1.47.jar`加入sqoop的lib目录中

导入数据命令

```sh
# sqoop import --connect jdbc:mysql://127.0.0.1:3306/dbname --username root --password 123456 --table log --columns 'id,title,desc' -m 1;
```
