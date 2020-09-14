### 1. 在项目配置
  一 . pom文件中加入依赖
  ```xml
    <dependency>
          <groupId>net.logstash.logback</groupId>
          <artifactId>logstash-logback-encoder</artifactId>
          <version>4.7</version>
      </dependency>
  ```
  二 . 在logback.xml文件中配置
 ```xml
 <?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <contextName>rfq-service</contextName>

  <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
  <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}spring.log}"/>
  <!--<property name="DB_LOG_PATTERN"
            value="%contextName %d{yyyy-MM-dd HH:mm:ss.SSS}%relative%thread%mdc%level%logger%msg%n"/>
  <include resource="org/springframework/boot/logging/logback/file-appender.xml"/>-->

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
          <pattern>${CONSOLE_LOG_PATTERN}</pattern>
          <!-- 本地环境.Win默认GBK. -->
          <springProfile name="local">
              <charset>gbk</charset>
          </springProfile>
          <!-- 开发、测试、UAT,运行环境. 多个使用逗号隔开.Linux默认UTF-8 -->
          <springProfile name="dev,test,jq,run">
              <charset>utf8</charset>
          </springProfile>
      </encoder>
  </appender>

  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
      <encoder>
          <pattern>${FILE_LOG_PATTERN}</pattern>
          <!-- 本地环境.Win默认GBK. -->
          <springProfile name="local">
              <charset>gbk</charset>
          </springProfile>
          <!-- 开发、测试、UAT,运行环境. 多个使用逗号隔开.Linux默认UTF-8 -->
          <springProfile name="dev,test,jq,run">
              <charset>utf8</charset>
          </springProfile>
      </encoder>
      <file>${LOG_FILE}</file>
      <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
          <fileNamePattern>${LOG_FILE}.%i</fileNamePattern>
          <!-- 最多保留20个文件，默认是7 -->
          <maxIndex>20</maxIndex>
      </rollingPolicy>
      <triggeringPolicy
              class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
          <MaxFileSize>20MB</MaxFileSize>
      </triggeringPolicy>
  </appender>

  <!--<appender name="log.mysql" class="ch.qos.logback.classic.db.DBAppender">
      &lt;!&ndash;<filter class="ch.qos.logback.classic.filter.LevelFilter">
          <level>DEBUG</level>
          <onMatch>ACCEPT</onMatch>
          <onMismatch>DENY</onMismatch>
      </filter>&ndash;&gt;
      <connectionSource class="ch.qos.logback.core.db.DataSourceConnectionSource">
          <dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">
              <driverClass>com.mysql.jdbc.Driver</driverClass>
              <jdbcUrl>jdbc:mysql://192.168.0.110:3306/logback_db</jdbcUrl>
              <user>root</user>
              <password>123456</password>
              <acquireIncrement>5</acquireIncrement>
              <minPoolSize>5</minPoolSize>
              <maxPoolSize>10</maxPoolSize>
              <maxIdleTime>1800</maxIdleTime>
          </dataSource>
      </connectionSource>
  </appender>-->
 <!-- elk配置主要是这 -->
  <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
      <destination>127.0.0.1:9250</destination>
      <!-- encoder必须配置,有多种可选 -->
      <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder" >
          <customFields>{"appname":"myapp"}</customFields>
      </encoder>
 </appender>

  <root level="INFO">
      <appender-ref ref="CONSOLE"/>
      <appender-ref ref="FILE"/>
      <!-- elk配置 -->
      <appender-ref ref="LOGSTASH"/>
      <!--<appender-ref ref="log.mysql" />-->
  </root>
</configuration>

 ```
### 2.在elk中配置

一 . 在logstash bin目录中创建logstash.conf文件(名字可以随便取)

- 在logstash.conf文件中加入配置
```
input {
	tcp {
	    ##host:port就是上面appender中的 destination，这里其实把logstash作为服务，开启9250端口接收logback发出的消息
	    host => "127.0.0.1"
	    port => 9250
	    #模式选择为server
	    mode => "server"
	    tags => ["tags"]
	    ##格式json
	    codec => json_lines
	  }
 }
output {
	#这里是es的地址
        elasticsearch {
            action => "index"
            hosts => "127.0.0.1:9200"
	    index => "%{[appname]}"
       }
}
```

- 启动logstash时加入配置文件启动
```
    .\logstash.bat -f .\logstash.conf
 ```
二 . 在kibana中设置
*在进入kibana页面中创建一个索引 "myapp" (对应配置文件中的appname)
在discover中就可以看到日志信息*
