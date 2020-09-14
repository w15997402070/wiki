# maven设置

## 设置代理

在settings.xml中设置 : 

```xml
<proxies>
    <proxy>
      <id>optional</id>
      <active>true</active><!-- 表示激活 -->
      <protocol>http</protocol><!-- 代理协议 -->
      <username>proxyuser</username> <!-- username -->
      <password>proxypass</password><!-- password -->
      <host>proxy.host.net</host> <!-- 主机名 -->
      <port>80</port> <!-- 端口 -->
      <nonProxyHosts>local.net|some.host.com</nonProxyHosts><!-- 指定哪些主机名不需要代理 用 | 分隔多个主机名,该配置支持通配符 ,如 *.google.com 表示以google.com结尾的域名主机名-->
    </proxy>
  </proxies>
```

proxies 下可以有多个 proxy元素,默认第一个被激活的proxy会生效

## pom文件设置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.demo</groupId>
    <artifactId>example</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <packaging>war</packaging>
    <name>Hello World Demo</name>
    
    <dependencies>
         <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.3.13.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <version>6.4.0.jre8</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>javax.sql</groupId>
            <artifactId>jdbc-stdext</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/rt.jar</systemPath>
        </dependency>
       <dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
            <version>4.1.2</version>
            <scope>test</scope>
		</dependency>
    </dependencies>
</project>
```

* modelVersion : 指定POM模型的版本

### maven 坐标

* groupId - artifactId - version :  定义了一个项目的基本的坐标, 

  * groupId  : 组,往往与项目所在的组织或公司存在关联,例如googlecode上建立了一个名为myapp的项目那么 groupId 是 `com.googlecode.myapp`
  * artifactId : 当前maven项目在组中唯一的id
  * version : 版本号
* packaging : 定义maven项目的打包方式

### maven 依赖

```xml
<dependencies>
    <dependency>
        <groupId>...</groupId>
        <artifactId>...</artifactId>
        <version>...</version>
        <type>...</type>
        <scope>...</scope>
        <optional>...</optional>
        <exclusions>
           <exclusion>
             ...
            </exclusion>
            ...
        </exclusions>
    </dependency>
    ...
</dependencies>
```

* dependencies : 项目依赖

  * groupId - artifactId - version : 依赖的基本坐标	
* type : 依赖的类型,对应坐标的packaging,默认为jar
  
* scope : 依赖范围 ,默认值 compile.(参见下面的依赖范围)
  * optional : 标记依赖是否可选
  * exclusions : 排除传递性依赖

### maven属性

```xml
 <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>1.8</java.version>
        <spring.version>4.1.3</spring.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>${spring.version}</version>
    </dependency>
</dependencies>
```

通过`<properties>`元素自定义一个或多个maven属性,在pom的其他地方可以使用`${属性名称引用该属性}`

maven 6 类属性 : 

* 内置属性 : 主要有两个常用内置属性 -- ${basedir} 表示项目跟目录,即包含pom.xml文件的目录;${version} 表示项目版本号
* POM 属性 : 用户可以使用该类属性引用POM文件中对应元素的值
  *  ${project.build.sourceDirectory}：项目的主源码目录，默认为src/main/java／。
  * ${project.build.testSourceDirectory}：项目的测试源码目录，默认为src/test/java／。
  * ${project.build.directory}：项目构建输出目录，默认为target/。
  * ${project.outputDirectory}：项目主代码编译输出目录，默认为target/classes／。
  * ${project.testOutputDirectory}：项目测试代码编译输出目录，默认为target/test-classes/。
  * ${project.groupld}：项目的groupld。
  * ${project.artifactld}：项目的artifactld。
  * ${projectversion}：项目的version，与${version}等价。
  * ${project.build.finalName}：项目打包输出文件的名称，默认为${project.artifactld}-${project.version}。
* 自定义属性 : 在POM的`<properties>`元素自定义maven属性
* settings属性 : 与POM 属性同理,用户使用以**settings.**开头的属性引用**settings.xml**文件中xml元素的值,如常用的`${settings.localRepository}`指向用户本地仓库的地址
* Java系统属性 : 所有的java系统属性都可以使用Maven属性引用,例如`${user.home}`指向了用户目录, `mvn help:system`查看所有的java系统属性
* 环境变量属性 : 所有环境变量都可以使用以 **env.**开头的maven属性引用,例如 `${env.JAVA_HOME}`指代了JAVA_HOME环境变量的值.可以用 `mvn help:system`查看所有的环境变量

### maven profile

```xml
<profiles>
    <profile>
        <!-- 本地模式 -->
        <id>local</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <activeprofile>local</activeprofile>
        </properties>
    </profile>
    <profile>
        <!-- dev模式 -->
        <id>dev</id>
        <!--<activation>-->
        <!--<activeByDefault>true</activeByDefault>-->
        <!--</activation>-->
        <properties>
            <activeprofile>dev</activeprofile>
        </properties>
    </profile>
</profiles>
```

#### 激活profile方式

* 命令行激活 : `mvn clean install -Pdev,local`这会激活**dev,和local** 两个profile

* settings  文件显示激活

  ```xml
  <settings>
    ...
      <activeprofiles>
          <activeprofile>dev</activeprofile>
      </activeprofiles>
     ...
  </settings>
  ```

* 系统属性激活:当系统某属性存在时,自动激活profile

  ```xml
  <profiles>
      <profile>
          <activation>
              <property>
                  <name>test</name>
              </property>
          </activation>
      </profile>
  </profiles>    
  ```

  也可以设置当某属性存在并且值为X时自动激活 : 

  ```xml
  <profiles>
      <profile>
          <activation>
              <property>
                  <name>test</name>
                  <value>X</value>
              </property>
          </activation>
      </profile>
  </profiles>   
  ```

* 操作系统环境激活

  ```xml
  <profiles>
      <profile>
          <activation>
              <os>
                  <name>Windows XP</name>
                  <family>Windows</family>
                  <arch>x86</arch>
                  <version>5.1.2600</version>
              </os>
          </activation>
      </profile>
  </profiles>  
  ```

  这里family的值包括Windows,Unix,Mac,其他属性name,arch,version可以查看系统属性`os.name`,`os.arch`,`os.version`获得

* 文件存在与否激活

  ```xml
  <profiles>
      <profile>
          <activation>
              <file>
                  <missing>x.properties</missing>
                  <exists>y.properties</exists>
              </file>
          </activation>
      </profile>
  </profiles>
  ```

* 默认激活

  ```xml
  <profiles>
      <profile>
          <!-- 本地模式 -->
          <id>local</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <activeprofile>local</activeprofile>
          </properties>
      </profile>
  </profiles>  
  ```

  

* 



### maven传递性依赖选择

A -> B -> C -> X(1.0) ,A -> D -> X(2.0) 第一原则是:路径最近者优先,所以X(2.0) 会被解析使用

A -> B -> Y(1.0) ,A -> C -> Y(2.0) 第二原则是:第一声明者优先,在依赖路径长度相等的前提下,依赖声明的顺序决定谁会被解析,顺序最靠前的那个依赖优胜,在这里如果B声明在C之前 那么Y(1.0)会被解析使用

### 依赖范围

maven在编译项目主代码的时候需要使用一套classpath,如`spring-core`依赖;maven在编译执行和执行测试的时候会使用另外一套classpath,如`junit`只在测试时需要;最后实际运行maven项目的时候,又会使用一套classpath,`spring-core`需要在classpath中,而junit则不需要

依赖范围就是控制依赖与这三种classpath的(编译classpath,测试classpath,运行classpath):

- compile : **编译依赖范围**。如果没有指定，就会默认使用该依赖范围。使用此依赖范围的Maven依赖，对于编译、测试、运行三种classpath都有效。如spring-core，在编译、测试和运行的时候都需要使用该依赖。

- test : **测试依赖范围**。使用此依赖范围的Maven依赖，只对于测试classpath有效，在编译主代码或者运行项目的使用时将无法使用此类依赖。典型的例子是JUnit，它只有在编译测试代码及运行测试的时候才需要。

- provided：**已提供依赖范围**。使用此依赖范围的Maven依赖，对于编译和测试classpath有效，但在运行时无效。典型的例子是servlet-api，编译和测试项目的时候需要该依赖，但在运行项目的时候，由于容器已经提供，就不需要Maven重复地引入一遍。

- runtime：**运行时依赖范围**。使用此依赖范围的Maven依赖，对于测试和运行classpath有效，但在编译主代码时无效。典型的例子是JDBC驱动实现，项目主代码的编译只需要JDK提供的JDBC接口，只有在执行测试或者运行项目的时候才需要实现上述接口的具体JDBC驱动。

- system：**系统依赖范围**。该依赖与三种classpath的关系，和provided依赖范围完全一致。但是，使用system范围的依赖时必须通过systemPath元素显式地指定依赖文件的路径。由于此类依赖不是通过Maven仓库解析的，而且往往与本机系统绑定，可能造成构建的不可移植，因此应该谨慎使用。systemPath元素可以引用环境变量,如:

  ```xml
  <dependency>
      <groupId>javax.sql</groupId>
      <artifactId>jdbc-stdext</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${project.basedir}/lib/rt.jar</systemPath>
  </dependency>
  ```

- import：导入依赖范围。该依赖范围不会对三种classpath产生实际的影响，

  

### maven代码位置

maven的主代码位于 `src/main/java`目录下

测试代码默认在`src/test/java` 目录下

### maven 打包

**maven打包**默认为`jar`包,可以通过`<packaging>war</packaging>`设置,jar文件默认在`/target`输出目录中,根据 `artifact-version.jar`规则进行命名,可以通过 ``

### maven配置远程仓库

```xml
<repositories>
    <repository>
        <id>jboss</id>
        <name>Jboss Repository</name>
        <url>http://repository.jboss.com/maven2/</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
            <checksumPolicy>ignore</checksumPolicy>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <layout>default</layout>
    </repository>
</repositories>
```

在 **repositories**可以声明一个或多个远程仓库

* id : 唯一,maven自带的中央仓库的id为central,如果其他仓库声明也使用该id,就会覆盖中央仓库的配置
* url : 指向仓库的地址
* releases : 发布版构件
  * enabled : true 表示支持发布版下载
  * updatePolicy : 用来配置maven从远程仓库更新的频率,默认值是daily,表示每天检查
    * never : 从不检查更新
    * always : 每次构建都检查更新
    * interval : X ,每隔X分钟检查一次更新(X为任意整数)
  * checksumPolicy : 用来配置Maven检查检验和文件的策略,默认值为warn,maven会在执行构建时输出警告信息
    * fail : maven遇到校验和错误就让构建失败
    * ignore : 使maven完全忽略校验和错误
* snapshots : 快照版构件,配置和releases一样
  * enabled : false表示不支持快照版下载
* layout : 表示仓库的布局是maven2及maven3的默认布局,而不是maven1的布局

#### 远程仓库的验证

远程仓库的认证信息需要配置在settings.xml文件中.

配置一个id为 my-proj的仓库配置认证信息(id元素必须与pom文件中配置的仓库的id一致):

```xml
<settings>
    <servers>
        <server>
            <id>my-proj</id>
            <username>repo-user</username>
            <password>repo-pwd</password>
        </server>
    </servers>
</settings>
```

#### 部署至远程仓库

在pom文件中配置构建部署地址 : 

```xml
<project>
    <distributionManagement>
        <repository>
            <id>proj-releases</id>
            <name>Proj Releases Repository</name>
            <url>http://192.168.0.110/content/repositories/proj-releases</url>
        </repository>
        <snapshotRepository>
            <id>proj-snapshots</id>
            <name>Proj Snapshots Repository</name>
            <url>http://192.168.0.110/content/repositories/proj-snapshots</url>
        </snapshotRepository>
    </distributionManagement>
</project>
```

运行命令 `mvn clean deploy`就会将构建输出的构件部署到对应的远程仓库

### 镜像

配置镜像 ,在settings.xml中配置 : 

```xml
<mirrors>
    <mirror>
          <id>maven.net.cn</id>
          <mirrorOf>central</mirrorOf><!-- 表示该配置为中央仓库的镜像,任何对于中央仓库的请求都会转至该镜像,也可以使用同样的方法配置其他仓库的镜像-->
          <name>one of the central mirrors in China</name>
          <url>http://maven.net.cn/content/groups/public/</url>
    </mirror>
</mirrors>
```

## 插件

* archetype : 创建项目骨架

* 

在pom文件中对插件进行全局配置 : 

  ```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <configuration>
                <webResources>
                    <resource>
                        <directory>lib/</directory>
                        <targetPath>WEB-INF/lib</targetPath>
                        <includes>
                            <include>**/*.jar</include>
                        </includes>
                    </resource>
                </webResources>
            </configuration>
        </plugin>
    </plugins>
</build>
  ```

  

### maven 命令

```shell
# 已解析的依赖
mvn  dependency:list  
# 查看项目依赖树
mvn dependency:tree
# 分析依赖树
mvn dependency:analyze

#部署构件
mvn clean deploy
```

