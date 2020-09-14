1. rpm安装
  到官网下载(https://pkg.jenkins.io/redhat-stable/) rpm安装包
2. 安装rpm包
    执行命令 rpm -ivh enkins-2.9-1.1.noarch.rpm
3. 进入jenkins的系统配置文件并修改相关端口号（也可以不修改）
     jenkins的默认JENKINS_PORT是8080，JENKINS_AJP_PORT默认端口是8009，这同tomcat的默认端口冲突。我这更改为8088和8089。

     vi /etc/sysconfig/jenkins
![](assets/markdown-img-paste-20171124144958544.png)
![](assets/markdown-img-paste-20171124145009578.png)
4. 检查jenkins是否配置jdk，在candidates后面加上自己的jdk路径，如下：

          vi /etc/rc.d/init.d/jenkins
![](assets/markdown-img-paste-20171124145034375.png)
5. 启动jenkins
    ```
    service jenkins start
    ```
6. 启动成功后，就可以访问了，访问地址如下：192.168.0.104:8080，如下：


wang: 7a4c161a7dc4e569505d6074185fe85be2cbf6bc

mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=7a4c161a7dc4e569505d6074185fe85be2cbf6bc
