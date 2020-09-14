# 更改镜像源

解决下载慢的问题

1. 在用户目录下,windows是`C:\Users\wang\`目录下新建pip文件夹,在pip文件夹下新建`pip.ini`文件

2. `pip.ini`文件

   ```ini
   [global]
   index-url = https://pypi.tuna.tsinghua.edu.cn/simple/ 
   ```

   

3. 镜像源

   ```txt
   阿里云 http://mirrors.aliyun.com/pypi/simple/ 
     中国科技大学 https://pypi.mirrors.ustc.edu.cn/simple/ 
     豆瓣(douban) http://pypi.douban.com/simple/ 
     清华大学 https://pypi.tuna.tsinghua.edu.cn/simple/ #推荐此源
     中国科学技术大学 http://pypi.mirrors.ustc.edu.cn/simple/
     华中理工大学 http://pypi.hustunique.com/simple/ 
     山东理工大学 http://pypi.sdutlinux.org/simple/ 
     加利福尼亚大学 https://www.lfd.uci.edu/~gohlke/pythonlibs/
   ```

   