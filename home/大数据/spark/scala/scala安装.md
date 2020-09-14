# Scala安装

下载包

`scala-2.13.1.tgz`

解压

```bash
$ tar -zxvf scala-2.13.1.tgz
```

配置环境变量

`/etc/profile`

```profile

export SCALA_HOME=/usr/local/userdata/scala-2.13.1
export PATH=$PATH:$SCALA_HOME/bin

```

```bash
# 刷新环境变量
$ source /etc/profile
```

查看是否安装成功

```bash
$ scala -version
Scala code runner version 2.13.1 -- Copyright 2002-2019, LAMP/EPFL and Lightbend, Inc.

```

