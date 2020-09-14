1. cd -    返回进入此目录之前所在的目录
```
  [root@localhost ~]# cd -
```
2. cd /  进入系统根目录
3. cd ~  进入当前用户主目录
   “当前用户主目录”和“系统根目录”是两个不同的概念。进入当前用户主目录有两个方法。
4. cd !$  把上个命令的参数作为cd参数使用。
```
    [root@localhost soft]# cd !$
    cd -
    /root
    [root@localhost ~]# cd !$
    cd -
    /opt/soft
    [root@localhost soft]#
```
