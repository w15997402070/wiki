# 问题集

## lib问题

```shell
Java HotSpot(TM) Client VM warning: You have loaded library /usr/local/hadoop-3.1.2/lib/native/libhadoop.so.1.0.0 which might have disabled stack guard. The VM will try to fix the stack guard now.
It's highly recommended that you fix the library with 'execstack -c <libfile>', or link it with '-z noexecstack'.
```

修改$HADOOP_HOME/etc/hadoop路径当中的hadoop-env.sh文件。

加上下面两句话：

export HADOOP_COMMON_LIB_NATIVE_DIR=${HADOOP_HOME}/lib/native

export HADOOP_OPTS="-Djava.library.path=${HADOOP_HOME}/lib"

这里的${HADOOP_HOME}要写安装路径，如：

export HADOOP_COMMON_LIB_NATIVE_DIR=/usr/local/hadoop-2.7.3/lib/native

export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true -Djava.library.path=/usr/local/hadoop-2.7.3/lib"

```shell
2019-04-27 20:16:14,270 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
```

解决方法：

在$HADOOP_HOME/etc/hadoop路径中修改log4j.properties文件，加上下面一句话：

log4j.logger.org.apache.hadoop.util.NativeCodeLoader=ERROR


## 解决运行 Hadoop MapReduce 任务时错误: 找不到或无法加载主类 org.apache.hadoop.mapreduce.v2.app.MRAppMaster

近来在新安装了 Hadoop 后运行 HDFS 的任务没问题，但一运行 MapReduce 任务时就出错，提示“错误: 找不到或无法加载主类 org.apache.hadoop.mapreduce.v2.app.MRAppMaster”。但是在 Hadoop 的 classpath 中明明有该主类所在的包“hadoop-mapreduce-client-app-x.x.x.jar”。查阅了谷歌之后发现还要在 mapred-site.xml 文件中添加 mapreduce 程序所用到的 classpath。在此记录一下，遇到同样问题的朋友可以试一下下面的方法。

编辑 Hadoop 安装目录下 etc/hadoop/mapred-site.xml 文件，在  `<configuration>` 标签和 `</configuration>` 标签之间添加如下配置：

```xml
<property>
  <description>CLASSPATH for MR applications. A comma-separated list
  of CLASSPATH entries. If mapreduce.application.framework is set then this
  must specify the appropriate classpath for that archive, and the name of
  the archive must be present in the classpath.
  If mapreduce.app-submission.cross-platform is false, platform-specific
  environment vairable expansion syntax would be used to construct the default
  CLASSPATH entries.
  For Linux:
  $HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*,
  $HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*.
  For Windows:
  %HADOOP_MAPRED_HOME%/share/hadoop/mapreduce/*,
  %HADOOP_MAPRED_HOME%/share/hadoop/mapreduce/lib/*.

  If mapreduce.app-submission.cross-platform is true, platform-agnostic default
  CLASSPATH for MR applications would be used:
  {{HADOOP_MAPRED_HOME}}/share/hadoop/mapreduce/*,
  {{HADOOP_MAPRED_HOME}}/share/hadoop/mapreduce/lib/*
  Parameter expansion marker will be replaced by NodeManager on container
  launch based on the underlying OS accordingly.
  </description>
   <name>mapreduce.application.classpath</name>
   <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*, $HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
</property>
```

这里说明一下，开头的 `<description>` 和 `</description>` 之间是说明文字，可以删去。`<value>` 和 `</value>`` 之间是属性值，也就是我们的配置值，其中的 “$HADOOP_MAPRED_HOME” 要换成 Hadoop 的安装路径，我这里 Hadoop 安装在：“/usr/local/hadoop”，因此我的属性值就应该是：

    <value>/usr/local/hadoop/share/hadoop/mapreduce/*,/usr/local/hadoop/share/hadoop/mapreduce/lib/*</value>

注意，这一点非常重要，必须填写完整的路径，即必须是绝对路径，不能包含变量。

添加完保存一下重新运行 MapReduce 任务就好了。

## hadoop安全模式

```shell
org.apache.hadoop.ipc.RemoteException: Cannot create directory /info. Name node is in safe mode.
Resources are low on NN. Please add or free up more resources then turn off safe mode manually. NOTE:  If you turn off safe mode before adding resources, the NN will immediately return to safe mode. Use "hdfs dfsadmin -safemode leave" to turn safe mode off. NamenodeHostName:192.168.184.128
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.newSafemodeException(FSNamesystem.java:1407)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.checkNameNodeSafeMode(FSNamesystem.java:1395)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.mkdirs(FSNamesystem.java:3040)
	at org.apache.hadoop.hdfs.server.namenode.NameNodeRpcServer.mkdirs(NameNodeRpcServer.java:1079)
	at org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolServerSideTranslatorPB.mkdirs(ClientNamenodeProtocolServerSideTranslatorPB.java:652)
	at org.apache.hadoop.hdfs.protocol.proto.ClientNamenodeProtocolProtos$ClientNamenodeProtocol$2.callBlockingMethod(ClientNamenodeProtocolProtos.java)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Server$ProtoBufRpcInvoker.call(ProtobufRpcEngine.java:447)
	at org.apache.hadoop.ipc.RPC$Server.call(RPC.java:989)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:850)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:793)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1844)
	at org.apache.hadoop.ipc.Server$Handler.run(Server.java:2489)

	at org.apache.hadoop.ipc.Client.getRpcResponse(Client.java:1489)
	at org.apache.hadoop.ipc.Client.call(Client.java:1435)
	at org.apache.hadoop.ipc.Client.call(Client.java:1345)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Invoker.invoke(ProtobufRpcEngine.java:227)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Invoker.invoke(ProtobufRpcEngine.java:116)
	at com.sun.proxy.$Proxy10.mkdirs(Unknown Source)
	at org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolTranslatorPB.mkdirs(ClientNamenodeProtocolTranslatorPB.java:583)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.io.retry.RetryInvocationHandler.invokeMethod(RetryInvocationHandler.java:409)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invokeMethod(RetryInvocationHandler.java:163)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invoke(RetryInvocationHandler.java:155)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invokeOnce(RetryInvocationHandler.java:95)
	at org.apache.hadoop.io.retry.RetryInvocationHandler.invoke(RetryInvocationHandler.java:346)
	at com.sun.proxy.$Proxy11.mkdirs(Unknown Source)
	at org.apache.hadoop.hdfs.DFSClient.primitiveMkdir(DFSClient.java:2472)
	at org.apache.hadoop.hdfs.DFSClient.mkdirs(DFSClient.java:2447)
	at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1159)
	at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1156)
	at org.apache.hadoop.fs.FileSystemLinkResolver.resolve(FileSystemLinkResolver.java:81)
	at org.apache.hadoop.hdfs.DistributedFileSystem.mkdirsInternal(DistributedFileSystem.java:1156)
	at org.apache.hadoop.hdfs.DistributedFileSystem.mkdirs(DistributedFileSystem.java:1148)
	at org.apache.hadoop.fs.FileSystem.mkdirs(FileSystem.java:1914)
	at com.example.demo.HDFSTest.makeDir(HDFSTest.java:32)
	at com.example.demo.HDFSTest.main(HDFSTest.java:47)
Exception in thread "main" org.apache.hadoop.hdfs.server.
```

解决 :  
解除安全模式

`hdfs dfsadmin -safemode leave`  
_注意_ : 先看一下磁盘的空间占用情况

## hadoop权限问题

```shell
org.apache.hadoop.ipc.RemoteException: Permission denied: user=wang, access=WRITE, inode="/":root:supergroup:drwxr-xr-x
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.check(FSPermissionChecker.java:318)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkPermission(FSPermissionChecker.java:219)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkPermission(FSPermissionChecker.java:189)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.checkPermission(FSDirectory.java:1663)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.checkPermission(FSDirectory.java:1647)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.checkAncestorAccess(FSDirectory.java:1606)
	at org.apache.hadoop.hdfs.server.namenode.FSDirMkdirOp.mkdirs(FSDirMkdirOp.java:60)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.mkdirs(FSNamesystem.java:3041)
	at org.apache.hadoop.hdfs.server.namenode.NameNodeRpcServer.mkdirs(NameNodeRpcServer.java:1079)
	at org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolServerSideTranslatorPB.mkdirs(ClientNamenodeProtocolServerSideTranslatorPB.java:652)
	at org.apache.hadoop.hdfs.protocol.proto.ClientNamenodeProtocolProtos$ClientNamenodeProtocol$2.callBlockingMethod(ClientNamenodeProtocolProtos.java)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Server$ProtoBufRpcInvoker.call(ProtobufRpcEngine.java:447)
	at org.apache.hadoop.ipc.RPC$Server.call(RPC.java:989)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:850)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:793)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1844)
	at org.apache.hadoop.ipc.Server$Handler.run(Server.java:2489)

	at org.apache.hadoop.ipc.Client.getRpcResponse(Client.java:1489)
	at org.apache.hadoop.ipc.Client.call(Client.java:1435)
	at org.apache.hadoop.ipc.Client.call(Client.java:1345)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Invoker.invoke(ProtobufRpcEngine.java:227)
	at org.apache.hadoop.ipc.ProtobufRpcEngine$Invoker.invoke(ProtobufRpcEngine.java:116)
	at com.sun.proxy.$Proxy10.mkdirs(Unknown Source)
	at org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolTranslatorPB.mkdirs(ClientNamenodeProtocolTranslatorPB.java:583)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.io.retry.RetryInvocationHandler.invokeMethod(RetryInvocationHandler.java:409)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invokeMethod(RetryInvocationHandler.java:163)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invoke(RetryInvocationHandler.java:155)
	at org.apache.hadoop.io.retry.RetryInvocationHandler$Call.invokeOnce(RetryInvocationHandler.java:95)
	at org.apache.hadoop.io.retry.RetryInvocationHandler.invoke(RetryInvocationHandler.java:346)
	at com.sun.proxy.$Proxy11.mkdirs(Unknown Source)
	at org.apache.hadoop.hdfs.DFSClient.primitiveMkdir(DFSClient.java:2472)
	at org.apache.hadoop.hdfs.DFSClient.mkdirs(DFSClient.java:2447)
	at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1159)
	at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1156)
	at org.apache.hadoop.fs.FileSystemLinkResolver.resolve(FileSystemLinkResolver.java:81)
	at org.apache.hadoop.hdfs.DistributedFileSystem.mkdirsInternal(DistributedFileSystem.java:1156)
	at org.apache.hadoop.hdfs.DistributedFileSystem.mkdirs(DistributedFileSystem.java:1148)
	at org.apache.hadoop.fs.FileSystem.mkdirs(FileSystem.java:1914)
	at com.example.demo.HDFSTest.makeDir(HDFSTest.java:32)
	at com.example.demo.HDFSTest.main(HDFSTest.java:47)
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

