一.安装cronolog-1.6.2 

1、下载（最新版本）
#  wget http://cronolog.org/download/cronolog-1.6.2.tar.gz
2、解压缩
# tar zxvf cronolog-1.6.2.tar.gz
3、进入cronolog安装文件所在目录
# cd cronolog-1.6.2
4、运行安装
# ./configure
    如果出现如下错误：
    checking for working makeinfo... missing
    checking for gcc... no
    checking for cc... no
    运行如下命令安装缺少的支持软件
    yum install -y gcc glibc
# make
# make install

5、查看cronolog安装后所在目录（验证安装是否成功）
# which cronolog

二.修改tomcat配置


3：修改Tomcat的catalina.sh文件



（1）第一处





将

```shell
if [ -z "$CATALINA_OUT" ] ; then  
 CATALINA_OUT="$CATALINA_BASE"/logs/catalina.out  
fi  
```
![](2018-03-28-15-31-24.png) 

修改为
```shell
if [ -z "$CATALINA_OUT" ] ; then  
 CATALINA_OUT="$CATALINA_BASE"/logs/catalina.%Y-%m-%d.out  
fi  
```

(2)第二处



注释 touch “$CATALINA_OUT”，约370行

```
# touch "$CATALINA_OUT"  
```
![](2018-03-28-15-34-18.png)

(3)第三处



将

```
>> "$CATALINA_OUT" 2>&1 "&"  
```

```
2>&1 |/usr/sbin/cronolog "$CATALINA_OUT" &  
```



4：重启tomcat 切换到tomcat日志目录下看重新生成的日志