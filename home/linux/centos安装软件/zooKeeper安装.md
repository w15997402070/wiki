1.下载zookeeper zookeeper-3.5.1-alpha.tar.gz 复制到 /usr/local/ 目录下
2.tar -xvf zookeeper-3.5.1-alpha.tar.gz
3.进入/usr/local/zookeeper-3.5.1-alpha/conf
  复制文件:cp zoo_sample.cfg zoo.cfg
4.启动 # cd /usr/local/zookeeper-3.5.1-alpha/bin
      # ./zkServer.sh start
5.关闭 ./zkServer.sh stop
6.检查可以用 jps
