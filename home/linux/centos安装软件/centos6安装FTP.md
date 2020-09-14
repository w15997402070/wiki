1.使用命令 rpm  -qa|grep vsftpd 查看系统是否安装了ftp，若安装了vsftp，使用这个命令会在屏幕上显示vsftpd的版本
    # rpm  -qa|grep vsftpd
2.使用命令rpm -e vsftpd 即可卸载ftp
    # rpm -e vsftpd
3.yum -y install vsftpd 安装ftp
    # yum -y install vsftpd
4.把下面几行注释去掉，让其配置生效：
    local_enable=YES
    write_enable=YES
    local_umask=022
    chroot_local_user=YES      #这行可能需自己写
    pam_service_name=vsftpd
    userlist_enable=YES
5.管理vsftpd相关命令：
    启动vsftpd:  service vsftpd start

    停止vsftpd:  service vsftpd stop

    重启vsftpd:  service vsftpd restart
6.设置vsftp的帐号。

    [root@localhost ~]# useradd -d /home/htdocs -s /sbin/nologin 用户名
7.为添加的账号设置密码

    [root@localhost ~]# passwd 用户名
    根据提示操作
8.添加ip_conntrack_ftp 模块

    [root@localhost ~]# vi /etc/sysconfig/iptables-config
    添加下面一行

    IPTABLES_MODULES="ip_conntrack_ftp"

9.打开21端口

    [root@localhost ~]# vi /etc/sysconfig/iptables

添加
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 21 -j ACCEPT
10.重启iptables使新的规则生效

    [root@localhost ~]# service iptables restart

11.到此，应该是可以了，若软件测试连接过程中，在用户验证的时候出现了错误503，
    应该是selinux设置的问题：可以用下面的命令检查

    [root@localhost ~]#getsebool -a |grep ftp

        allow_ftpd_anon_write --> off
        allow_ftpd_full_access --> off
        allow_ftpd_use_cifs --> off
        allow_ftpd_use_nfs --> off
        allow_tftp_anon_write --> off
        ftp_home_dir --> off
        ftpd_connect_db --> off
        ftpd_disable_trans --> off
        ftpd_is_daemon --> on
        httpd_enable_ftp_server --> off

    这是selinux的问题，我们只要打开ftp_home_dir的值开启为on：，allow_ftpd_full_access也一同开启即可。

    [root@localhost ~]#setsebool -P ftp_home_dir 1
    [root@localhost ~]#setsebool -P allow_ftpd_full_access 1
