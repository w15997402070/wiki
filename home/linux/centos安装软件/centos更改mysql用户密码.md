# centos 更改mysql用户密码

## 1.停止mysql数据库

    /etc/init.d/mysqld stop

## 2.执行如下命令

    mysqld_safe --user=mysql --skip-grant-tables --skip-networking &

## 3.使用root登录mysql数据库

    mysql -u root mysql

## 4.更新root密码

    mysql> UPDATE user SET Password=PASSWORD('newpassword') where USER='root';

`最新版MySQL请采用如下SQL` ：

    mysql> UPDATE user SET authentication_string=PASSWORD('newpassword') where USER='root';

## 5.刷新权限

    mysql> FLUSH PRIVILEGES;

## 6.退出mysql

    mysql> quit

## 7.重启mysql

    /etc/init.d/mysqld restart

## 8.使用root用户重新登录mysql

    mysql -uroot -p

## `初次登陆显示密码需要重置`

    mysql> ALTER USER USER() IDENTIFIED BY 'abc';
    ERROR 1819 (HY000): Your password does not satisfy the current
    policy requirements
    mysql> ALTER USER 'jeffrey'@'localhost' IDENTIFIED WITH mysql_native_password AS'*0D3CED9BEC10A777AEC23CCC353A8C08A633045E';

## `然后再重新执行一次重置密码的操作`

## `centos远程连接mysql (密码是Wzp@123456)`

### 设置权限

    mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Wzp@123456' WITH GRANT OPTION;
    mysql> flush privileges;
    
    
    GRANT ALL PRIVILEGES ON ai_check.* TO 'epp_qc'@'%';
    
    GRANT ALL ON *.* to epp_qc@'%' IDENTIFIED BY '!@Epp_qc123'; 

### `centos mysql的表名是区分大小写的`

设置表名怎么不区分大小写 :
用root帐号登录后，在/etc/my.cnf 中的[mysqld]后添加添加`lower_case_table_names=1`，重启MYSQL服务，  
`service mysqld restart` 这时已设置成功.不区分表名的大小写.

### lower_case_table_names参数详解：

lower_case_table_names = 0  
其中0：区分大小写，1：不区分大小写  

```conf
  SHOW VARIABLES LIKE '%case%';
  Variable_name                    Value
  lower_case_file_system             OFF
  lower_case_table_names              1
  validate_password_mixed_case_count  1
```

MySQL在Linux下数据库名、表名、列名、别名大小写规则是这样的：  

1. 数据库名与表名是严格区分大小写的；
2. 表的别名是严格区分大小写的；
3. 列名与列的别名在所有的情况下均是忽略大小写的；
4. 变量名也是严格区分大小写的；  

`MySQL在Windows下都不区分大小写`。
