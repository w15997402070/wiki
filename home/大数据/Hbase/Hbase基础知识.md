# Hbase

[toc]

## Hbase基本操作

| Java类             | 对应数据模型               |
| ------------------ | -------------------------- |
| HbaseConfiguration | HBase配置类                |
| HBaseAdmin         | HBase管理Admin类           |
| Table              | HBase的Table操作类         |
| Put                | HBase添加操作数据模型      |
| Get                | HBase单个查询操作数据模型  |
| Scan               | HBase Scan检索操作数据模型 |
| Result             | HBase查询的结果模型        |
| ResultScanner      | HBase检索结果模型          |

## HBase过滤器

| 过滤器(Filter)     | 功能                             |
| ------------------ | -------------------------------- |
| RowFilter          | 筛选出匹配的所有的行             |
| PrefixFilter       | 筛选出具有特定前缀的行键的数据   |
| KeyOnlyFilter      | 只返回每行的行键,值全部为空      |
| ColumnPrefixFilter | 按照列名的前缀来筛选表格         |
| ValueFilter        | 按照具体的值来筛选单元格的过滤器 |
| TimestampsFilter   | 根据数据的时间戳版本进行过滤     |
| FilterList         | 用于综合使用多个过滤器           |
|                    |                                  |



## Phoenix

* 多租户
* 二级索引
* 用户自定义函数
* 行时间戳列
* 分页查询
* 视图