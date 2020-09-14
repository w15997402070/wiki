# Docker安装软件

[toc]

## Mysql

下载mysql5.7的docker镜像：

``` shell
$ docker pull mysql:5.7
```

docker命令启动：

```shell
$ docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root  \
-d mysql:5.7

-- docker安装mysql8
docker run -p 3306:3306 --name mysql8 -v /mydata/mysql/log:/var/log/mysql -v /mydata/mysql/data:/var/lib/mysql -v /mydata/mysql/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=123456  -d mysql
```

参数说明

- -p 3306:3306：将容器的3306端口映射到主机的3306端口
- -v /mydata/mysql/conf:/etc/mysql：将配置文件夹挂在到主机
- -v /mydata/mysql/log:/var/log/mysql：将日志文件夹挂载到主机
- -v /mydata/mysql/data:/var/lib/mysql/：将数据文件夹挂载到主机
- -e MYSQL_ROOT_PASSWORD=123456：初始化root用户的密码

进入运行mysql的docker容器：

```shell
$ docker exec -it mysql /bin/bash
```

使用mysql命令打开客户端：

```shell
mysql -uroot -p123456 --default-character-set=utf8

-- mysql8
> use mysql;
> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';


```

## Redis

下载redis3.2的docker镜像：

```shell
docker pull redis:3.2
```

使用docker命令启动：

```shell
docker run -p 6379:6379 --name redis \
-v /mydata/redis/data:/data \
-d redis:3.2 redis-server --appendonly yes
```

进入redis容器使用redis-cli命令进行连接：

```shell
docker exec -it redis redis-cli


$ docker exec -it redis redis-cli
127.0.0.1:6379> set a 100
OK
127.0.0.1:6379> get a
"100"
127.0.0.1:6379>

```

## zookeeper

下载镜像:

```shell
docker pull zookeeper
```

启动容器

```shell
docker run --privileged=true -d --name zookeeper --publish 2181:2181  -d zookeeper:latest
```



## Nginx

下载nginx1.10的docker镜像：

```shell
docker pull nginx:1.10
```

先运行一次容器（为了拷贝配置文件）：

```shell
docker run -p 80:80 --name nginx \
-v /mydata/nginx/html:/usr/share/nginx/html \
-v /mydata/nginx/logs:/var/log/nginx  \
-d nginx:1.10
```





## RabbitMQ

下载rabbitmq3.7.15的docker镜像：

```shell
docker pull rabbitmq:3.7.15
```

使用docker命令启动：

```shell
docker run -d --name rabbitmq \
--publish 5671:5671 --publish 5672:5672 --publish 4369:4369 \
--publish 25672:25672 --publish 15671:15671 --publish 15672:15672 \
rabbitmq:3.7.15
```

进入容器并开启管理功能：

```shell
docker exec -it rabbitmq /bin/bash
rabbitmq-plugins enable rabbitmq_management
```

开启防火墙：

```shell
firewall-cmd --zone=public --add-port=15672/tcp --permanent
firewall-cmd --reload
```

- 访问地址查看是否安装成功：http://192.168.3.101:15672/ ![img](http://www.macrozheng.com/images/refer_screen_76.png)
- 输入账号密码并登录：guest guest
- 创建帐号并设置其角色为管理员：mall mall

![img](http://www.macrozheng.com/images/refer_screen_77.png)

创建一个新的虚拟host为：/mall

![img](http://www.macrozheng.com/images/refer_screen_78.png)

点击mall用户进入用户配置页面

![img](http://www.macrozheng.com/images/refer_screen_79.png)

给mall用户配置该虚拟host的权限

![img](http://www.macrozheng.com/images/refer_screen_80.png)

## Elasticsearch

下载elasticsearch6.4.0的docker镜像：

```shell
docker pull elasticsearch:6.4.0
```

修改虚拟内存区域大小，否则会因为过小而无法启动:

```shell
sysctl -w vm.max_map_count=262144
```

使用docker命令启动：

```shell
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \
-e "discovery.type=single-node" \
-e "cluster.name=elasticsearch" \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-d elasticsearch:6.4.0
```

启动时会发现/usr/share/elasticsearch/data目录没有访问权限，只需要修改/mydata/elasticsearch/data目录的权限，再重新启动。

```shell
chmod 777 /mydata/elasticsearch/data/
```

安装中文分词器IKAnalyzer，并重新启动：

```shell
docker exec -it elasticsearch /bin/bash
#此命令需要在容器中运行
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.4.0/elasticsearch-analysis-ik-6.4.0.zip
docker restart elasticsearch
```

开启防火墙：

```shell
firewall-cmd --zone=public --add-port=9200/tcp --permanent
firewall-cmd --reload
```

## kibana

下载kibana6.4.0的docker镜像：

```shell
docker pull kibana:6.4.0
```

使用docker命令启动：

```shell
docker run --name kibana -p 5601:5601 \
--link elasticsearch:es \
-e "elasticsearch.hosts=http://es:9200" \
-d kibana:6.4.0
```

开启防火墙：

```shell
firewall-cmd --zone=public --add-port=5601/tcp --permanent
firewall-cmd --reload
```

访问地址进行测试：http://localhost:5601

![image-20200502212540800](D:\data\notes\notes\docker\docker安装软件\image-20200502212540800.png)

## logstash

下载logstash:6.4.0镜像

```sh
$ docker pull logstash:6.4.0
# 创建容器
$ docker run --rm -di  logstash:6.4.0
# 查看容器id
$ docker ps
eebb161855473872e74
# 创建用于存放配置的目录
mkdir -p /mydata/logstash
# 复制配置文件，冒号前面为容器ID
docker cp eebb161855473872e74:/usr/share/logstash/config /mydata/logstash
# 复制logstash管道文件
docker cp eebb161855473872e74:/usr/share/logstash/pipeline /mydata/logstash

# 停止容器
$ docker stop eebb161855473872e74
#删除容器
$ docker rm eebb161855473872e74

#创建并挂载容器
$ docker run -d -it --privileged=true  --name=logstash -p 5047:5047 -p 9600:9600 --link elasticsearch:elasticsearch -e "elasticsearch.hosts=http://elasticsearch:9200" -v /mydata/logstash/pipeline/:/usr/share/logstash/pipeline/ -v /mydata/logstash/config/:/usr/share/logstash/config/ logstash:6.4.0


# 查看日志 -f查看滚动日志
$ docker logs logstash -f

# 上传要导入的文件到logstash容器中
$ docker cp /mydata/data/movies.csv logstash:/usr/share/logstash/
# 复制配置文件到挂载的Pipeline文件中
$ cp /mydata/data/logstash-csv.conf /mydata/logstash/pipeline/

```

编写`/mydata/data/logstash-csv.conf`配置文件

```conf
input {
  file {
    path => "/usr/share/logstash/movies.csv"
        start_position => "beginning"
        sincedb_path => "/dev/null"
  }
}

filter {
    csv {
        separator => ","
        columns => ["id","content","genre"]
    }
    
    mutate {
        split => { "genre" => "|" }
        remove_field => ["path", "host","@timestamp","message"]
    }
    
    mutate {
        split => ["content", "("]
        add_field => { "title" => "%{[content][0]}"}
        add_field => { "year" => "%{[content][1]}"}
    }

    mutate {
        convert => {
            "year" => "integer"
        }
        strip => ["title"]
        remove_field => ["path", "host","@timestamp","message","content"]
    }
}

output {
  elasticsearch {
    hosts => "http://elasticsearch:9200"
        index => "movies"
        document_id => "%{id}"
  }
    stdout {}
}
```

重启`logstash`容器

```sh
# 重启容器
docker restart logstash

# 查看日志
docker logs logstash -f
```

## Mongodb

下载mongo3.2的docker镜像：

```shell
$ docker pull mongo:3.2
```

使用docker命令启动：

```shell
docker run -p 27017:27017 --name mongo \
-v /mydata/mongo/db:/data/db \
-d mongo:3.2
```

## Oracle

下载镜像:

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
```

创建容器:

```sh
docker run -d -p 1521:1521 -v /mydata/oracle:/data/oracle --name oracle11 registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g
```

进入容器:

```sh
docker exec -it oracle11 /bin/bash
```

进行软连接

```sh
 sqlplus /nolog
```

会报没有这个命令

切换root用户

```sh
su root
password ;
# 输入密码 : helowin
```

编辑profile文件配置ORACLE环境变量		

打开：`vi /etc/profile` ，在文件最后写上下面内容：

```profile
        export ORACLE_HOME=/home/oracle/app/oracle/product/11.2.0/dbhome_2

        export ORACLE_SID=helowin

        export PATH=$ORACLE_HOME/bin:$PATH
```
保存后执行`source /etc/profile` 加载环境变量；

创建软连接

​      `ln -s $ORACLE_HOME/bin/sqlplus /usr/bin`

切换到oracle 用户

```sh
su - oracle 
# 一定要加中间的 - ,不然会报错
```

登录sqlplus并修改sys、system用户密码

```sql
sqlplus /nolog   --登录
conn /as sysdba  --
alter user system identified by system;--修改system用户账号密码；
alter user sys identified by system;--修改sys用户账号密码；
create user test identified by test; -- 创建内部管理员账号密码；
grant connect,resource,dba to test; --将dba权限授权给内部管理员账号和密码；
ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED; --修改密码规则策略为密码永不过期；（会出现坑，后面讲解）
alter system set processes=1000 scope=spfile; --修改数据库最大连接数据；
```
修改以上信息后，需要重新启动数据库；

```sql
conn /as sysdba
shutdown immediate; --关闭数据库
startup; --启动数据库
exit：-- 退出软链接
```

`docker进入oracle账户,默认密码好像就是oracle`



