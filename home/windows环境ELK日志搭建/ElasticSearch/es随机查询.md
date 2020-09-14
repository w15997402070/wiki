# Es 随机查询

## rest 随机查询

```json
curl -XGET 'master:9200/aaa/_search?pretty'   -H 'Content-Type: application/json' -d'
{
  "from": 0,
  "size": 10,
  "query": {
    "match_all": {
}
  },
   "_source": {
    "includes": []
  },
  "sort": {
    "_script": {
      "script": "Math.random()",
      "type": "number",
      "order": "asc"
    }
  }
}
```

## java API 随机查询

```java
    //创建排序脚本
    Script script = new Script("Math.random()");
    ScriptSortBuilder scriptSortBuilder = SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
    //创建查询
    QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("title.keyword","*"+"测试"+"*");
    SearchResponse response = client.prepareSearch(article).setTypes(content)//
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)//
            .addSort(scriptSortBuilder)
            .setQuery(queryBuilder)
            .setSize(3)
            .get();

   //解析查询结果
    for (SearchHit searchHit : response.getHits()) {
             EsSearchInfo esSearchInfo = JSON.parseObject(JSONObject.toJSONString(searchHit.getSource()), EsSearchInfo.class);
            logger.info(JSONObject.toJSONString(searchHit.getSource()));
        }  
```