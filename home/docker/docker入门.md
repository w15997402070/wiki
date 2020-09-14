# Docker

[toc]

## Docker相关知识

* Docker client
* Docker守护进程（Docker daemon) ->daemon的主要功能包括镜像管理、镜像构建、REST API、身份验证、安全、核心网络以及编排
* containerd -> 容器的生命周期管理——start | stop | pause | rm....
* runc ->创建容器

## Docker命令

### Docker 系统命令

* `service docker restart`重启docker

### Docker镜像命令

* `docker image ls`查看镜像 
* `docker image build -t web:latest .` 运行Dockerfile文件
* `docker image inspect web:latest`查看配置
* `docker image history`来查看在构建镜像的过程中都执行了哪些指令
* `docker pull ubuntu:latest`拉取镜像
* `docker search ubuntu`查找镜像

### Docker容器命令

* `sudo docker container help`查看container命令帮助

*  `sudo docker container run -it ubuntu:18.04 /bin/bash`启动 Ubuntu镜像

  * 命令的基础格式为`docker container run <options> <im- age>:<tag> <app>`

* ``ctrl +P + Q `组合键退出当前容器但是不关闭容器

* 重新连接容器

    ```bash
    $ sudo docker container exec -it voltainer sh
    ```

* `sudo docker container ls`查看当前正在运行的容器

* `sudo docker container ls -a`查看所有的容器

* `sudo docker container stop cbedd0a593a1`停止容器命令

* `docker start mysql-test`启动容器

* `sudo docker container rm cbedd0a593a1`删除指定容器加`-f`参数可以删除正在运行的container

* `sudo docker rm $(sudo docker ps -aq)`删除所有的容器

* `sudo docker exec -it cbedd0a593a1  bash`重新回到容器运行的命令行中`cbedd0a593a1`是上面命令查看到的`CONTAINER ID`.注意 :` docker container exec`命令创建了新的Bash,Shell提示符切换到了容器,但是再次运行ps命令，会看到两个Bash.输入exit退出容器，并通过命令`docker container ps`来确认容器依然在运行中。果然容器还在运行。

* 查看容器内容

  ```bash
  $ sudo docker container exec -it voltainer sh
  
  / # echo "I promise to write a review of the book " > /vol/file1
  / # ls -l /vol
  total 4
  -rw-r--r--    1 root     root            41 Mar 28 11:23 file1
  / # vat /vol/file1
  sh: vat: not found
  / # cat /vol/file1
  I promise to write a review of the book
  ```
  
* 查看容器日志`sudo docker logs c2`

*   `exit`:退出当前容器命令行,但是不停止容器

### Docker网络命令

* `sudo docker network ls`查看网络

* `docker network inspect bridge`

* ```bash
  $ sudo docker network inspect bridge | grep bridge.name
              "com.docker.network.bridge.name": "docker0",
  
  # 查看ip地址
># docker network inspect bridge
  
  
  ```
  
* `sudo docker network create -d bridge localnet`创建新的单机桥接网络，名为“localnet”

* 删除Docker主机上全部未使用的网络`docker network prune`

* 删除Docker主机上指定网络`docker network rm`

### Docker存储卷命令

* 创建名为myvol的新卷`$ sudo docker volume create myvol`

* 查看创建的卷`$ sudo docker volume ls`

* 查看卷详情`$ sudo docker volume inspect myvol`

* 删除Docker卷`docker volume prune`会删除未装入到某个容器或者服务的所有卷;`docker volume rm`允许删除指定卷

* 创建一个容器,并且挂载一个名为bizvol的卷

  ```bash
  $ sudo docker container run -dit --name voltainer \
  > --mount source=bizvol,target=/vol \
  > alpine
  ```




### Docker其他命令

* `docker ps`查询当前进程

### Docker命令实践

* 创建一个新的容器，并接入到新建桥接网络localnet当中
    ```bash
    >$ sudo docker container run -d --name c1 \
    > --network localnet \
    > alpine sleep 1d

    # 创建第二个容器
    >$ sudo docker container run -it --name c2 \
    > --network localnet \
    > alpine sh
    
    ```

* 创建一个nginx web 容器

  ```bash
  > $ sudo docker container run -d --name \
  > web --network localnet \
  > --publish 5000:80 \
  > nginx
  
  ~$ sudo docker port web
  80/tcp -> 0.0.0.0:5000
  # 这表示容器80端口已经映射到Docker主机所有接口上的5000端口
  ```

* docker安装WordPress

    ```bash
    $ sudo docker run --name db --env MYSQL_ROOT_PASSWORD=example -d mariadb
    
    $ sudo docker run --name MyWordPress --link db:mysql -p 8080:80 -d wordpress
    ```


##  docker-compose

修改daemon配置文件`/etc/docker/daemon.json`来使用加速器

```json
{
  "registry-mirrors": "https://docker.mirrors.ustc.edu.cn"
}
```

重启

```shell
sudo systemctl daemon-reload
sudo systemctl restart docker
```

安装

```bash
> sudo curl -L "https://get.daocloud.io/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
> chmod +x /usr/local/bin/docker-compose
```

查看安装是否成功

```bash
> sudo docker-compose --version
docker-compose version 1.22.0, build f46880fe
```



## Docker Swarm

Docker Swarm包含两方面：一个企业级的Docker安全集群，以及一个微服务应用编排引擎

集群方面，Swarm将一个或多个Docker节点组织起来，使得用户能够以集群方式管理它们。Swarm默认内置有加密的分布式集群存储（encrypted distributed cluster store）、加密网络（Encrypted Network）、公用TLS（Mutual TLS）、安全集群接入令牌Secure Cluster JoinToken）以及一套简化数字证书管理的PKI（Public Key Infrastructure）

编排方面，Swarm提供了一套丰富的API使得部署和管理复杂的微服务应用变得易如反掌。通过将应用定义在声明式配置文件中，就可以使用原生的Docker命令完成部署。此外，甚至还可以执行滚动升级、回滚以及扩缩容操作，同样基于简单的命令即可完成

## Docker 网络

Docker网络架构由3个主要部分构成：容器网络模型（CNM）、Libnetwork和驱动

Libnetwork是Docker对CNM的一种实现，提供了Docker核心网络架构的全部功能。不同的驱动可以通过插拔的方式接入Libnetwork来提供定制化的网络拓扑。

```bash
~$ sudo docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
d755bb72ead9        bridge              bridge              local
de1b99a6ca23        host                host                local

```

