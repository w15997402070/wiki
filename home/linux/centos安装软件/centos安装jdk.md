- 查看已安装的jdk :

      # rpm -qa | grep Java

 ![](assets/markdown-img-paste-20171027170936991.png)

- 卸载jdk :  rpm -e --nodeps   后面跟系统自带的jdk名    这个命令来删除系统自带的jdk，
```
  # rpm -e --nodeps java-1.8.0-openjdk-1.8.0.102-4.b14.el7.x86_64
  # rpm -e --nodeps java-1.8.0-openjdk-headless-1.8.0.102-4.b14.el7.x86_64
  # rpm -e --nodeps java-1.7.0-openjdk-headless-1.7.0.111-2.6.7.8.el7.x86_64
  # rpm -e --nodeps java-1.7.0-openjdk-1.7.0.111-2.6.7.8.el7.x86_64
```
![](assets/markdown-img-paste-20171027170947348.png)

- 删完之后可以再通过    rpm -qa | grep Java  命令来查询出是否删除掉
```
# rpm -qa | grep Java
```
![](assets/markdown-img-paste-20171027170954844.png)
- 然后在/usr文件夹下创建java文件夹 : mkdir java
```
# mkdir java
```
- 进入java文件夹上传jdk-8u152-linux-x64.tar.gz  : rz命令上传

      # rz

- 解压缩文件 : tar -zxvf jdk-8u152-linux-x64.tar.gz
```
# tar -zxvf jdk-8u152-linux-x64.tar.gz
```
安装完毕之后在/etc/profile文件末尾添加

- 编辑配置文件
```
# vim /etc/profile
export JAVA_HOME=/usr/java/jdk1.8.0_152
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH
```
- 使/etc/profile生效
```
# source /etc/profile
```

- 检测安装是否成功
```
# java -version
```

![](assets/markdown-img-paste-20171027171846471.png)




### windows :
    32为jdk下载地址 :http://download.oracle.com/otn-pub/java/jdk/8u152-b16/aa0333dd3019491ca4f6ddbe78cdb6d0/jdk-8u152-windows-i586.exe
    64位jdk下载地址 :http://download.oracle.com/otn-pub/java/jdk/8u152-b16/aa0333dd3019491ca4f6ddbe78cdb6d0/jdk-8u152-windows-x64.exe
### linux :
    32为jdk下载地址 : http://download.oracle.com/otn-pub/java/jdk/8u152-b16/aa0333dd3019491ca4f6ddbe78cdb6d0/jdk-8u152-linux-i586.tar.gz
    64位jdk下载地址 : http://download.oracle.com/otn-pub/java/jdk/8u152-b16/aa0333dd3019491ca4f6ddbe78cdb6d0/jdk-8u152-linux-x64.tar.gz
