# 简单概念

[toc]

## term

term 精确匹配  相当于 =  

```json
{ "term": { "age":    26           }}
{ "term": { "date":   "2014-09-01" }}
{ "term": { "public": true         }}
{ "term": { "tag":    "full_text"  }}
```

terms 查询和 term 查询一样，但它允许你指定多值进行匹配。如果这个字段包含了指定值中的任何一个值，

```json
{ "terms": { "tag": [ "search", "full_text", "nosql" ] }}
```

exists 查询和 missing 查询被用于查找那些指定字段中有值 (exists) 或无值 (missing) 的文档。这与SQL中的 IS_NULL (missing) 和 NOT IS_NULL (exists) 在本质上具有共性：

```json
{
    "exists":   {
        "field":    "title"
    }
}
```

wildcard 通配符查询
prefix 前缀
fuzzy  
搜索的时候，可能输入的搜索文本会出现误拼写的情况
自动将拼写错误的搜索文本，进行纠正，纠正以后去尝试匹配索引中的数据
纠正在一定的范围内如果差别大无法搜索出来

```txt
must 文档 必须 匹配这些条件才能被包含进来。
must_not 文档 必须不 匹配这些条件才能被包含进来。
should  如果满足这些语句中的任意语句，将增加 _score ，否则，无任何影响。它们主要用于修正每个文档的相关性得分。
filter 必须 匹配，但它以不评分、过滤模式来进行。这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。
```

## 合并查询语句

查询语句(Query clauses) 就像一些简单的组合块 ，这些组合块可以彼此之间合并组成更复杂的查询。这些语句可以是如下形式：

叶子语句（Leaf clauses） (就像 match 语句) 被用于将查询字符串和一个字段（或者多个字段）对比。
复合(Compound) 语句 主要用于 合并其它查询语句。 比如，一个 bool 语句 允许在你需要的时候组合其它语句，无论是 must 匹配、 must_not 匹配还是 should 匹配，同时它可以包含不评分的过滤器（filters）：

```json
{
    "bool": {
        "must":     { "match": { "tweet": "elasticsearch" }},
        "must_not": { "match": { "name":  "mary" }},
        "should":   { "match": { "tweet": "full text" }},
        "filter":   { "range": { "age" : { "gt" : 30 }} }
    }
}
```

一条复合语句可以合并 任何 其它查询语句，包括复合语句，了解这一点是很重要的。这就意味着，复合语句之间可以互相嵌套，可以表达非常复杂的逻辑。

```json
{
    "bool": {
        "must": { "match":   { "email": "business opportunity" }},
        "should": [
            { "match":       { "starred": true }},
            { "bool": {
                "must":      { "match": { "folder": "inbox" }},
                "must_not":  { "match": { "spam": true }}
            }}
        ],
        "minimum_should_match": 1
    }
}
```

## 排序

### 字段的值排序

在这个案例中，通过时间来对 tweets 进行排序是有意义的，最新的 tweets 排在最前。 我们可以使用 sort 参数进行实现：

```json
GET /_search
{
    "query" : {
        "bool" : {
            "filter" : { "term" : { "user_id" : 1 }}
        }
    },
    "sort": { "date": { "order": "desc" }}
}
```

### 多级排序

假定我们想要结合使用 date 和 _score 进行查询，并且匹配的结果首先按照日期排序，然后按照相关性排序：

```json
GET /_search
{
    "query" : {
        "bool" : {
            "must":   { "match": { "tweet": "manage text search" }},
            "filter" : { "term" : { "user_id" : 2 }}
        }
    },
    "sort": [
        { "date":   { "order": "desc" }},
        { "_score": { "order": "desc" }}
    ]
}
```

### 多值字段的排序

一种情形是字段有多个值的排序， 需要记住这些值并没有固有的顺序；一个多值的字段仅仅是多个值的包装，这时应该选择哪个进行排序呢？

对于数字或日期，你可以将多值字段减为单值，这可以通过使用 min 、 max 、 avg 或是 sum 排序模式 。 例如你可以按照每个 date 字段中的最早日期进行排序，通过以下方法：

```json
"sort": {
    "dates": {
        "order": "asc",
        "mode":  "min"
    }
}
```

```txt
gt   大于
gte  大于等于
lt   小于
lte  小于等于
```
