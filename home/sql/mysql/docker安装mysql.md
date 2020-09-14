# Docker安装MySQL





```sh
# 查找docker镜像
$ docker search mysql
# 拉取docker最新镜像
$ docker pull mysql:latest
# 启动docker
$ docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql

# 挂载配置文件启动方式
# docker run -p 3306:3306 --name mysql -v /opt/docker_v/mysql/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWOR
# -v /opt/docker_v/mysql/conf:/etc/mysql/conf.d：将主机/opt/docker_v/mysql/conf目录挂载到容器的/etc/mysql/conf.d

# -p 3306:3306 ：映射容器服务的 3306 端口到宿主机的 3306 端口，外部主机可以直接通过 宿主机ip:3306 访问到 MySQL 的服务。
# MYSQL_ROOT_PASSWORD=123456：设置 MySQL 服务 root 用户的密码。

#进入容器
$ docker exec -it mysql bash
# 进入mysql命令行
$ mysql -uroot -p123456



```

