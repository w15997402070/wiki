# habse启动报错

## 1 问题1：java.lang.NoClassDefFoundError: org/apache/htrace/SamplerBuilder

```shell
java.lang.NoClassDefFoundError: org/apache/htrace/SamplerBuilder
    at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:644)
    at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:628)
    at org.apache.hadoop.hdfs.DistributedFileSystem.initialize(DistributedFileSystem.java:149)
    at org.apache.hadoop.fs.FileSystem.createFileSystem(FileSystem.java:2667)
    at org.apache.hadoop.fs.FileSystem.access$200(FileSystem.java:93)
    at org.apache.hadoop.fs.FileSystem$Cache.getInternal(FileSystem.java:2701)
    at org.apache.hadoop.fs.FileSystem$Cache.get(FileSystem.java:2683)
    at org.apache.hadoop.fs.FileSystem.get(FileSystem.java:372)
    at org.apache.hadoop.fs.Path.getFileSystem(Path.java:295)
    at org.apache.hadoop.hbase.util.CommonFSUtils.getRootDir(CommonFSUtils.java:362)
    at org.apache.hadoop.hbase.util.CommonFSUtils.isValidWALRootDir(CommonFSUtils.java:411)
    at org.apache.hadoop.hbase.util.CommonFSUtils.getWALRootDir(CommonFSUtils.java:387)
    at org.apache.hadoop.hbase.regionserver.HRegionServer.initializeFileSystem(HRegionServer.java:697)
    at org.apache.hadoop.hbase.regionserver.HRegionServer.<init>(HRegionServer.java:606)
    at org.apache.hadoop.hbase.master.HMaster.<init>(HMaster.java:489)
    at org.apache.hadoop.hbase.master.HMasterCommandLine$LocalHMaster.<init>(HMasterCommandLine.java:308)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at org.apache.hadoop.hbase.util.JVMClusterUtil.createMasterThread(JVMClusterUtil.java:132)
    at org.apache.hadoop.hbase.LocalHBaseCluster.addMaster(LocalHBaseCluster.java:227)
    at org.apache.hadoop.hbase.LocalHBaseCluster.<init>(LocalHBaseCluster.java:174)
    at org.apache.hadoop.hbase.master.HMasterCommandLine.startMaster(HMasterCommandLine.java:229)
    at org.apache.hadoop.hbase.master.HMasterCommandLine.run(HMasterCommandLine.java:140)
    at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:70)
    at org.apache.hadoop.hbase.util.ServerCommandLine.doMain(ServerCommandLine.java:149)
    at org.apache.hadoop.hbase.master.HMaster.main(HMaster.java:3103)
Caused by: java.lang.ClassNotFoundException: org.apache.htrace.SamplerBuilder
    at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:338)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
    ... 28 more
2019-01-16 15:06:08,129 ERROR [main] master.HMasterCommandLine: Master exiting
java.lang.RuntimeException: Failed construction of Master: class org.apache.hadoop.hbase.master.HMasterCommandLine$LocalHMasterorg.apache.htrace.SamplerBuilder
    at org.apache.hadoop.hbase.util.JVMClusterUtil.createMasterThread(JVMClusterUtil.java:137)
    at org.apache.hadoop.hbase.LocalHBaseCluster.addMaster(LocalHBaseCluster.java:227)
    at org.apache.hadoop.hbase.LocalHBaseCluster.<init>(LocalHBaseCluster.java:174)
    at org.apache.hadoop.hbase.master.HMasterCommandLine.startMaster(HMasterCommandLine.java:229)
    at org.apache.hadoop.hbase.master.HMasterCommandLine.run(HMasterCommandLine.java:140)
    at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:70)
    at org.apache.hadoop.hbase.util.ServerCommandLine.doMain(ServerCommandLine.java:149)
    at org.apache.hadoop.hbase.master.HMaster.main(HMaster.java:3103)
Caused by: java.lang.NoClassDefFoundError: org/apache/htrace/SamplerBuilder
    at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:644)
    at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:628)
    at org.apache.hadoop.hdfs.DistributedFileSystem.initialize(DistributedFileSystem.java:149)
    at org.apache.hadoop.fs.FileSystem.createFileSystem(FileSystem.java:2667)
    at org.apache.hadoop.fs.FileSystem.access$200(FileSystem.java:93)
    at org.apache.hadoop.fs.FileSystem$Cache.getInternal(FileSystem.java:2701)
    at org.apache.hadoop.fs.FileSystem$Cache.get(FileSystem.java:2683)
    at org.apache.hadoop.fs.FileSystem.get(FileSystem.java:372)
    at org.apache.hadoop.fs.Path.getFileSystem(Path.java:295)
    at org.apache.hadoop.hbase.util.CommonFSUtils.getRootDir(CommonFSUtils.java:362)
    at org.apache.hadoop.hbase.util.CommonFSUtils.isValidWALRootDir(CommonFSUtils.java:411)
    at org.apache.hadoop.hbase.util.CommonFSUtils.getWALRootDir(CommonFSUtils.java:387)
    at org.apache.hadoop.hbase.regionserver.HRegionServer.initializeFileSystem(HRegionServer.java:697)
    at org.apache.hadoop.hbase.regionserver.HRegionServer.<init>(HRegionServer.java:606)
    at org.apache.hadoop.hbase.master.HMaster.<init>(HMaster.java:489)
    at org.apache.hadoop.hbase.master.HMasterCommandLine$LocalHMaster.<init>(HMasterCommandLine.java:308)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at org.apache.hadoop.hbase.util.JVMClusterUtil.createMasterThread(JVMClusterUtil.java:132)
    ... 7 more
Caused by: java.lang.ClassNotFoundException: org.apache.htrace.SamplerBuilder
    at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:338)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
    ... 28 more
```

1.2 解决
默认是`htrace-core-4.1.0-incubating.jar`包,没有这个类.所以从hadoop 的 `share/hadoop/common`下可以找到这个包复制到hbase的lib目录下可以解决。

```shell
[root@shizhi002 hbase-2.1.2]# cp ./lib/client-facing-thirdparty/htrace-core-3.1.0-incubating.jar ./lib/
```

原来的错误解决了，又遇到了新的错误：

## 2 错误2：java.lang.NoClassDefFoundError: Could not initialize class org.apache.hadoop.hbase.io.asyncfs.FanOutOneBlockAsyncDFSOutputHelper

```shell
handler.OpenRegionHandler: Failed open of region=hbase:meta,,1.1588230740
java.lang.NoClassDefFoundError: Could not initialize class org.apache.hadoop.hbase.io.asyncfs.FanOutOneBlockAsyncDFSOutputHelper
    at org.apache.hadoop.hbase.io.asyncfs.AsyncFSOutputHelper.createOutput(AsyncFSOutputHelper.java:51)
    at org.apache.hadoop.hbase.regionserver.wal.AsyncProtobufLogWriter.initOutput(AsyncProtobufLogWriter.java:167)
    at org.apache.hadoop.hbase.regionserver.wal.AbstractProtobufLogWriter.init(AbstractProtobufLogWriter.java:166)
    at org.apache.hadoop.hbase.wal.AsyncFSWALProvider.createAsyncWriter(AsyncFSWALProvider.java:113)
    at org.apache.hadoop.hbase.regionserver.wal.AsyncFSWAL.createWriterInstance(AsyncFSWAL.java:612)
    at org.apache.hadoop.hbase.regionserver.wal.AsyncFSWAL.createWriterInstance(AsyncFSWAL.java:124)
    at org.apache.hadoop.hbase.regionserver.wal.AbstractFSWAL.rollWriter(AbstractFSWAL.java:756)
    at org.apache.hadoop.hbase.regionserver.wal.AbstractFSWAL.rollWriter(AbstractFSWAL.java:486)
    at org.apache.hadoop.hbase.regionserver.wal.AsyncFSWAL.<init>(AsyncFSWAL.java:251)
    at org.apache.hadoop.hbase.wal.AsyncFSWALProvider.createWAL(AsyncFSWALProvider.java:73)
    at org.apache.hadoop.hbase.wal.AsyncFSWALProvider.createWAL(AsyncFSWALProvider.java:48)
    at org.apache.hadoop.hbase.wal.AbstractFSWALProvider.getWAL(AbstractFSWALProvider.java:152)
    at org.apache.hadoop.hbase.wal.AbstractFSWALProvider.getWAL(AbstractFSWALProvider.java:60)
    at org.apache.hadoop.hbase.wal.WALFactory.getWAL(WALFactory.java:284)
    at org.apache.hadoop.hbase.regionserver.HRegionServer.getWAL(HRegionServer.java:2104)
    at org.apache.hadoop.hbase.regionserver.handler.OpenRegionHandler.openRegion(OpenRegionHandler.java:284)
    at org.apache.hadoop.hbase.regionserver.handler.OpenRegionHandler.process(OpenRegionHandler.java:108)
    at org.apache.hadoop.hbase.executor.EventHandler.run(EventHandler.java:104)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    at java.lang.Thread.run(Thread.java:748)
```

    2.2 解决
参考了：

RegionServer因无法初始化对HDFS的访问而中止

HBase 2.0.1 with Hadoop 2.8.4 causes NoSuchMethodException

第2篇文章就提到了：

```xml
<property>
    <name>hbase.wal.provider</name>
    <value>filesystem</value>
</property>
```

Seems to fix it, but would be nice to actually try the fanout wal with
hadoop 2.8.4.
按照说明修改一下，编辑配置文件：

vi conf/hbase-site.xml
加入内容：

```xml
<property>
    <name>hbase.wal.provider</name>
    <value>filesystem</value>
 </property>
```

再次启动log中就不报错了。