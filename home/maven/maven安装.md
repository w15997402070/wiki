# maven安装

## windows 安装maven

### 1. 下载maven包

* [下载地址](http://maven.apache.org/download.cgi)`http://maven.apache.org/download.cgi`,下载`apache-maven-3.6.2-bin.zip`将

* zip文件解压到指定目录,例如 : `D:\maven\apache-maven-3.6.2
* 设置环境变量,在系统变量中新建一个变量 `M2_HOME`值为`D:\maven\apache-maven-3.6.2`,接着在path中添加`%M2_HOME%\bin;`
* 在 cmd中输入 `mvn -v`验证是否安装成功





## maven 安装目录分析

* bin : maven 运行脚本
  * m2.conf : classworlds配置文件
  * mvn 基于unix平台的shell脚本
  * mvn.bat : 基于windows平台的bat脚本
  * mvnDebug 和 mvnDebug : 运行maven时开启debug,以便调式maven本身
* boot : 该目录只包含一个文件
  * `plexus-classworlds-2.5.2.jar` 一个类加载器,相当于默认的java类加载器
* conf : 配置文件
  * settings.xml : 直接修改该文件,就能在机器上全局定制maven的行为.一般情况下，我们更偏向于复制该文件至~/.m2/目录下（~表示用户目录），然后修改该文件，在用户范围定制Maven的行为。后面将会多次提到settings.xml，并逐步分析其中的各个元素。
* lib : 该目录包含了所有maven运行时需要的java类库
* LICENSE.txt : 软件许可证
* NOTICE.txt : maven包含的第三方软件
* README.txt : maven的简要介绍,包含安装需求及如何安装的简要指令