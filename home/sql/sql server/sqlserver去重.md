# sqlserver去重
## 1.distinct字段
- sqlserver 使用distinct字段查询时,distinct必须放在字段前面.
   ```sql
       SELECT xu.id,DISTINCT xu.user_name from xiron_user xu;
   ``` 
   会报错[Err] 42000 - [SQL Server]关键字 'DISTINCT' 附近有语法错误。
   ```sql
      SELECT DISTINCT xu.user_name,xu.USER_ID from xiron_user xu;
   ```
   这样就可以查询.  
   distinct 后面跟几个字段就表示根据几个字段去重
   例如 : 
   ```sql
      SELECT DISTINCT xu.user_name from xiron_user xu;
   ```
   就是去除了user_name重复的值
   ```sql
      SELECT DISTINCT xu.user_name,xu.USER_ID from xiron_user xu;
   ```
   就是去除了user_name 和 USER_ID两个字段都重复的值.
## 2.group by 
- sqlserver 使用group by 查询时,查询的字段中只能包含group by 的字段.
```sql
SELECT xu.USER_ID,xu.user_name from xiron_user xu GROUP BY xu.USER_NAME;
```
会报错 ,[Err] 42000 - [SQL Server]选择列表中的列 'xiron_user.USER_ID' 无效，因为该列没有包含在聚合函数或 GROUP BY 子句中。
```sql
SELECT xu.user_name from xiron_user xu GROUP BY xu.USER_NAME;
```
这样就可以了.
但是这样就达不到去重的效果,所以需要使用聚合函数
```sql
SELECT MAX(xu.USER_ID),xu.user_name from xiron_user xu GROUP BY xu.USER_NAME;
```
这样就可以查到最大的user_id 的数据,再联合查一下就可以查到去重之后的所有数据
```sql
SELECT * from xiron_user x WHERE x.USER_ID in( SELECT MAX(xu.USER_ID) from xiron_user xu GROUP BY xu.USER_NAME);
```