# linux命令

[toc]

## PATH环境变量

PATH环境变量 : 定义的是系统搜索命令的路径

```shell
[root@localhost ~]# echo $PATH  
/usr/lib/qt-3.3/bin:
/usr/local/node/0.10.24/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/usr/java/jdk1.8.0_121/bin:/usr/local/tomcat/bin:/usr/local/tomcat2/bin:/usr/local/zookeeper-3.5.1/bin:/usr/local/hadoop/bin:/usr/local/apache-hive-2.3.4/bin:/usr/local/sqoop-1.4.7/bin:/usr/local/hbase-2.1.4/bin:/root/bin:/sbin:/usr/bin:/usr/sbin:/root/apache-maven-3.3.9/bin
```

## 1.查看网络端口信息 :

    [root@admin ~]# netstat -ntpl

## 2.查询进程

    [root@admin ~]# ps -ef | grep redis
    root      3986     1  0 17:18 ?        00:00:12 ./redis-server*:6379  *
    root      9318  9278  1 20:42 pts/12   00:00:00 grep redis

## 3.杀死进程

    [root@admin ~]# kill -9 3986

## 4.文件搜索命令

-- locate 文件名  
locate 是在后台数据库中按文件名搜索,搜索速度更快  
`/var/lib/mlocate`  
locate 命令搜索的后台数据库

    [root@localhost ~]# locate install.log  
    locate: can not stat () `/var/lib/mlocate/mlocate.db': 没有那个文件或目录  
报错时需要先执行  更新数据库  

    [root@localhost ~]# updatedb

*locate命令搜索遵循下面的规则* 在/etc/updatedb.conf

```conf
# yes开启搜索限制,就是遵循下面的搜索规则,no就是不遵守下面的规则
PRUNE_BIND_MOUNTS = "yes"

# 搜索时,不搜索的文件系统
PRUNEFS = "9p afs anon_inodefs auto autofs bdev binfmt_misc cgroup cifs coda configfs cpuset debugfs devpts ecryptfs exofs fuse fusectl gfs gfs2 gpfs hugetlbfs inotifyfs iso9660 jffs2 lustre mqueue ncpfs nfs nfs4 nfsd pipefs proc ramfs rootfs rpc_pipefs securityfs selinuxfs sfs sockfs sysfs tmpfs ubifs udf usbfs vmhgfs"

# 搜索时不搜索的文件类型
PRUNENAMES = ".git .hg .svn"

# 搜索时不搜索的路径
PRUNEPATHS = "/afs /media /net /sfs /tmp /udev /var/cache/ccache /var/spool/cups /var/spool/squid /var/tmp"
```

## 5. 文件搜索命令find

    find [搜索范围] [搜索条件]
    
    [root@localhost ~]# find / -name install.log  
    /root/install.log   -- 搜到的是完全匹配的  
 避免大范围搜索,会非常耗费系统资源  
 find是在系统中搜索符号条件的文件名.如果需要匹配,使用通配符匹配,通配符是完全匹配  
使用通配符搜索  

    [root@localhost ~]# find /root -name "install.log*"  
    /root/install.log.syslog  --搜索的是模糊匹配的
    /root/install.log

查找10天前修改的文件

    [root@localhost ~]# find /var/log/ -mtime +10  
    /var/log/dracut.log  
    /var/log/spice-vdagent.log  
-10 10天内修改的文件  
10  10天当天修改的文件  
+10 10天前修改的文件

atime  文件访问的时间  
ctime  改变文件属性  
mtime  修改文件内容

查找当前目录下文件大小是25Kb的文件

    [root@localhost usr]# find . -size 25k  
    ./bin/gvfs-mount  
    ./bin/whiptail  
    ./bin/xdg-settings  

查找 当前目录下 大于20k小于50k的文件  
-a and 逻辑与,两个条件都满足  
-o or 逻辑或,两个条件满足一个即可  

    [root@localhost tomcat]# find . -size +20k -a -size -50k  
    ./bin/tomcat-juli.jar  
    ./bin/catalina.sh  
    ./bin/commons-daemon.jar  

查找当前目录下大于25k并且小于30k的文件,并显示详细信息

    -exec     ls -lh {}\;  
    -exec/-ok 命令   {}\;对搜索结果执行操作
    
    [root@localhost tomcat]# find . -size +25k -a -size -30k -exec ls -lh {} \;  
    -rw-r-----. 1 root root 29K 4月  16 2017 ./webapps2/docs/security-manager-howto.html

## 6. 字符串搜索命令grep

    grep [选项] 字符串 文件名  
在文件当中匹配符号条件的字符串  
选项 :  
-i 忽略大小写  
-v 排除指定的字段

## 7. 命令搜索命令whereis和which

whereis只能查启动命令  

    [root@localhost ~]# whereis hadoop  
    hadoop: /usr/local/hadoop  
    [root@localhost ~]# whereis ls  
    ls: /bin/ls /usr/share/man/man1p/ls.1p.gz /usr/share/man/man1/ls.1.gz  

which可以看别名的命令  

    [root@localhost ~]# which ll  
    alias ll='ls -l --color=auto'
        /bin/ls

## 8. 压缩命令

### .zip格式压缩

压缩文件

- zip 压缩文件名 源文件

压缩文件夹

- zip -r 压缩文件名 源目录

.zip格式解压缩

- unzip 压缩文件

### .gz格式压缩

压缩为.gz格式的压缩文件,源文件会消失

- gzip 源文件

压缩为.gz格式,源文件保留

- gzip -c 源文件 > 压缩文件

压缩目录下的所有的子文件,但是不能压缩目录

- gzip -r 目录

解压缩文件

- gzip -d 压缩文件
- gunzip 压缩文件

### 打包命令 tar

- tar -cvf 打包文件名 源文件  
-c : 打包  
-v : 显示过程  
-f : 指定打包后的文件名

- tar -xvf 打包文件名  
-x : 解打包

### .tar.gz格式

其实.tar.gz格式是先打包为.tar格式,再压缩为.gz格式

- tar -zcvf 压缩包名为.tar.gz 源文件  
-z : 压缩为.tar.gz格式

- tar -zxvf 压缩包名.tar.gz  
-x : 解压缩包名为.tar.gz格式

### .tar.bz2格式

其实.tar.gz格式是先打包为.tar格式,再压缩为.bz2格式

- tar -jcvf 压缩包名为.tar.bz2 源文件  
-z : 压缩为.tar.bz2格式

- tar -jxvf 压缩包名.tar.bz2  
-x : 解压缩包名为.tar.bz2格式

## 9. 关机命令

### shutdown 命令

    [root@localhost local]# shutdown [选项] 时间
    选项 :
    -c : 取消前一个关机命令
    -h : 关机
    -r : 重启



## 10, 复制命令

`linux命令/cp命令`

## w  查看当前连接

    [root@localhost local]# w
     18:41:16 up 1 day,  6:30,  3 users,  load average: 0.08, 0.02, 0.01
    USER     TTY      FROM              LOGIN@   IDLE   JCPU   PCPU WHAT
    root     tty1     :0               Fri21   45:22m 14.78s 14.78s /usr/bin/Xorg :0 -br -verbose -audit 4 -auth /var/run/gdm/auth-for-gdm-wjZOut/database -nolist
    root     pts/0    192.168.184.1    15:55    2.00s  1.29s  0.80s w
    root     pts/1    192.168.184.1    15:58    2:40m  0.11s  0.11s -bash

## last 系统中所有登录的信息

last命令实际是查看 /var/log/wtmp 文件,这个文件是二进制的乱码,所以只能用这个命令看

    [root@localhost local]# last
    root     pts/1        192.168.184.1    Sun May 26 15:58   still logged in
    root     pts/0        192.168.184.1    Sun May 26 15:55   still logged in
    root     pts/2        192.168.184.1    Sat May 25 13:49 - 23:21  (09:31)
    root     pts/1        192.168.184.1    Sat May 25 13:36 - 23:21  (09:44)

## linux中的通配符

    *  匹配任意内容  
    ?    匹配一个任意的字符  
    []   匹配任意一个中括号内的字符
    [-]  匹配任意一个字符,-代表范围 [a-z]代表匹配一个小写字母
    [^]  匹配不是中括号内的一个字符,[^0-9] 代表匹配一个不是数字的字符

## 常用快捷键

    ctrl+c  强制终止当前命令
    ctrl+l  清屏
    ctrl+a  光标移动到命令行首
    ctrl+e  光标移动到命令行尾
    ctrl+u  从光标位置删除到行首
    ctrl+z  把命令放置到后台
    ctrl+r  在历史命令中搜索

## 输出重定向

 类型 | 符号 | 作用
 :---- |:------:|:-----:
标准输出重定向 | 命令 > 文件 | 以`覆盖`的方式把命令的`正确输出`输出到指定的文件当中
标准输出重定向 | 命令 >> 文件 | 以`追加`的方式,把命令的`正确输出`输出到指定的文件中
标准输出重定向 | 错误命令 2> 文件 | 以`覆盖`的方式,把命令的`错误输出`输出到指定的文件中
标准输出重定向 | 错误命令 2>> 文件 | 以`追加`的方式,把命令的`错误输出`输出到指定的文件中
正确输出和错误输出同时保存 | 命令 > 文件 2>&1 | 以`覆盖`的方式,把`正确输出和错误输出`都保存到同一个文件中
正确输出和错误输出同时保存 | 命令 >> 文件 2>&1 | 以`追加`的方式,把`正确输出和错误输出`都保存到同一个文件中
正确输出和错误输出同时保存 | 命令 &> 文件  | 以`覆盖`的方式,把`正确输出和错误输出`都保存到同一个文件中
正确输出和错误输出同时保存 | 命令 &>> 文件| 以`追加`的方式,把`正确输出和错误输出`都保存到同一个文件中
正确输出和错误输出同时保存 | 命令 >> 文件1 2>>文件2 | 把`正确的输出`追加到`文件1`中,把`错误的输出`追加到`文件2`中
冒号表示对齐

## 输入从定向

    查看当前运行级别
    [root@localhost local]# wc [选项] [文件]
    选项 :
    -c 统计字节数
    -w 统计单词数
    -l 统计行数

## 管道符

- 多命令顺序执行

多命令执行符 | 格式 | 作用 |
:---------: | :----: | :----|
:           | 命令1 : 命令2 | 多个命令顺序执行,命令之间没有任何逻辑
&&          | 命令1 && 命令2 | 逻辑与,命令1执行正确命令2才会执行
\|\|          | 命令1 \|\| 命令2 | 逻辑或,命令1执行不正确,命令2才会执行;命令1执行正确,命令2不会执行

- 管道符  

命令1的正确输出作为命令2的操作对象

    [root@localhost local]# 命令1 | 命令2
    [root@localhost local]# ls -l | more

## 系统运行级别

    查看当前运行级别
    [root@localhost local]# runlevel
    N 5
    
    0 关机   -- init 0  就是关机
    1 单用户
    2 不完全多用户,不含NFS服务
    3 完全多用户
    4 未分配
    5 图形界面
    6 重启