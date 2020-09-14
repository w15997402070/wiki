## 查询正在执行的sql

```sql
SELECT  
der.[session_id],der.[blocking_session_id],  
sp.lastwaittype,sp.hostname,sp.program_name,sp.loginame,  
der.[start_time] AS '开始时间',  
der.[status] AS '状态',  
dest.[text] AS 'sql语句',  
DB_NAME(der.[database_id]) AS '数据库名',  
der.[wait_type] AS '等待资源类型',  
der.[wait_time] AS '等待时间',  
der.[wait_resource] AS '等待的资源',  
der.[logical_reads] AS '逻辑读次数'  
FROM sys.[dm_exec_requests] AS der  
INNER JOIN master.dbo.sysprocesses AS sp ON der.session_id=sp.spid  
CROSS APPLY  sys.[dm_exec_sql_text](der.[sql_handle]) AS dest  
--WHERE [session_id]>50 AND session_id<>@@SPID  
ORDER BY der.[session_id]  
GO

-- 查询进程
select * from master..sysprocesses;
-- 查询进程的sql 
dbcc inputbuffer(153); -- (spid)
```

