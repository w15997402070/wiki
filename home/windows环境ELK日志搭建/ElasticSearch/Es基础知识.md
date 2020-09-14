# Elasticsearch

[toc]

## 索引

文档归属于一种类型(type),而这些类型存在于索引(index)中

```
Relational -> Databases -> Tables -> Rows -> Columns
Elasticsearch -> Indexes -> Types -> Documents -> Fields
```

Elasticsearch集群可以包含多个索引(Indexes)(数据库),每一个索引可以包含多个类型(types)(表),每一个类型可以包含多个文档(documents)(行),然后每个文档包含多个字段(Fields)(列)

```
GET /megacrop/employee/_search?q=last_name:Smith
# 和上面等价
GET /megacrop/employee/_search
{
  "query" : {
    "match": {
      "last_name": "Smith"
    }
  }
}

```

```
#相关性查询
GET /megacrop/employee/_search
{
  "query" : {
    "match": {
      "about": "rock climbing"
    }
  }
}
//返回结果
{
  "took": 13,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 2,
    "max_score": 0.5753642,
    "hits": [
      {
        "_index": "megacrop",
        "_type": "employee",
        "_id": "1",
        "_score": 0.5753642,
        "_source": {
          "first_name": "John",
          "last_name": "Smith",
          "age": 25,
          "about": "I love to go rock climbing",
          "interests": [
            "sports",
            "music"
          ]
        }
      },
      {
        "_index": "megacrop",
        "_type": "employee",
        "_id": "2",
        "_score": 0.2876821,
        "_source": {
          "first_name": "Jane",
          "last_name": "Smith",
          "age": 32,
          "about": "I love to go rock albums",
          "interests": [
            "music"
          ]
        }
      }
    ]
  }
}
```



```
# 匹配短语
GET /megacrop/employee/_search
{
  "query" : {
    "match_phrase": {
      "about": "rock climbing"
    }
  }
}
# 结果只返回一条数据
{
  "took": 47,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 1,
    "max_score": 0.5753642,
    "hits": [
      {
        "_index": "megacrop",
        "_type": "employee",
        "_id": "1",
        "_score": 0.5753642,
        "_source": {
          "first_name": "John",
          "last_name": "Smith",
          "age": 25,
          "about": "I love to go rock climbing",
          "interests": [
            "sports",
            "music"
          ]
        }
      }
    ]
  }
}
```

```
# 高亮查询
GET /megacrop/employee/_search
{
  "query" : {
    "match_phrase": {
      "about": "rock climbing"
    }
  },
  "highlight": {
    "fields": {
      "about": {}
    }
  }
}
#结果
{
  "took": 105,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 1,
    "max_score": 0.5753642,
    "hits": [
      {
        "_index": "megacrop",
        "_type": "employee",
        "_id": "1",
        "_score": 0.5753642,
        "_source": {
          "first_name": "John",
          "last_name": "Smith",
          "age": 25,
          "about": "I love to go rock climbing",
          "interests": [
            "sports",
            "music"
          ]
        },
        "highlight": {
          "about": [
            "I love to go <em>rock</em> <em>climbing</em>"
          ]
        }
      }
    ]
  }
}
```

```
# 统计相当于group by
GET /megacrop/employee/_search
{
 "aggs": {
   "all_insterests": {
     "terms": {
       "field": "interests.keyword"
     }
   }
 }
}
# 结果
{
  "took": 13,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 1,
    "hits":[...]//省略
  },
  "aggregations": {
    "all_insterests": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "music",
          "doc_count": 2
        },
        {
          "key": "forestry",
          "doc_count": 1
        },
        {
          "key": "sports",
          "doc_count": 1
        }
      ]
    }
  }
}
```

```
#统计
GET /megacrop/employee/_search
{
 "aggs": {
   "all_insterests": {
     "terms": {
       "field": "interests.keyword"
     },
     "aggs": {
       "avg_age": {
         "avg": {
           "field": "age"
         }
       }
     }
   }
 }
}
#结果
{
  "took": 65,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 1,
    "hits": [...]//省略
  },
  "aggregations": {
    "all_insterests": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "music",
          "doc_count": 2,
          "avg_age": {
            "value": 28.5
          }
        },
        {
          "key": "forestry",
          "doc_count": 1,
          "avg_age": {
            "value": 32
          }
        },
        {
          "key": "sports",
          "doc_count": 1,
          "avg_age": {
            "value": 25
          }
        }
      ]
    }
  }
}
```



## 分布式集群

* 集群(Cluster)

* 节点(node)

一个节点就是一个Elasticsearch实例,而一个集群由一个或多个节点组成,他们具有相同的`cluster.name`.

集群中的一个节点会被选举为主节点(master),它将临时管理集群级别的一些变更,例如新建索引,增加或移除节点等.主节点不参与文档级别的变更或搜索,这意味着在流量增长的时候,该节点不会成为集群的瓶颈.任何节点都可以成为主节点

* 分片(shard)

  一个分片是一个最小级别的"工作单元",它只是保存了索引中所有数据的一部分

  * 主要分片(primary shard):
  * 复制分片(replica shard):

* 

### 集群健康

集群有三种状态:green,yellow,red

* green:所有主要分片和复制分片都可用
* yellow:所有主要分片可用,但不是所有复制分片都可用
* red:不是所有的主要分片都可用

```
GET /_cluster/health

{
  "cluster_name": "elasticsearch",
  "status": "yellow",
  "timed_out": false,
  "number_of_nodes": 1,
  "number_of_data_nodes": 1,
  "active_primary_shards": 6,
  "active_shards": 6,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 5,
  "delayed_unassigned_shards": 0,
  "number_of_pending_tasks": 0,
  "number_of_in_flight_fetch": 0,
  "task_max_waiting_in_queue_millis": 0,
  "active_shards_percent_as_number": 54.54545454545454
}
```

## 英文术语

* 索引(index) -> indexes,indices
* 分片(shard)  -> shards,主分片(primary shard ),复制分片(replica shard)  
* 逻辑命名空间(logical namespace)
* 倒排索引(inverted index)  