# Storm安装配置

[toc]



## 1. 下载包

`apache-storm-2.1.0.tar.gz`

## 2. 解压缩包

`$ tar -zxvf apache-storm-2.1.0.tar.gz`

## 启动zookeeper

进入zookeeper目录启动zookeeper

```bash
$ bin/zkServer.sh start
```



### 3. 配置文件

在`conf/storm.yaml`填写一下内容

```yaml
storm.zookeeper.servers:
 - "master"
storm.local.dir: "/usr/local/userdata/data/stormdata"
nimbus.host: "master"
supervisor.slots.ports:
 - 6701
 - 6702
 - 6703
ui.port: 6700

```

### 启动Nimbus

```bash
$ bin/storm nimbus
```

### 启动Supervisor

```bash
$ bin/storm supervisor
```

### 启动UI

```bash
$ bin/storm ui
```

可以用`http://localhost:6700`访问UI界面

ui端口在上面配置文件中`ui.port: 6700`配置