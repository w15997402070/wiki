# Hive 安装与配置

## hive安装

1. 下载压缩包 [下载地址](https://mirrors.tuna.tsinghua.edu.cn/apache/hive/)

2. 解压压缩包  

    `tar -zxvf apache-hive-2.3.4-bin.tar.gz`
    
3. 配置环境变量

    `/etc/profile`
    
    ```profile
    export HIVE_HOME=/usr/local/apache-hive-2.3.4-bin
    export PATH=$PATH:$HIVE_HOME/bin
    ```
    
    刷新环境变量
    
    `source /etc/profile`
    
4. 检查是否安装成功  
    `hive --version`

5. hive使用mysql数据库 :

    1. 将mysql的驱动程序包`mysql-connector-java-5.1.47.jar`加入hive的lib目录中

6. 更改配置文件

将`hive-default.properties.template` 复制一份重命名为 `hive-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
   <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>123456</value>
    </property>
   <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://127.0.0.1:3306/hive?useSSL=false</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>
  <property>
       <name>hive.metastore.local</name>
       <value>true</value>
   </property>
</configuration>
```

## 启动HiveServer2,简单的web界面

1. 配置文件`hive-site.xml` 加入

```xml
    <property>
        <name>hive.server2.webui.host</name>
        <value>192.168.184.128</value>
    </property>

    <property>
        <name>hive.server2.webui.port</name>
        <value>10002</value>
    </property>
``

```