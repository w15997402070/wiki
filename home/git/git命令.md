# git命令

## git设置本机用户名和邮箱

如果不加 `--global` 则为当前目录添加配置

```shell
>$ git config --global user.name "Your Name"
>$ git config --global user.email "email@example.com"
```

## git创建仓库

最好创建空文件夹作为仓库,进入创建的文件夹

```shell
>$ git init
```

## git添加文件到仓库

```shell
-- 把文件添加到仓库
>$ git add readme.txt

git add -f readme.tx 可以把ignore文件添加
-- 把文件commit到仓库
>$ git commit -m "wrote a readme file"
```

## git status

这个命令可以查看仓库状态

```shell

PS D:\data\notes\notes\git> git status
On branch master
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

        modified:   "git\345\221\275\344\273\244.md"

no changes added to commit (use "git add" and/or "git commit -a")
```

## git log

```shell
PS D:\data\notes\notes\git> git log
commit b6c13caa2319c7e9bd1ec804384af3d8c36f8c6b (HEAD -> master, origin/master, origin/HEAD)
Author: 汪正朋 <wangzhengpeng@ruiqin.net>
Date:   Thu Apr 25 09:38:43 2019 +0800

    git命令

commit c1dcc89dc36e01a13b228aa84d5763f3c1b98fc6
Author: 汪正朋 <wangzhengpeng@ruiqin.net>
Date:   Thu Apr 25 09:36:24 2019 +0800

    软件

-- 可以用这个格式化输出 git log --pretty=oneline

PS D:\data\notes\notes\git> git log --pretty=oneline
b6c13caa2319c7e9bd1ec804384af3d8c36f8c6b (HEAD -> master, origin/master, origin/HEAD) git命令
c1dcc89dc36e01a13b228aa84d5763f3c1b98fc6 软件
b87044199a1133a12b2ce015d59164b0489129eb 提交
5abb88664d2bfba60319bdd5b1a3b9ceb2e12d77 Merge branch 'master' of https://gitee.com/wzpeng/notes
1368d991acbbfe163d6cd2fe162d5cc842dd4ddb springboot启动

-- 查看每次的命令(这是查看每次提交到服务器的命令例如commit,pull,push等)
$ git reflog
```

## git reset 回退命令

```shell
-- 退回到上一个版本
$ git reset --hard HEAD^
-- 退回到指定把版本 `1094a`就是刚才gitlog看的版本号,可以写全也可以不写全,能区分哪个版本就行
$ git reset --hard 1094a
```

## git checkout

git checkout -- file 撤销工作区的修改
-- 不能省略 ,缺少--就变成切换分支了

$ git reset HEAD readme.txt 可以把暂存区的修改撤销掉,重新放回工作区

## $ git rm test.txt 删除文件