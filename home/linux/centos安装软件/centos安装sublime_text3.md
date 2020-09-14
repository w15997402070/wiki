1. 下载软件安装包
![](assets/markdown-img-paste-20171130143442174.png)

2. 解压压缩包

       tar -jxvf sublime_text_3_build_3126_x64.tar.bz2
3. 可以进入解压的目录使用命令运行sublime

       ./sublime_text
4. 配置环境变量

       vi /etc/profile
       export PATH=/opt/sublime:$PATH
       source /etc/profile
5.这样就可以直接运行`sublime_text`启动sublime了
