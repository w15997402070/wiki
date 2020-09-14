# Hive问题

## java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkArgument

```bash
Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkArgument(ZLjava/lang/String;Ljava/lang/Object;)V
        at org.apache.hadoop.conf.Configuration.set(Configuration.java:1357)
        at org.apache.hadoop.conf.Configuration.set(Configuration.java:1338)
        at org.apache.hadoop.mapred.JobConf.setJar(JobConf.java:536)
        at org.apache.hadoop.mapred.JobConf.setJarByClass(JobConf.java:554)
        at org.apache.hadoop.mapred.JobConf.<init>(JobConf.java:448)
        at org.apache.hadoop.hive.conf.HiveConf.initialize(HiveConf.java:4045)
        at org.apache.hadoop.hive.conf.HiveConf.<init>(HiveConf.java:4003)
        at org.apache.hadoop.hive.common.LogUtils.initHiveLog4jCommon(LogUtils.java:81)
        at org.apache.hadoop.hive.common.LogUtils.initHiveLog4j(LogUtils.java:65)
        at org.apache.hadoop.hive.cli.CliDriver.run(CliDriver.java:702)
        at org.apache.hadoop.hive.cli.CliDriver.main(CliDriver.java:686)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.apache.hadoop.util.RunJar.run(RunJar.java:323)
        at org.apache.hadoop.util.RunJar.main(RunJar.java:236)
```

查看 hadoop 中guava包`hadoop-3.2.1/share/hadoop/common/lib`和hive lib中guava包哪个版本高

`/hive/lib`

将高版本复制到低版本那里删除低版本的

`hadoop-3.2.1`中是`guava-27.0-jre.jar`,将这个替换hive中的guava包



```java
Exception in thread "main" java.lang.IllegalArgumentException: java.net.URISyntaxException: Relative path in absolute URI: ${system:java.io.tmpdir%7D/$%7Bsystem:user.name%7D
        at org.apache.hadoop.fs.Path.initialize(Path.java:263)
        at org.apache.hadoop.fs.Path.<init>(Path.java:221)
        at org.apache.hadoop.hive.ql.session.SessionState.createSessionDirs(SessionState.java:659)
        at org.apache.hadoop.hive.ql.session.SessionState.start(SessionState.java:582)
        at org.apache.hadoop.hive.ql.session.SessionState.beginStart(SessionState.java:549)
        at org.apache.hadoop.hive.cli.CliDriver.run(CliDriver.java:750)
        at org.apache.hadoop.hive.cli.CliDriver.main(CliDriver.java:686)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.apache.hadoop.util.RunJar.run(RunJar.java:323)
        at org.apache.hadoop.util.RunJar.main(RunJar.java:236)
Caused by: java.net.URISyntaxException: Relative path in absolute URI: ${system:java.io.tmpdir%7D/$%7Bsystem:user.name%7D
        at java.net.URI.checkPath(URI.java:1823)
        at java.net.URI.<init>(URI.java:745)
        at org.apache.hadoop.fs.Path.initialize(Path.java:260)
        ... 12 more
```

在 hive-site.xml 中添加

```xml
  <property>
    <name>system:java.io.tmpdir</name>
    <value>/tmp/hive/java</value>
  </property>
  <property>
    <name>system:user.name</name>
    <value>${user.name}</value>
  </property>
```





```xml
FAILED: SemanticException org.apache.hadoop.hive.ql.metadata.HiveException: java.lang.RuntimeException: Unable to instantiate org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient
```

```rust
(1)删除HDFS上的hive数据与hive数据库
        hadoop fs -rm -r -f /tmp/hive
        hadoop fs -rm -r -f /user/hive
  (2)删除MySQL上的hive的元数据信息
        mysql -uroot -p 
        drop database hive

初始化hive, 将mysql作为hive的元数据库
  schematool -dbType mysql -initSchema 
```



```java
hive> show databases;
Exception in thread "main" java.lang.IllegalAccessError: tried to access method com.google.common.collect.Iterators.emptyIterator()Lcom/google/common/collect/UnmodifiableIterator; from class org.apache.hadoop.hive.ql.exec.FetchOperator
        at org.apache.hadoop.hive.ql.exec.FetchOperator.<init>(FetchOperator.java:108)
        at org.apache.hadoop.hive.ql.exec.FetchTask.initialize(FetchTask.java:87)
        at org.apache.hadoop.hive.ql.Driver.compile(Driver.java:541)
        at org.apache.hadoop.hive.ql.Driver.compileInternal(Driver.java:1317)
        at org.apache.hadoop.hive.ql.Driver.runInternal(Driver.java:1457)
        at org.apache.hadoop.hive.ql.Driver.run(Driver.java:1237)
        at org.apache.hadoop.hive.ql.Driver.run(Driver.java:1227)
        at org.apache.hadoop.hive.cli.CliDriver.processLocalCmd(CliDriver.java:233)
        at org.apache.hadoop.hive.cli.CliDriver.processCmd(CliDriver.java:184)
        at org.apache.hadoop.hive.cli.CliDriver.processLine(CliDriver.java:403)
        at org.apache.hadoop.hive.cli.CliDriver.executeDriver(CliDriver.java:821)
        at org.apache.hadoop.hive.cli.CliDriver.run(CliDriver.java:759)
        at org.apache.hadoop.hive.cli.CliDriver.main(CliDriver.java:686)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.apache.hadoop.util.RunJar.run(RunJar.java:323)
        at org.apache.hadoop.util.RunJar.main(RunJar.java:236)
```

升级到3.1.2版本解决