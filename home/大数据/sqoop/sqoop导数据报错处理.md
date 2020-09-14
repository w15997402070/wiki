# sqoop 导数据报错处理

```shell
ERROR tool.ImportTool: Import failed: java.io.IOException: Generating splits for a textual index column allowed only in case of "-Dorg.apache.sqoop.splitter.allow_text_splitter=true" property passed as a parameter
    at org.apache.sqoop.mapreduce.db.DataDrivenDBInputFormat.getSplits(DataDrivenDBInputFormat.java:204)
    at org.apache.hadoop.mapreduce.JobSubmitter.writeNewSplits(JobSubmitter.java:310)
    at org.apache.hadoop.mapreduce.JobSubmitter.writeSplits(JobSubmitter.java:327)
    at org.apache.hadoop.mapreduce.JobSubmitter.submitJobInternal(JobSubmitter.java:200)
    at org.apache.hadoop.mapreduce.Job$11.run(Job.java:1570)
    at org.apache.hadoop.mapreduce.Job$11.run(Job.java:1567)
    at java.security.AccessController.doPrivileged(Native Method)
    at javax.security.auth.Subject.doAs(Subject.java:422)
    at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1729)
    at org.apache.hadoop.mapreduce.Job.submit(Job.java:1567)
    at org.apache.hadoop.mapreduce.Job.waitForCompletion(Job.java:1588)
    at org.apache.sqoop.mapreduce.ImportJobBase.doSubmitJob(ImportJobBase.java:200)
    at org.apache.sqoop.mapreduce.ImportJobBase.runJob(ImportJobBase.java:173)
    at org.apache.sqoop.mapreduce.ImportJobBase.runImport(ImportJobBase.java:270)
    at org.apache.sqoop.manager.SqlManager.importTable(SqlManager.java:692)
    at org.apache.sqoop.manager.MySQLManager.importTable(MySQLManager.java:127)
    at org.apache.sqoop.tool.ImportTool.importTable(ImportTool.java:520)
    at org.apache.sqoop.tool.ImportTool.run(ImportTool.java:628)
    at org.apache.sqoop.Sqoop.run(Sqoop.java:147)
    at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:76)
    at org.apache.sqoop.Sqoop.runSqoop(Sqoop.java:183)
    at org.apache.sqoop.Sqoop.runTool(Sqoop.java:234)
    at org.apache.sqoop.Sqoop.runTool(Sqoop.java:243)
    at org.apache.sqoop.Sqoop.main(Sqoop.java:252)
Caused by: Generating splits for a textual index column allowed only in case of "-Dorg.apache.sqoop.splitter.allow_text_splitter=true" property passed as a parameter
```

解决办法 :
在导入命令中加入`-Dorg.apache.sqoop.splitter.allow_text_splitter=true`

    sqoop import -Dorg.apache.sqoop.splitter.allow_text_splitter=true --connect jdbc:mysql://127.0.0.1:3306/dbname --username root --password 123456 --table item --columns 'ID,PRICE,COUNT' --hive-import --hive-table item;

```shell
ERROR hive.HiveConfig: Could not load org.apache.hadoop.hive.conf.HiveConf. Make sure HIVE_CONF_DIR is set correctly.
2019-05-12 15:04:22,938 ERROR tool.ImportTool: Import failed: java.io.IOException: java.lang.ClassNotFoundException: org.apache.hadoop.hive.conf.HiveConf
    at org.apache.sqoop.hive.HiveConfig.getHiveConf(HiveConfig.java:50)
    at org.apache.sqoop.hive.HiveImport.getHiveArgs(HiveImport.java:392)
    at org.apache.sqoop.hive.HiveImport.executeExternalHiveScript(HiveImport.java:379)
    at org.apache.sqoop.hive.HiveImport.executeScript(HiveImport.java:337)
    at org.apache.sqoop.hive.HiveImport.importTable(HiveImport.java:241)
    at org.apache.sqoop.tool.ImportTool.importTable(ImportTool.java:537)
    at org.apache.sqoop.tool.ImportTool.run(ImportTool.java:628)
    at org.apache.sqoop.Sqoop.run(Sqoop.java:147)
    at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:76)
    at org.apache.sqoop.Sqoop.runSqoop(Sqoop.java:183)
    at org.apache.sqoop.Sqoop.runTool(Sqoop.java:234)
    at org.apache.sqoop.Sqoop.runTool(Sqoop.java:243)
    at org.apache.sqoop.Sqoop.main(Sqoop.java:252)
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.conf.HiveConf
    at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
    at java.lang.Class.forName0(Native Method)
    at java.lang.Class.forName(Class.java:264)
    at org.apache.sqoop.hive.HiveConfig.getHiveConf(HiveConfig.java:44)
    ... 12 more

```

解决办法 :
将 `hive-common-2.3.4.jar` 包复制到sqoop lib 目录中

    cp /usr/local/apache-hive-2.3.4/lib/hive-common-2.3.4.jar /usr/local/sqoop-1.4.7/lib/
