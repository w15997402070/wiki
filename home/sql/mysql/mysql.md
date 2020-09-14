# Mysql知识

[toc]

## 权限

赋予远程连接root权限

```sql
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant OPTION;

flush privileges;
-- 创建用户
create user 'root'@'%' identified by '123456';
-- 授予远程连接权限
grant all privileges on *.* to 'root'@'%' with grant option;
-- mysql8修改成可以远程连接的原来的密码模式
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
-- 查询连接的模式
select host,user,plugin from user;
-- caching_sha2_password模式,Navicat和Sqlyog就连接不了,要改成 mysql_native_password 模式
mysql> select host,user,plugin from user;
+-----------+------------------+-----------------------+
| host      | user             | plugin                |
+-----------+------------------+-----------------------+
| %         | root             | mysql_native_password |
| localhost | mysql.infoschema | caching_sha2_password |
| localhost | mysql.session    | caching_sha2_password |
| localhost | mysql.sys        | caching_sha2_password |
| localhost | root             | mysql_native_password |
+-----------+------------------+-----------------------+


systemctl restart mysql.service
grant all privileges on ai_check.* to 'epp_qc'@'%' identified by '!@Epp_qc123'
epp_qc

change master to master_host='192.168.0.54', master_port=5406, master_user='sync', master_password='sync123', master_log_file='mysql-bin.000001', master_log_pos=154;
```



## 小技巧

* 计数器表

  如果应用在表中保存计数器，则在更新计数器时可能碰到并发问题。计数器表在Web应用中很常见。可以用这种表缓存一个用户的朋友数、文件下载次数等。创建一张独立的表存储计数器通常是个好主意，这样可使计数器表小且快。使用独立的表可以帮助避免查询缓存失效

* 