# Oracle

[toc]

## Oracle 解锁Scott账户

进入oracle数据库命令行

```sh
sqlplus /nolog --访问不了时参见docker安装oracle文档
SQL> alter user scott account unlock;
User altered.
SQL> commit;
Commit complete.
SQL> conn scott/tiger
ERROR:
ORA-28001: the password has expired
Changing password for scott
New password:  helowin
Retype new password:  helowin
Password changed
Connected.
SQL> 

(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 118.24.110.224)(PORT = 1521))(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = helowin)))
```

## oracle基本术语

1．Oracle实例/ Oracle数据库

（1）实例（Instance）实例是一个非固定的、基于内存的基本进程与内存结构。当服务器关闭后，实例也就不存在了。

（2）数据库（Database）数据库指的是固定的、基于磁盘的数据文件、控制文件、日志文件、参数文件和归档日志文件等。一般情况下，Oracle数据库都是一个数据库包含一个实例

2．数据库服务器数据库服务器（Database Server）

一般指的是数据库各软件部件（如sqlplus,OEM,EXP/IMP等）和实例及数据库3个主要部分，是由安装在服务器上的所有软件（包括各种类型的文件）及启动成功后的实例组成的。

![image-20200901215622275](D:\data\notes\notes\sql\oracle\oracle\image-20200901215622275.png)

1．表空间

在Oracle数据库系统中，用于存放数据库表、索引、回滚段等对象的磁盘逻辑空间叫表空间（tablespace）

![image-20200901215704570](D:\data\notes\notes\sql\oracle\oracle\image-20200901215704570.png)

一般在完成Oracle系统的安装并创建Oracle实例后，Oracle系统自动建立多个表空间。

下面是Oracle 11g版本默认创建的主要表空间

（1）SYSTEM表空间

SYSTEM表空间用于存放Oracle系统内部表和数据字典的数据，如表名、列名、用户名等。不赞成将用户创建的表、索引等存放在SYSTEM表空间中。SYSTEM表空间对应的数据文件是system01.dbf，如果表空间对应的数据文件比较小，也可以追加另外一个新的数据文件，如图4-4所示的SYSTEM表空间对应system01.dbf和system02.dfb两个数据文件。

（2）SYSAUX表空间

SYSAUX表空间是Oracle 11g新增加的表空间，主要用于存放Oracle系统内部的常用样例用户的对象，如存放CRM用户的表和索引等。SYSAUX表空间一般不存储用户的数据，由Oracle系统内部自动维护。该表空间对应的数据文件为sysaux01.dbf。

（3）撤销表空间

撤销表空间（Undo Tablesapce）用于存储撤销信息的表空间。当我们对数据库表进行修改（包括INSERT,UPDATE,DELETE操作）时，Oracle系统自动使用撤销表空间来临时存放修改前的数据（Before Image）。当所做的修改操作完成并提交（Commit）后，Oracle系统可根据需要保留修改前数据时间长短来释放撤销表空间的部分空间。一般在创建Oracle实例后，Oracle系统自动创建一个名字为UNDOTBS1的撤销表空间，撤销表空间对应的数据文件为UNDOTBS01.DBF。

（4）USERS表空间

USER是Oracle建议用户使用的表空间，可以在这个表空间上创建各种对象，如创建表、索引等。Oracle的基本样例用户SCOTT的对象就存放在USERS表空间中。一般在创建Oracle实例完成后，USERS表空间对应的数据文件是USERS01.DBF。

2．数据文件

数据文件（Datafile）是用于保存用户应用数据和Oracle系统内部数据的文件。Oracle数据库由表空间组成，每个表空间可以包含一个或者多个数据文件

![image-20200901215851365](D:\data\notes\notes\sql\oracle\oracle\image-20200901215851365.png)

数据文件可以存放下面两种类型的数据:

（1）系统数据

管理用户数据和Oracle系统本身的数据，如用户建立的表的名字、列的名字及字段类型等属于用户数据，这些数据自动被存放在系统表空间对应的system01.dbf文件中；而Oracle系统内部的数据字典、表，如DBA_USERS，DBA_DATA_FILES等所存放的数据属于Oracle系统的内部数据，这些数据也存放在系统表空间对应的system01.dbf文件中。

（2）用户数据

用户数据是指用户应用系统的数据，包括与应用系统有关的所有相关信息，如某市劳动局的医疗保险系统中参保单位信息、参保职工信息、医疗支付信息等。

```sql
SQL> col file_name for a50;
SQL> set linesize 140;
SQL> select file_name, tablespace_name, bytes from dba_data_files;
FILE_NAME                                          TABLESPACE_NAME                     BYTES
-------------------------------------------------- ------------------------------ ----------
/home/oracle/app/oracle/oradata/helowin/example01.dbf EXAMPLE                         104857600
                                                                               
/home/oracle/app/oracle/oradata/helowin/users01.dbf  USERS                             5242880
                                                                                
/home/oracle/app/oracle/oradata/helowin/undotbs01.dbf UNDOTBS1                         99614720
                                                                               
/home/oracle/app/oracle/oradata/helowin/sysaux01.dbf SYSAUX                          534773760
                                                                                
/home/oracle/app/oracle/oradata/helowin/system01.dbf  SYSTEM                          713031680

```

从上面给出的图示和查询结果可以看出，表空间和数据文件是一对不可分离的数据库实体，

具有以下特点：

* 表空间是一个数据库的逻辑区；

* 每个表空间由一个或多个数据文件组成；

* 一个数据文件只能属于一个表空间。

### 临时表空间与临时文件

临时表空间（Temporary Tablespace）是Oracle系统用于存放与排序有关的特殊表空间，临时表空间相当于数据库系统的一块白板。当操作中需要进行排序时，Oracle系统就将排序的数据临时存放在该表空间内，排序处理完成后即可释放排序数据所占的空间，因此称之为临时表空间。与一般的永久表空间一样，临时表空间也对应一个或者多个临时文件（Tempfile）。

从DBA_TEMP_FILES数据字典中查询临时表空间信息

```sql
SQL> select tablespace_name, file_name from dba_temp_files;
TABLESPACE_NAME                FILE_NAME
------------------------------ --------------------------------------------------
TEMP                           /home/oracle/app/oracle/oradata/helowin/temp01.dbf

```

### 系统全局区（SGA）

系统全局区（System Global Area）主要由三部分构成，分别是数据缓冲区、日志缓冲区与共享池。

1．数据高速缓冲区（Data Buffer Cache）

数据高速缓冲区中存放着Oracle系统最近使用过的数据块（即用户的高速缓冲区），当把数据写入数据库时，它以数据块为单位进行读写；当数据高速缓冲区填满时，会自动去掉一些不常被访问的数据。如果用户要查的数据不在数据高速缓冲区，Oracle会自动从硬盘中读取。数据高速缓冲区有3种类型。

* 脏区（Dirty Buffers）：包含已经改变过并需要写回数据文件的数据块。
* 自由区（Free Buffers）：不包含任何数据，并且可以被再次写入的区，Oracle可以将从数据文件中读出的数据块存放在该区。
* 保留区（Pinned Buffers）：此区包含正在处理的或明确保留以作将来使用的区。
* Oracle将缓冲池分为3个区（使用多个缓冲池特性时）。
    * KEEP缓冲池（Keep buffer pool）：在内存中保留数据块，而且这些数据块在内存中不会被挤掉。
    * RECYCLE缓冲池：循环使用的缓冲池，表示不再需要所清除的内存块。
    * DEFAULT缓冲池：包含已经分配的块。

2．重做日志缓冲区（Redo Log Buffer）

Oracle数据库系统在处理事务时会产生日志信息，这些日志信息在记录到重做日志文件（恢复工作需要使用联机重做日志）之前，必须首先放到重做日志缓冲区（Redo Log Buffer）中。然后，在检查点发生或日志缓冲区达到一定的块数量时，由日志写入进程（LGWR）。最后将此缓冲区的内容写入重做日志文件。

3．共享池（Shared Pool）

共享池是SGA保留的区，用于存储SQL、PL/SQL存储过程、包、数据字典、锁、字符集、安全属性等。共享池包含库高速缓冲区（Library Cache）和字典高速缓冲区（Dictionary Cache）。

（1）库高速缓冲区（Library Cache），是共享池的一部分，包括：

* 共享SQL区（Shared Pool Area）
* 私有SQL区（Private Pool Area）
* PL/SQL存储过程及包（PL/SQL Procedure and Package）
* 控制结构

(2）字典高速缓冲区（Data Dictionary Cache），用于存放Oracle系统管理自身需要的所有信息，包括登录的用户名、用户对象、权限等。

4．大池（Large Pool）

在SGA中，大池是一个可选的缓冲区，管理员可以根据需要对其进行配置。大池还可以提供一个大的区以供数据库的备份与恢复之类的操作使用。

### 后台进程

Oracle后台进程是指运行于Oracle服务器端的后台程序，是Oracle实例的一部分。它们是一组分工明确、分别完成不同功能的进程。一般情况下，Oracle有如下进程。

1．数据库写入器（DBWn）

数据库写入器（Database Writer）的任务是将修改后的（在内存）数据块写回到数据库文件中。在某些比较繁忙的应用系统中，可以修改服务器参数文件SPFILE的DB_WRITER_PROCESSES参数，以允许使用多个DBWR进程，这样的DBWR进程名字分别为DBW0，DBW1，DBW2等。

2．检查点进程（CKPT）

检查点进程是一个可选进程。在数据库运行中当出现查找数据请求时，系统从数据库中找出这些数据并存入内存区，用户对数据的操作在内存中进行。当需要对修改的数据写回数据文件而产生重做日志文件的切换（Switch）时就出现校验点。DBA可以通过修改初始化参数文件SPFILE中的CHECKPOINT_PROCESS参数为TRUE来启动检查点进程。

3．日志写入器（LGWR）

日志写入器用于将SGA区中的日志信息写入到日志文件。一般是用户所做的修改先写入日志文件，等到一定时间才真正将修改结果写回到数据文件。

4．系统监控器（SMON）

系统监控器（System Monitor）是在数据库系统启动时执行恢复工作的强制性进程。比如，在并行服务器模式下（两台服务器共用一个磁盘组），SMON可以恢复另一台处于失败的数据库，使系统切换到另外一台正常的服务器上。

5．进程监控器（PMON）

进程监控器主要用于清除失效的用户进程，释放用户进程所用的资源。如，PMON将回滚未提交的工作释放锁、释放分配给失败进程的SGA资源。

6．归档器（ARCH）

可选进程，当数据库系统处于归档（ARCHIVELOG）模式时使用。当系统比较繁忙而导致LGWR进程处于等待ARCH进程时，可通过修改LOG_ARCHIVE_MAX_PROCESSES参数启动多个归档进程，从而提高归档写磁盘的速度。

7．锁（LCKn）

可选进程，并行服务器模式下可出现多个锁定进程以利于数据库通信。

8．恢复器（RECO）

分布式数据库（不同地点有不同机器和不同的Oracle系统）模式下使用的可选进程，用于数据不一致时进行恢复工作。在RECO解决恢复前，所做的修改数据的标识均标为“可疑”。

9．调度（Dnnn）

可选进程，在共享服务器模式下使用，可以启动多个调度进程。一般在网络环境具有多种协议下，每种协议至少创建一个调度进程，每个调度进程负责从所连接的用户进程到可用服务器进程的路由请求，然后把响应返回给合适的用户进程。

10．快照进程（SNPn）

快照进程处理数据库快照的自动刷新，并通过DBMS_JOB包运行预定的数据库存储过程。一般情况下，快照进程可以启动多个，在Oracle 11g版本里，可以直接在SQL>提示下用ALTER SYSTEM命令修改初始化参数JOB_QUEUE_PROCESS，动态启动多个快照进程。

11．并行查询进程（Pnnn）

可以根据数据库的活动并行查询选项的设置，Oracle服务器启动或停止并行查询进程，这些进程设计并行索引的创建、表的创建及查询。启动的数量与参数PARALLEL_MIN_SERVERS指定的数量相同，不能超出该参数指定的值。

从V$BGPROCESS数据字典中查询当前实例进程信息。

```sql
SQL> select name, description from v$bgprocess;
NAME  DESCRIPTION
----- ----------------------------------------------------------------
PMON  process cleanup
VKTM  Virtual Keeper of TiMe process
GEN0  generic0
DIAG  diagnosibility process
DBRM  DataBase Resource Manager
VKRM  Virtual sKeduler for Resource Manager
RSMN  Remote Slave Monitor
PING  interconnect latency measurement
FMON  File Mapping Monitor Process
PSP0  process spawner 0
ACMS  Atomic Controlfile to Memory Server
DSKM  slave DiSKMon process
...
VDBG  Volume Driver BG
VMB0  Volume Membership 0
ACFS  ACFS CSS
MMON  Manageability Monitor Process
MMNL  Manageability Monitor Process 2
XDMG  cell automation manager
XDWK  cell automation worker actions
295 rows selected
```

### 数据字典

#### Oracle数据字典的构成

Oracle数据字典名称由前缀和后缀组成，使用下画线“__”连接。其代表的含义如下。_

* USER__：记录用户的对象信息。_
*  ALL__：记录用户的对象信息及被授权访问的对象信息。_
* DBA__：包含数据库实例的所有对象信息。_
*  V$__：当前实例的动态视图，包含系统管理和系统优化等所使用的视图。_
* GV_：分布式环境下所有实例的动态视图，包含系统管理和系统优化使用的视图，这里的GV表示Global v$。

#### Oracle常用的数据字典

##### 1．基本的数据字典

![image-20200901222554881](D:\data\notes\notes\sql\oracle\oracle\image-20200901222554881.png)

##### 2．与数据库组建相关的数据字典

Oracle数据库管理员经常按照数据库组建对数据库进行管理，比如，了解表空间及数据文件的信息，查询DBA_DATA_FILES和DBA_TABLESPACES数据字典等。表4-2是按照数据库组建进行分类的数据字典。

![image-20200901222702885](D:\data\notes\notes\sql\oracle\oracle\image-20200901222702885.png)

![image-20200901222743594](D:\data\notes\notes\sql\oracle\oracle\image-20200901222743594.png)

##### 3. Oracle常用的动态性能视图

动态性能视图（以V$开头的数据字典）对于鉴别实例级性能问题来说是非常有用的，所有带V$的视图都可以从V$FIXED_TABLE视图中列出，而V$FIXED_TABLE视图中以X$开头的视图是可以修改的内部数据结构。所以，这些表只是在实例处在NOMOUNT或MOUNT状态时才有效。

![image-20200901222828684](D:\data\notes\notes\sql\oracle\oracle\image-20200901222828684.png)

## 数据类型

### Number类型

![image-20200902220415729](D:\data\notes\notes\sql\oracle\oracle\image-20200902220415729.png)

## 字符类型

Char类型的最大长度为32767

### 类型转换

* To_Char：可以将Number和Date类型转换为Varchar2类型。

* To_Date：可以将Char转换为Date类型。

* To_Number：可以将Char类型转换为Number类型。

## 用户管理和安全

### 创建用户

![image-20200902221104425](D:\data\notes\notes\sql\oracle\oracle\image-20200902221104425.png)

* CREATE USER username：用户名，一般为字母数字型和“#”及“_”符号；_
* IDENTIFIED BY password：用户口令，一般为字母数字型和“#”及“_”符号；
* IDENTIFIED EXETERNALLY：表示用户名在操作系统下验证，该用户名必须与操作系统中所定义的用户名相同；
* IDENTIFIED GLOBALLY AS 'CN=user'：用户名由Oracle安全域中心服务器验证，CN名字表示用户的外部名；
* [DEFAULT TABLESPACE tablespace]：默认的表空间；
* [TEMPORARY TABLESPACE tablespace]：默认的临时表空间；
* [QUOTA [integer K[M] ] [UNLIMITED] ] ON tablespace：用户可以使用的表空间的字节数；
* [PROFILES profile_name]：资源文件的名称；
* [PASSWORD EXPIRE]：立即将口令设成过期状态，用户再登录前必须修改口令；
* [ACCOUNT LOCK or ACCOUNT UNLOCK]：用户是否被加锁，在默认情况下是不加锁的。

创建用户语句

```sql
CREATE USER wang IDENTIFIED BY wbtest DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE temp;
```

（2）创建用户，并配置磁盘限额。创建一个用户名为wbtest，口令为wbtest，默认表空间为users，临时表空间为TEMP的用户，并且不允许该用户使用SYSTEM表空间。

```sql
CREATE USER webtest IDENTIFIED BY wbtest DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE temp QUOTA 0 ON SYSTEM;
```

3）配置用户在指定表空间上不受限制。创建一个用户名为test，口令为test，默认表空间为users，并且该用户使用users表空间不受限制。

```sql
CREATE USER test IDENTIFIED BY test DEFAULT TABLESPACE USERS  QUOTA unlimited ON USERS;
```

在创建用户时，以下几点特别需要特别注意。

* 初始建立的数据库用户没有任何权限，不能执行任何数据库操作。
* 如果建立用户时不指定DEFAULT TABLESPACE子句，Oracle会将SYSTEM表空间作为用户默认表空间。 
* 如果建立用户时不指定TEMPORARY TABLESPACE子句，Oracle会将数据库默认临时表空间作为用户的临时表空间。
*  如果建立用户时没有为表空间指定QUOTA子句，那么用户在特定表空间上的配额为0，用户将不能在相应表空间上建立数据对象。
*  初始建立的用户没有任何权限，所以为了使用户可以连接到数据库，必须授权其CREATE SESSION权限

### 修改用户语法与实例

![image-20200902222243929](D:\data\notes\notes\sql\oracle\oracle\image-20200902222243929.png)

### 查询Oracle系统中被锁住的用户信息。

```sql
SELECT username, account_status, lock_date FROM dba_users;
```

### 权限数据字典

![image-20200902223017731](D:\data\notes\notes\sql\oracle\oracle\image-20200902223017731.png)

## 性能优化

### Oracle动态视图

Oracle最重要的9个动态性能视图如下：

* v$session + v$session_wait
* v$process
* v$sql
* v$sqltext
* v$bh
* v$lock
* v$latch_children
* v$sysstat
* v$system_event

1．计数和累计视图

* v$sysstat
* v$system_event
* v$parameter

2．与Oracle会话有关的视图

* v$process
* v$session
* v$session_wait
* v$session_event
* v$sesstat

3．与SQL有关的视图

* v$sql◎ v$sqlarea
* v$SQL_PLAN
* V$SQL_PLAN_STATISTICS
* v$sqltext_with_newlines

4．与Oracle系统竞争有关的视图

* v$latch
* v$latch_children
* v$latch_holder
* v$lock
* V$ENQUEUE_STAT
* V$ENQUEUE_LOCK

5．与Oracle磁盘I/O有关的视图

* v$segstat
* v$filestat
* v$tempstat
* v$datafile
* v$tempfile

6．与Oracle内存有关的视图

* v$Librarycache
* v$rowcache
* v$ksmsp

7．与Oracle实例有关的视图

* v$db_cache_advice
* v$PGA_TARGET_ADVICE
* v$SHARED_POOL_ADVICE

![image-20200903210401506](D:\data\notes\notes\sql\oracle\oracle\image-20200903210401506.png)