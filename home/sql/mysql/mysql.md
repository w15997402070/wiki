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


## 索引

### 索引概念

索引是帮助MySQL高效获取数据的排好序的数据结构

### 索引结构

高度小,查找快

### 索引实现底层原理

[MySQL-InnoDB-MVCC多版本并发控制](https://segmentfault.com/a/1190000012650596)

#### 二叉树

#### 红黑树

#### Hash表

对索引的key进行一次hash计算就可以定位出数据存储的位置

很多时候Hash索引要比B+树索引更高效

仅能满足"=","IN",不支持范围查询

hash冲突问题

#### B-Tree

1. 页节点具有相同的深度,叶节点的指针为空

2. 所有索引不重复

3. 节点中的数据索引从左到右递增排列

B-Tree , B+Tree(多叉平衡树)

* B+Tree

    * 非叶子几点不存储data,只存储索引(冗余),可以放更多的索引
    * 叶子节点包含所有的索引字段
    * 叶子节点用指针连接,提高区间访问的性能

    

    ```sql
    -- 查询mysql一页数据的大小
    SHOW GLOBAL STATUS LIKE 'Innodb_page_size'
    
    Variable_name	Value
    Innodb_page_size	16384
    ```

    ![image-20210221213232081](https://raw.githubusercontent.com/w15997402070/images/main/note/image-20210221213232081.png)

    假设索引字段用bigint 8字节,加上子节点的地址6字节,第一层可以放16kb/8byte+6byte≈1170

    第二层每个节点同样的也是1170,

    假设高度3的时候,叶子节点每个数据为1kb,可以存16条数据,

    三层计算下来`1170*1170*16=21902400`大概2000多万条数据

    

    主键索引

    InnoDB 引擎的数据存储在叶子节点

    ![image-20210221214855944](https://raw.githubusercontent.com/w15997402070/images/main/note/image-20210221214855944.png)

    MyISAM引擎的数据存储在单独文件中,索引叶子节点存储数据存放的地址,通过索引找到地址后再按照地址找数据

    ![image-20210221214920636](https://raw.githubusercontent.com/w15997402070/images/main/note/image-20210221214920636.png)

* 

###  索引类型

#### 聚集索引

页节点包含了完整的数据记录(InnoDB引擎)

#### 非聚集索引

叶子节点不包含完整数据(MyISAM引擎)



MySQL的BTree索引使⽤的是B树中的B+Tree，但对于主要的两种存储引擎的实现⽅式是不同的。
**MyISAM**: B+Tree叶节点的data域存放的是数据记录的地址。在索引检索的时候，⾸先按照B+Tree
搜索算法搜索索引，如果指定的Key存在，则取出其 data 域的值，然后以 data 域的值为地址
读取相应的数据记录。这被称为“⾮聚	簇索引”。
**InnoDB**: 其数据⽂件本身就是索引⽂件。相⽐MyISAM，索引⽂件和数据⽂件是分离的，其表数据
⽂件本身就是按B+Tree组织的⼀个索引结构，树的叶节点data域保存了完整的数据记录。这个索
引的key是数据表的主键，因此InnoDB表数据⽂件本身就是主索引。这被称为“聚簇索引（或聚集
索引） ”。⽽其余的索引都作为辅助索引，辅助索引的data域存储相应记录主键的值⽽不是地
址，这也是和MyISAM不同的地⽅。 **在根据主索引搜索时，直接找到key所在的节点即可取出数**
**据；在根据辅助索引查找时，则需要先取出主键的值，再⾛⼀遍主索引。 因此，在设计表的时**
**候，不建议使⽤过⻓的字段作为主键，也不建议使⽤⾮单调的字段作为主键，这样会造成主索引**
**频繁分裂。**  

#### 主键索引

#### 联合索引

联合索引存储结构

![image-20210222210644026](https://raw.githubusercontent.com/w15997402070/images/main/note/image-20210222210644026.png)

最左前缀原则



1. 为什么建议InnoDB表必须建主键,并且推荐使用整形的自增主键?

如果自己不建主键索引,MySQL会自己查找一列数据都不相同的建默认的主键索引,如果还找不到就会建一个类似rowid的唯一的隐藏列,这会消耗MySQL的资源的,所以最好自己建主键索引

2. 为什么非主键索引结构叶子节点存储的是主键值?(一致性和节省存储空间)

### 高性能的索引策略

* 独立的列

    指索引不能是表达式的一部分,也不能是函数的参数

    ```sql
    SELECT id,`name` FROM test WHERE id+1 = 2
    ```

    **所以应该始终将索引列单独放在比较符号的一侧**

* 前缀索引

* 合适的索引列顺序



## MYSQL相关SQL

### 查找MySQL支持的引擎

```sql
-- 查询引擎
SHOW ENGINES;

-- 查询默认引擎
show variables like '%storage_engine%';

-- 查询指定表的引擎
SHOW TABLE STATUS LIKE "table_name" ;

-- 检查是否自动提交
SHOW VARIABLES LIKE 'autocommit';
-- 设置不自动提交
SET autocommit=0;

-- MySQL InnoDB 存储引擎的默认⽀持的隔离级别是 REPEATABLE-READ（可重读） 
SELECT @@tx_isolation;
```

![image-20210222211558681](https://raw.githubusercontent.com/w15997402070/images/main/note/image-20210222211558681.png)

## MyISAM和InnoDB区别

MyISAM是MySQL的默认数据库引擎（5.5版之前）。虽然性能极佳，⽽且提供了⼤量的特性，包括全⽂索
引、压缩、空间函数等，但MyISAM不⽀持事务和⾏级锁，⽽且最⼤的缺陷就是崩溃后⽆法安全恢复。不
过， 5.5版本之后， MySQL引⼊了InnoDB（事务性数据库引擎）， MySQL 5.5版本后默认的存储引擎为
InnoDB。
⼤多数时候我们使⽤的都是 InnoDB 存储引擎，但是在某些情况下使⽤ MyISAM 也是合适的⽐如读密集
的情况下。（如果你不介意 MyISAM 崩溃恢复问题的话）  

两者的对⽐：

1. 是否⽀持⾏级锁 : MyISAM 只有表级锁(table-level locking)，⽽InnoDB ⽀持⾏级锁(rowlevel locking)和表级锁,默认为⾏级锁。
2. 是否⽀持事务和崩溃后的安全恢复： MyISAM 强调的是性能，每次查询具有原⼦性,其执⾏速度
    ⽐InnoDB类型更快，但是不提供事务⽀持。但是InnoDB 提供事务⽀持事务，外部键等⾼级数据
    库功能。 具有事务(commit)、回滚(rollback)和崩溃修复能⼒(crash recovery capabilities)
    的事务安全(transaction-safe (ACID compliant))型表。
3. 是否⽀持外键： MyISAM不⽀持，⽽InnoDB⽀持。
4. 是否⽀持MVCC ：仅 InnoDB ⽀持。应对⾼并发事务, MVCC⽐单纯的加锁更⾼效;MVCC只在
    READ COMMITTED 和 REPEATABLE READ 两个隔离级别下⼯作;MVCC可以使⽤ 乐观
    (optimistic)锁 和 悲观(pessimistic)锁来实现;各数据库中MVCC实现并不统⼀。推荐阅读：
    MySQL-InnoDB-MVCC多版本并发控制  

## 大表优化

当MySQL单表记录数过⼤时，数据库的CRUD性能会明显下降，⼀些常⻅的优化措施如下：

1. 限定数据的范围
    务必禁⽌不带任何限制数据范围条件的查询语句。⽐如：我们当⽤户在查询订单历史的时候，我们可以
    控制在⼀个⽉的范围内；

2. 读/写分离
    经典的数据库拆分⽅案，主库负责写，从库负责读； 

3. 垂直分区
    根据数据库⾥⾯数据表的相关性进⾏拆分。 例如，⽤户表中既有⽤户的登录信息⼜有⽤户的基本信
    息，可以将⽤户表拆分成两个单独的表，甚⾄放到单独的库做分库。
    简单来说垂直拆分是指数据表列的拆分，把⼀张列⽐较多的表拆分为多张表。 如下图所示，这样来说
    ⼤家应该就更容易理解了。  

    垂直拆分的优点： 可以使得列数据变⼩，在查询时减少读取的Block数，减少I/O次数。此外，
    垂直分区可以简化表的结构，易于维护。
    垂直拆分的缺点： 主键会出现冗余，需要管理冗余列，并会引起Join操作，可以通过在应⽤层
    进⾏Join来解决。此外，垂直分区会让事务变得更加复杂；  

4. ⽔平分区
    保持数据表结构不变，通过某种策略存储数据分⽚。这样每⼀⽚数据分散到不同的表或者库中，达到了
    分布式的⽬的。 ⽔平拆分可以⽀撑⾮常⼤的数据量。
    ⽔平拆分是指数据表⾏的拆分，表的⾏数超过200万⾏时，就会变慢，这时可以把⼀张的表的数据拆成
    多张表来存放。举个例⼦：我们可以将⽤户信息表拆分成多个⽤户信息表，这样就可以避免单⼀表数据
    量过⼤对性能造成影响。  

    ⽔平拆分可以⽀持⾮常⼤的数据量。需要注意的⼀点是：分表仅仅是解决了单⼀表数据过⼤的问题，但
    由于表的数据还是在同⼀台机器上，其实对于提升MySQL并发能⼒没有什么意义，所以 ⽔平拆分最好分
    库 。
    ⽔平拆分能够 ⽀持⾮常⼤的数据量存储，应⽤端改造也少，但 分⽚事务难以解决 ，跨节点Join性能
    较差，逻辑复杂。  

5. 