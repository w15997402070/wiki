# 创建链接服务器

```sql
EXEC  sp_addlinkedserver

      @server='view_dd',--被访问的服务器别名

      @srvproduct='',

      @provider='SQLOLEDB',

      @datasrc="192.168.0.235,1490"  --要访问的服务器
GO

EXEC sp_addlinkedsrvlogin

     'view_dd',--被访问的服务器别名

     'false',

     NULL,

     'sa',--帐号

     '123456' --密码

Go
	 
	 --查看链接服务器
EXEC sp_linkedservers;
	 --删除链接服务器
EXEC sp_dropserver 'view_dd';
-- 查询数据 `别名.数据库名.dbo.表名`
select * from view_dd.mtmrp.dbo.v_done_book_detail_if;
```

