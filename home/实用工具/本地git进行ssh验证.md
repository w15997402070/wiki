1.生成一个ssh的key
  $ ssh-keygen -t rsa -b 4096 -C "15997402070@163.com"
  接着会出现:-->
  $ Enter file in which to save the key (/c/Users/wang/.ssh/id_rsa):
  可以键入放key的位置(推荐直接默认按回车键)接着-->
  $ Enter passphrase (empty for no passphrase):
  $ Enter same passphrase again:
  输入两次密码.(下面会用一次)然后会出现-->
  Your identification has been saved in /c/Users/wang/.ssh/id_rsa.
Your public key has been saved in /c/Users/wang/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:b5Ii3lnosU62nJEfFBbtlZw9uR3wIqOyMavvd31Jk68 15997402070@163.com
The key's randomart image is:
+---[RSA 4096]----+
|        .. . =.. |
|         .. = =. |
|        o. + . =.|
|       . .o o o .|
|       +S.      .|
|       +*o     + |
|    . Oo= o . . +|
|   . B.@ = . . o.|
|    .o%oo .   E. |
+----[SHA256]-----+
2.ssh代理设置
确认是否有代理:-->
  $ eval $(ssh-agent -s)
将ssh的key加入到代理中:-->
  $ ssh-add ~/.ssh/id_rsa
  会出现:Agent pid 18212
  输入刚才的密码:
  $ Enter passphrase for /c/Users/wang/.ssh/id_rsa:
  会出现:Identity added: /c/Users/wang/.ssh/id_rsa (/c/Users/wang/.ssh/id_rsa)
3.在github网页中加入ssh的key
  进入github的个人设置中(setting)-->选SSH and GRG keys-->点New SSH key
  -->随便写一个title-->然后复制C:\Users\wang\.ssh\id_rsa.pub的key到key中(全部复制不要少东西也不要加空格)-->然后Add SSH key就可以了.
4.验证是否添加成功
  $ ssh -T git@github.com
出现:-->Hi w15997402070! You've successfully authenticated, but GitHub does not provide shell access.
表示成功.
