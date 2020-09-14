#1.停止mysql数据库
/etc/init.d/mysqld stop

#2.执行如下命令
mysqld_safe --user=mysql --skip-grant-tables --skip-networking &

#3.使用root登录mysql数据库
mysql -u root mysql

#4.更新root密码
mysql> UPDATE user SET Password=PASSWORD('newpassword') where USER='root';
#最新版MySQL请采用如下SQL：
mysql> UPDATE user SET authentication_string=PASSWORD('newpassword') where USER='root';

#5.刷新权限
mysql> FLUSH PRIVILEGES;

#6.退出mysql
mysql> quit

#7.重启mysql
/etc/init.d/mysqld restart

#8.使用root用户重新登录mysql
mysql -uroot -p

#初次登陆显示密码需要重置
mysql> ALTER USER USER() IDENTIFIED BY 'abc';
ERROR 1819 (HY000): Your password does not satisfy the current
policy requirements
mysql> ALTER USER 'jeffrey'@'localhost' IDENTIFIED WITH mysql_native_password AS'*0D3CED9BEC10A777AEC23CCC353A8C08A633045E';

# 然后再重新执行一次重置密码的操作;



mysql更改可以使用简单密码

```sql
set global validate_password_policy=0;
set global validate_password_mixed_case_count=0;
set global validate_password_number_count=3;
set global validate_password_special_char_count=0;
set global validate_password_length=3;

-- 查看设置的选项
SHOW VARIABLES LIKE 'validate_password%';

+--------------------------------------+-------+
| Variable_name                        | Value |
+--------------------------------------+-------+
| validate_password_check_user_name    | OFF   |
| validate_password_dictionary_file    |       |
| validate_password_length             | 3     |
| validate_password_mixed_case_count   | 0     |
| validate_password_number_count       | 3     |
| validate_password_policy             | LOW   |
| validate_password_special_char_count | 0     |
+--------------------------------------+-------+


```

