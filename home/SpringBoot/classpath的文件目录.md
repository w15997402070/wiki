# classPath的文件目录

找到target目录,classes目录就是classpath的目录

eg : D:\idea\demo\demo\target\classes

## Resources针对于资源文件的统一接口

Resources

* UrlResource:URL对应的资源，根据一个URL地址即可构建
* ClassPathResource：获取类路径下的资源文件
* FileSystemResource：获取文件系统里面的资源
* ServletContextResource:ServletContext封装的资源，用于访问ServletContext环境下的资源
* InputStreamResource：针对于输入流封装的资源
* ByteArrayResource：针对于字节数组封装的资源

## ResourceLoader

| prefix      | 示例                           | 说明                |
| ----------- | ------------------------------ | ------------------- |
| classpath : | classpath:com/myapp/config.xml | 从classpath加载     |
| file :      | file : /data/config.xml        | 从文件系统的url加载 |
| http :      | http://localhost/logo.png      | 从url加载           |
| (no)        | /data/config.xml               | 依赖上下文          |

