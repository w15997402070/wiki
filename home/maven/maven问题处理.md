# maven问题

## Maven pom文件jar包下载不下来问题

更改下载的jar包位置的镜像地址例如aliyun的

在`settings.xml`中添加

```xml
    <mirror> 
		<id>alimaven</id> 
		<name>aliyun maven</name> 
		<url>http://maven.aliyun.com/nexus/content/repositories/central/</url> 
		<mirrorOf>central</mirrorOf> 
	</mirror>
	
	<mirror>
      <id>alimaven2</id>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
      <mirrorOf>central</mirrorOf>        
    </mirror>
```

也可以网上搜索其他镜像地址