# 更新指定字段

```java
 /**
     * 根据某个字段的 查询，统一修改
     * update table  set updatefield = updateValue where field = filedValue
     * @param client
     * @param content
     * @param article
     * @param field 根据某个字段查询
     * @param filedValue 值
     * @param updatefield 修改的字段名称
     * @param updateValue 修改的字段值
     */
    public long updateQuery(TransportClient client,String field ,String filedValue, String updatefield,String updateValue,String article,String content){
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        updateByQuery.source(article)
                //查询要修改的结果集
                .filter(QueryBuilders.termQuery(field, filedValue))
                //修改操作
                .script(new Script( "ctx._source['"+updatefield+"']='"+updateValue+"';ctx._source['"+updatefield+"']='"+updateValue+"'"));
        //响应结果集
        BulkByScrollResponse response = updateByQuery.get();
        long updated = response.getUpdated();
        return updated;
    }
```