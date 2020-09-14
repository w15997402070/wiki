# 相关命令

[toc]

查看主题命令

```bash
$ ./kafka-topics.sh --list --zookeeper localhost:2181 
```

创建主题

```bash
$ sh kafka-topics.sh --create --topic kafkatopictest --replication-factor 1 --partitions 1 --zookeeper localhost:2181
```

删除主题

```bash
$ ./kafka-topics.sh --delete --zookeeper localhost:2181 --topic kafkatopictest
```

