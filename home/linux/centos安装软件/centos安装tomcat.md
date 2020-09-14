安装Tomcat8

这里采用离线解压tar.gz的方式安装

下载：

wget http://mirror.bit.edu.cn/apache/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz


解压：

    # tar -zxvf apache-tomcat-8.0.33.tar.gz -C /usr/java

启动Tomcat：

    # cd /usr/java/apache-tomcat-8.0.33/bin/
    # ./startup.sh

将8080端口添加到防火墙例外并重启

    # firewall-cmd --zone=public --add-port=8080/tcp --permanent
    # firewall-cmd --reload

验证是否成功 :
![](assets/markdown-img-paste-20171027175405301.png)
