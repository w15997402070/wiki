# es增删改查

[toc]



## 索引

### 创建索引  

    curl -X PUT "localhost:9200/information?pretty"
    
    可以设置副本和分片 : 
    ```json
    curl -X PUT "localhost:9200/information?pretty" -d'
    {
        "settings": {
            "index" : {
            "number_of_shards" : 3, //设定了索引的分片数量
            "number_of_replicas" : 2 //副本数量
            }
        }
    }'
    ```

### 创建索引结构

    curl -X PUT "localhost:9200/information/book/mapping?pretty" -d'

### 查看所有的索引

    curl -X GET "localhost:9200/_cat/indices?v"

## 新增

### 添加数据

    curl -X PUT "localhost:9200/information/content/1?pretty" -H 'Content-Type: application/json' -d'
    {
     "auditCause": "",
     "readRight": "",
     "updateDate": 1541554874000,
     "publishDate": 1541554874000
    }'

## 更新

### 修改数据  重新按照id添加就可以覆盖前面数据

    curl -X PUT "localhost:9200/information/content/1?pretty" -H 'Content-Type: application/json' -d'
    {
     "auditCause": "2",
     "updateDate": 1541554874000,
     "publishDate": 1541554874000
     }'

### 更新指定字段

    curl -X POST "localhost:9200/information/content/1/_update?pretty" -H 'Content-Type: application/json' -d'
    {
      "doc": { "auditCause": "2", "publishDate": 20 }
    }'

### 也可以用脚本更新

    curl -X POST "localhost:9200/information/content/1/_update?pretty" -H 'Content-Type: application/json' -d'
    {
      "script" : "ctx._source.auditCause += 5"
    }'

### 批量更新

```
curl -X POST "localhost:9200information_square/content/_update_by_query"
{
    "script": {
       "lang": "painless",
       "inline": "if (ctx._source.isWork== null){ctx._source.isWork= 1}"
     }
}
```



## 查询

### 查询一条数据

    curl -X GET "localhost:9200/information/content/1?pretty"

### 查询多个文档

```
POST /_mget
{
  "docs" : [
     {
       "_index" : "book",
       "_type" : "info",
       "_id" : 19
     },
     {
       "_index" : "bank",
       "_type" : "account",
       "_id" : 25,
       "_source" : "firstname"
     }
     
  ]
}
```



## 删除

### 删除索引

    curl -X DELETE "localhost:9200/information?pretty"

### 删除数据

    curl -X DELETE "localhost:9200/information/content/1?pretty"

### 删除所有数据

```
POST information_square/content/_delete_by_query
{
  "query": { 
    "match_all": {}
  }
}
```



## Bulk API

Bulk API允许我们使用单一请求来实现多个文档的create,index,update,delete

bulk 请求体如下:

```json
{action : { metadata}} \n
{request body } \n
{action : {metadata }} \n
{ request body} \n
...
```

这种格式类似于用“\n”符号连接起来的一行一行的JSON文档流（stream）。两个重要的点需要注意：

* 每行必须以"\n”符号结尾，包括最后一行。这些都是作为每行有效的分离而做的标记。
* 每一行的数据不能包含未被转义的换行符，它们会干扰分析——这意味着JSON不能被美化打印。

action/metadata这一行定义了文档行为（what action）发生在哪个文档（which document）之上。



| 行为   | 解释                     |
| ------ | ------------------------ |
| create | 当文档不存在时创建之     |
| index  | 创建新文档或替换已有文档 |
| update | 局部更新文档             |
| delete | 删除一个文档             |

在索引、创建、更新或删除时必须指定文档的index、type、id 这些元数据（metadata）。



例如

```json
{"delete": {"_index":"bank","_type" : "account", "id" : "25"}} <1>
{"create": {"_index":"bank","_type" : "account", "id" : "25"}}
{"title": "My firdt account"}
{"index": {"_index": "bank", "_type": "account"}}
{"title" : "My second account"}
{"update":{"_index": "bank", "_type": "account" ,"_id": "25"}}
{"doc": {"title": "My update account post"}}<2>
```

* `<1>`:注意delete行为没有请求体,它紧接着另一个行为
* `<2>`:记得最后一个换行符

## 集群查询

### 查看集群健康状态

`GET _cat/health?v`

```
epoch      timestamp cluster    status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1593423547 09:39:07  es-cluster green           3         3     10   5    0    0        0             0                  -                100.0%

```



### 查看集群节点个数

`GET _cat/nodes?v`

```
ip             heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
192.168.50.131           14          16   0    0.03    0.03     0.05 mdi       *      node-50131
192.168.51.130           10          17   0    0.07    0.05     0.05 mdi       -      node-51130
192.168.51.131           17          28   0    0.00    0.03     0.05 mdi       -      node-51131

```

### 查看集群索引

`GET _cat/indices?v`

```
health status index                           uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .security-6                     aANT6A3FTauTbTkc1HkAxA   1   1                                                  
green  open   .monitoring-kibana-6-2020.06.29 o3cFx_29RGqguq9KeV7vZQ   1   1                                                  
green  open   .kibana_task_manager            z5Ew43VIRSu-l4H_ozbCpQ   1   1          2            0     25.1kb         12.5kb
green  open   .kibana_1                       nJs8cz6NRL6bzIcqypetVQ   1   1          3            0     27.8kb         13.9kb
green  open   .monitoring-es-6-2020.06.29     FfsgCDb9R3ejL-dQctUfYg   1   1                                                  

```

