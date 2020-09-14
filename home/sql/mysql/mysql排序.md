# mysql排序处理null值

## 1.排序处理空值

`order by column is null,column;`  

如果:order by column,则column中空值的数据放在最前面,有数据的放在后面

## 2.null值放在前面处理

   1.将null强制放在最前：

    if(isnull(字段名),0,1) asc   //asc可以省略

　　2.将null强制放在最后

    if(isnull(字段名),0,1) desc

    if(isnull(字段名),1,0)  asc   //asc可以省略

## 3.随机排序

`Order By Rand()`  

表数据量大了之后查询效率低

数据量大了之后可以分两次查询

只查id字段,有索引查询很快  
`SELECT id FROM zs_ctt_information ORDER BY RAND() LIMIT 10;`  
再写一条sql  
`SELECT * FROM zs_ctt_information i WHERE i.`ID` IN (?);`  
不能用子查询,子查询里面不能用limit

## 4.按照当前时间的远近排序

```sql
SELECT
  a.ID,
  -- 先按在当前时间之前还是之后排序
  ( CASE
      WHEN a.`HOLDING_TIME_START` >= NOW() THEN 0
      WHEN a.`HOLDING_TIME_START` < NOW() THEN 1
    END
  )sort_compare_now,
  -- 按时间间隔排序
  (CASE
     WHEN a.`HOLDING_TIME_START` >= NOW() THEN TIMEDIFF(a.`HOLDING_TIME_START`, NOW())
     WHEN a.`HOLDING_TIME_START`< NOW() THEN TIMEDIFF(NOW(),a.`HOLDING_TIME_START`)
  END
  )sort_lastdate
FROM
  zs_ctt_meeting_exhibition a
ORDER BY 
 sort_compare_now ASC,sort_lastdate ASC
LIMIT 6
```