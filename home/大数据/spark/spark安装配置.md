# Spark安装配置

[toc]

## 伪分布式

1. 首先安装Scala

2. 下载spark安装包

   `spark-3.0.0-preview2-bin-hadoop3.2.tgz`

3. 解压

   `tar -zxvf  spark-3.0.0-preview2-bin-hadoop3.2.tgz`

4. 配置环境变量

   `/etc/profile`

   ```profile
   export SPARK_HOME=/usr/local/userdata/spark-3.0
   export PATH=$PATH:$SPARK_HOME/bin
   ```

5. spark配置

   复制配置文件

   `cp spark-env.sh.template spark-env.sh`

   更改配置文件

   ```sh
   export SCALA_HOME=/usr/local/userdata/scala-2.13.1
   export JAVA_HOME=/usr/java/jdk8
   # 本地安装绑定
   #export SPARK_MASTER_IP=
   #export SPARK_LOCAL_IP=127.0.0.1
   export SPARK_MASTER_PORT=7077
   export SPARK_WORKER_MEMORY=1G
   export SPARK_EXECUTOR_CORES=2
   export HADOOP_HOME=/usr/local/userdata/hadoop-3.2.1
   export HADOOP_CONF_DIR=/usr/local/userdata/hadoop-3.2.1/etc/hadoop
   export SPARK_MASTER_IP=master
   export SPARK_MASTER_PORT=7077
   
   ```

   

6. 更改web页面启动的端口

   `sbin/stop-master.sh `

   更改下面这个地方,大概在63行

   ```sh
   if [ "$SPARK_MASTER_WEBUI_PORT" = "" ]; then
     SPARK_MASTER_WEBUI_PORT=7000
   fi
   
   ```

   

7. 启动spark

   ```bash
   $ sbin/start-all.sh
   ```

   

8. 访问web页面

   `http://localhost:7000/`

   jps也可以看到信息

   ```bash
   $ jps
   3095 Worker
   2985 Master
   ```

   

9. 

   

   

    