# 基本命令

[toc]

## HBase命令

* 启动hbase shell命令

  ```bash
  ./bin/hbase shell
  ```

  

* 

## 表命令



1. status -- 查看状态

```shell
-- 创建表
hbase(main):006:0>create 'testtable','colfam1'

-- 添加行和列
hbase(main):007:0>put 'testtable','myrow=1','colfam1:q1','value=1'
hbase(main):008:0>put 'testtable','myrow=2','colfam1:q2','value=2'
hbase(main):009:0>put 'testtable','myrow=2','colfam1:q3','value=3'


-- 查看表数据
hbase(main):010:0>scan 'testtable'
ROW                                      COLUMN+CELL
 myrow=1                                 column=colfam1:q1, timestamp=1558763800589, value=value=1
 myrow=2                                 column=colfam1:q2, timestamp=1558763815940, value=value=2
 myrow=2                                 column=colfam1:q3, timestamp=1558763841934, value=value=3

-- 查看一行
hbase(main):011:0> get 'testtable','myrow=1'
COLUMN                                   CELL
 colfam1:q1                              timestamp=1558763800589, value=value=1

--删除数据
hbase(main):012:0> delete 'testtable','myrow=2','colfam1:q3'

-- 禁用并且删除表
hbase(main):015:0> disable 'testtable'
Took 2.8396 seconds
hbase(main):016:0> drop 'testtable'


```

