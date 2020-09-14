Jenkins启动

1. 下载 jenkins.war包

2. 进入 jenkins.war所在的路径

3. 运行命令 `java -jar jenkins.war`

   指定端口启动 : `java -jar jenkins.war --httpPort=9090`

4. 浏览器输入 `http://localhost:9090`,就会进入登录页面

5. 输入密码,密码默认在` .jenkins\secrets\initialAdminPassword`这个文件里面



在浏览器中会出现 一直卡在启动中的问题

需要改两个地方:

1. `.jenkins\hudson.model.UpdateCenter.xml`将url修改成

   ```xml
   http://mirror.xmission.com/jenkins/updates/update-center.json
   ```

   也可以网上搜索其他的更新中心的地址

2. 修改`.jenkins\updates\default.json`文件

   ```json
   "connectionCheckUrl":"http://www.google.com/"
   ```

   改成 : 

   ```json
   "connectionCheckUrl":"http://www.baidu.com/"插件更新
   ```

   通过notepad++打开default.json

   搜索并替换 `updates.jenkins-ci.org/download` 为 `mirrors.tuna.tsinghua.edu.cn/jenkins`

3. 镜像站:

```properties
Jenkins 中文社区	= https://updates.jenkins-zh.cn/update-center.json
清华大学	= https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json
华为开源镜像站 =	https://mirrors.huaweicloud.com/jenkins/updates/update-center.json
```



Jenkins镜像站 Jenkins相关安装包可以在这下载https://mirrors.tuna.tsinghua.edu.cn/jenkins/