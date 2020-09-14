1.下载 :[root@admin ~]#  wget http://download.redis.io/releases/redis-3.0.1.tar.gz
2.解压 : [root@admin ~]# tar -xvf redis-3.0.1.tar.gz
3.编译和安装: [root@admin ~]# cd /usr/local/redis-3.0.1
             [root@admin redis-3.0.1]# make
             [root@admin redis-3.0.1]# make insatll
4.查看版本 : [root@admin redis-3.0.1]# redis-server -v
5.复制配置文件到/etc 目录下 [root@admin redis-3.0.1]# cp redis.conf /etc/
6.启动  [root@admin redis-3.0.1]# redis-server /etc/redis.conf
