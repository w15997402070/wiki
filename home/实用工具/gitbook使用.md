# Gitbook使用

[toc]

# 安装

```sh
# 安装gitbook
> npm install gitbook-cli -g

> gitbook init

# 进入到要编译成Gitbook的文件夹
> cd .\test-doc\
> gitbook serve
Live reload server started on port: 35729
Press CTRL+C to quit ...

info: 7 plugins are installed
info: loading plugin "livereload"... OK
info: loading plugin "highlight"... OK
info: loading plugin "search"... OK
info: loading plugin "lunr"... OK
info: loading plugin "sharing"... OK
info: loading plugin "fontsettings"... OK
info: loading plugin "theme-default"... OK
info: found 26 pages
info: found 0 asset files
info: >> generation finished with success in 2.5s !

Starting server ...
Serving book on http://localhost:4000

# 访问 http://localhost:4000这个地址就可以看到Gitbook了

```

其他相关命令

```sh
# 查看Gitbook版本
$ gitbook ls-remote
# 安装其他版本
$ gitbook fetch beta
```



