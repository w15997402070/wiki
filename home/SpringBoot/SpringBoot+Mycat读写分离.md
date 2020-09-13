# SpringBoot Mycat读写分离

[toc]

## Mysql主从配置

 docker安装mysql

下载MySQL镜像
```bash
docker pull mysql:5.7
```

启动主MySQL
```bash
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root  \
-d mysql:5.7
```
参数说明

- -p 3306:3306：将容器的3306端口映射到主机的3306端口,前面是主机的端口,后面是容器的端口
- -v /mydata/mysql/conf:/etc/mysql：将配置文件夹挂在到主机
- -v /mydata/mysql/log:/var/log/mysql：将日志文件夹挂载到主机
- -v /mydata/mysql/data:/var/lib/mysql/：将数据文件夹挂载到主机
- -e MYSQL_ROOT_PASSWORD=root：初始化root用户的密码

编写mysql配置文件
在`/mydata/mysql/conf`目录下发现没有配置文件,所以需要自己创建一个
```bash
# 创建文件
touch my.cnf
# 编辑文件
vim my.cnf

[mysqld]
log-bin=mysql-bin #[必须]启用二进制日志
server-id=1 #[必须]服务器唯一ID，默认是1，最好取ip的后3位
expire-logs-days=7
```

登录mysql查看log-bin是否启动成功
```bash
# 进入mysql容器
docker exec -it mysql /bin/bash

# 在容器内登录mysql
mysql -uroot -proot

# 查看是否启用
mysql> show variables like '%log_bin%';
+---------------------------------+--------------------------------+
| Variable_name                   | Value                          |
+---------------------------------+--------------------------------+
| log_bin                         | ON                             |
| log_bin_basename                | /var/lib/mysql/mysql-bin       |
| log_bin_index                   | /var/lib/mysql/mysql-bin.index |
| log_bin_trust_function_creators | OFF                            |
| log_bin_use_v1_row_events       | OFF                            |
| sql_log_bin                     | ON                             |
+---------------------------------+--------------------------------+

新增备份用户
mysql> grant replication slave on *.* to 'backup'@'%' identified by '123456';
mysql> flush privileges;
# 记住这里从mysql会用到
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000001 |      591 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+

```

配置从mysql
docker再起一个容器mysql-slave
```bash
docker run -p 4306:3306 --name mysql-slave \
-v /mydata/mysql-slave/log:/var/log/mysql \
-v /mydata/mysql-slave/data:/var/lib/mysql \
-v /mydata/mysql-slave/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root  \
-d mysql:5.7
```
同配置主mysql一样在`/mydata/mysql-slave/conf`目录下创建配置文件

```bash
# 创建文件
touch my.cnf
# 编辑文件
vim my.cnf

[mysqld]
log-bin=mysql-bin
server-id=2
expire-logs-days=7
```
和上面主MySQL一样登录进mysql

```bash
mysql -u root -proot
// 停止 slave
mysql> stop slave;
# mysql-bin.000001,master_log_pos=591对应主MySQL show master status;
mysql>change master to master_host='192.168.124.27',master_user='backup',master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=591;
// 启用slave
mysql> start slave;
// 查看slave状态
mysql> show slave status \G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.124.27
                  Master_User: backup
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000001
          Read_Master_Log_Pos: 2584
               Relay_Log_File: dd1d1c5ab744-relay-bin.000003
                Relay_Log_Pos: 2313
        Relay_Master_Log_File: mysql-bin.000001
             Slave_IO_Running: Yes   
            Slave_SQL_Running: Yes

```
这两个yes表示配置成功,可以创建数据库和表试试了
```bash
Slave_IO_Running: Yes   
Slave_SQL_Running: Yes
```
MySQL配置完成


## 安装配置Mycat

下载mycat
`http://mycat.org.cn/`这个地址查看Mycat版本,选择合适的版本下载

进入这里`http://dl.mycat.org.cn/1.6-RELEASE/`下载,[linux版本下载地址](http://dl.mycat.org.cn/1.6-RELEASE/Mycat-server-1.6-RELEASE-20161028204710-linux.tar.gz)
linux：
./mycat start 启动

./mycat stop 停止

./mycat console 前台运行

./mycat install 添加到系统自动启动（暂未实现）

./mycat remove 取消随系统自动启动（暂未实现）

./mycat restart 重启服务

./mycat pause 暂停

./mycat status 查看启动状态
进入linux中放置压缩包的目录,解压文件
```bash
# 解压
tar -zxvf Mycat-server-1.6-RELEASE-20161028204710-linux.tar.gz

# 启动
bin/mycat start
```
Mycat连接测试：
测试mycat与测试mysql完全一致，mysql怎么连接，mycat就怎么连接。

推荐先采用命令行测试：

`mysql -uroot -proot -P8066 -h127.0.0.1`
查看`hostname`
```bash
$ hostname
localhost

```
Mycat配置
`schema.xml`
```xml
<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://io.mycat/">

    <schema name="itools_simple" checkSQLschema="false" sqlMaxLimit="100" dataNode="dn1">
    </schema>
    <dataNode name="dn1" dataHost="wang-localhost" database="testdb" />
    <dataHost name="localhost" maxCon="1000" minCon="10" balance="0"
              writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
        <heartbeat>select user()</heartbeat>
        <writeHost host="hostM1" url="192.168.124.27:3306" user="user1" password="123456">
            <!-- 可以配置多个从库 -->
            <readHost host="hostS2" url="192.168.124.27:4306" user="user2" password="123456" />
        </writeHost>
    </dataHost>
</mycat:schema>
```
`server.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- - - Licensed under the Apache License, Version 2.0 (the "License"); 
	- you may not use this file except in compliance with the License. - You 
	may obtain a copy of the License at - - http://www.apache.org/licenses/LICENSE-2.0 
	- - Unless required by applicable law or agreed to in writing, software - 
	distributed under the License is distributed on an "AS IS" BASIS, - WITHOUT 
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. - See the 
	License for the specific language governing permissions and - limitations 
	under the License. -->
<!DOCTYPE mycat:server SYSTEM "server.dtd">
<mycat:server xmlns:mycat="http://io.mycat/">
	<system>
	<property name="useSqlStat">0</property>  <!-- 1为开启实时统计、0为关闭 -->
	<property name="useGlobleTableCheck">0</property>  <!-- 1为开启全加班一致性检测、0为关闭 -->

		<property name="sequnceHandlerType">2</property>
      <!--  <property name="useCompression">1</property>--> <!--1为开启mysql压缩协议-->
        <!--  <property name="fakeMySQLVersion">5.6.20</property>--> <!--设置模拟的MySQL版本号-->
	<!-- <property name="processorBufferChunk">40960</property> -->
	<!-- 
	<property name="processors">1</property> 
	<property name="processorExecutor">32</property> 
	 -->
		<!--默认为type 0: DirectByteBufferPool | type 1 ByteBufferArena-->
		<property name="processorBufferPoolType">0</property>
		<!--默认是65535 64K 用于sql解析时最大文本长度 -->
		<!--<property name="maxStringLiteralLength">65535</property>-->
		<!--<property name="sequnceHandlerType">0</property>-->
		<!--<property name="backSocketNoDelay">1</property>-->
		<!--<property name="frontSocketNoDelay">1</property>-->
		<!--<property name="processorExecutor">16</property>-->
		<!--
			<property name="serverPort">8066</property> <property name="managerPort">9066</property> 
			<property name="idleTimeout">300000</property> <property name="bindIp">0.0.0.0</property> 
			<property name="frontWriteQueueSize">4096</property> <property name="processors">32</property> -->
		<!--分布式事务开关，0为不过滤分布式事务，1为过滤分布式事务（如果分布式事务内只涉及全局表，则不过滤），2为不过滤分布式事务,但是记录分布式事务日志-->
		<property name="handleDistributedTransactions">0</property>
		
			<!--
			off heap for merge/order/group/limit      1开启   0关闭
		-->
		<property name="useOffHeapForMerge">1</property>

		<!--
			单位为m
		-->
		<property name="memoryPageSize">1m</property>

		<!--
			单位为k
		-->
		<property name="spillsFileBufferSize">1k</property>

		<property name="useStreamOutput">0</property>

		<!--
			单位为m
		-->
		<property name="systemReserveMemorySize">384m</property>


		<!--是否采用zookeeper协调切换  -->
		<property name="useZKSwitch">true</property>


	</system>
	
	<!-- 全局SQL防火墙设置 -->
	<!-- 
	<firewall> 
	   <whitehost>
	      <host host="127.0.0.1" user="mycat"/>
	      <host host="127.0.0.2" user="mycat"/>
	   </whitehost>
       <blacklist check="false">
       </blacklist>
	</firewall>
	-->
	
	<user name="root">
		<property name="password">123456</property>
		<property name="schemas">testdb</property>
		
		<!-- 表级 DML 权限设置 -->
		<!-- 		
		<privileges check="false">
			<schema name="TESTDB" dml="0110" >
				<table name="tb01" dml="0000"></table>
				<table name="tb02" dml="1111"></table>
			</schema>
		</privileges>		
		 -->
	</user>

	<user name="user">
		<property name="password">user</property>
		<property name="schemas">testdb</property>
		<property name="readOnly">true</property>
	</user>
	<user name="user1">
	    <property name="password">123456</property>
	    <property name="schemas">testdb</property>
	    <property name="defaultSchema">itools_simple</property>
	</user>

	<user name="user2">
	    <property name="password">123456</property>
	    <property name="schemas">testdb</property>
	    <property name="readOnly">true</property>
	    <property name="defaultSchema">testdb</property>
	</user>
</mycat:server>
```

**先在mysql数据库中创建数据库testdb**
可以用高版本的navicat测试连接mycat,和连接mysql一样

## 读写分离代码

数据源配置

数据库连接配置
```properties
spring.datasource.select.jdbc-url=jdbc:mysql://192.168.124.27:8066/itools_simple?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC
spring.datasource.select.username=user2
spring.datasource.select.password=123456
# 这里使用 com.mysql.jdbc.Driver 而不是 com.mysql.cj.jdbc.Driver,用后面这个包会报错
spring.datasource.select.driver-class-name=com.mysql.jdbc.Driver

spring.datasource.update.jdbc-url=jdbc:mysql://192.168.124.27:8066/itools_simple?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC
spring.datasource.update.username=user1
spring.datasource.update.password=123456
spring.datasource.update.driver-class-name=com.mysql.jdbc.Driver
```
上面驱动用`com.mysql.jdbc.Driver`,并且mysql驱动版本也不能太高
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.46</version>
    <scope>runtime</scope>
</dependency>
```
不然会报错
```java
Caused by: java.sql.SQLNonTransientConnectionException: CLIENT_PLUGIN_AUTH is required
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:526)
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:513)
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:505)
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:479)
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:489)
```
对应多数据源配置代码
```java
@Configuration
public class DataSourceConfig {
    /**
     * 创建可读数据源
     *
     * @return
     */
    @Bean(name = "selectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.select")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建可写数据源
     *
     * @return
     */
    @Bean(name = "updateDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.update")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }
}
```

```java
@Component
@Lazy(false)
public class DataSourceContextHolder {
    /**
     * 采用ThreadLocal 保存本地多数据源
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 设置数据源类型
     *
     * @param dbType
     */
    public static void setDbType(String dbType) {
        contextHolder.set(dbType);
    }

    /**
     * 获取数据源类型
     */
    public static String getDbType() {
        return contextHolder.get();
    }

    public static void clearDbType() {
        contextHolder.remove();
    }
}

```

```java
@Component
@Primary
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Autowired
    @Qualifier("selectDataSource")
    private DataSource selectDataSource;

    @Autowired
    @Qualifier("updateDataSource")
    private DataSource updateDataSource;

    /**
     * 返回生效的数据源名称
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDbType();
    }

    /**
     * 配置数据源信息
     */
    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> map = new HashMap<>(16);
        map.put("selectDataSource", selectDataSource);
        map.put("updateDataSource", updateDataSource);
        setTargetDataSources(map);
        setDefaultTargetDataSource(updateDataSource);
        super.afterPropertiesSet();
    }
}
```

```java
@Aspect
@Component
@Lazy(false)
@Order(0) // Order设定AOP执行顺序 使之在数据库事务上先执行
public class DataSourceOptionAop {

    /**
     * 可读数据源
     */
    private final static String DATASOURCE_TYPE_SELECT = "selectDataSource";
    /**
     * 可写数据源
     */
    private final static String DATASOURCE_TYPE_UPDATE = "updateDataSource";

    /**
     * 创建切面，根据方法类型选择不同的数据源
     *
     * @param joinPoint
     */
    @Before("execution(* com.example.mycat.service.*.*(..))")
    public void process(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.print("=========== " + methodName);
        if (methodName.startsWith("get") || methodName.startsWith("count") || methodName.startsWith("find")
                || methodName.startsWith("list") || methodName.startsWith("select") || methodName.startsWith("check")
                || methodName.startsWith("query")) {
            DataSourceContextHolder.setDbType(DATASOURCE_TYPE_SELECT);
            System.out.println("-----------------使用selectDataSource数据源-------------------");
        } else {
            DataSourceContextHolder.setDbType(DATASOURCE_TYPE_UPDATE);
            System.out.println("-----------------使用updateDataSource数据源-------------------");
        }
    }
}
```