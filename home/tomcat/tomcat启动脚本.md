# tomcat启动脚本

## `startup.bat`

主要功能是找到 `catalina.bat`

```bat
@echo off

rem 此命令表示之后所有对环境变量的改变只限于该批处理文件,
rem 要还原原先的设置,可以执行 endlocal,如果未显示执行,则会在批处理的最后隐式执行endlocal命令
setlocal

rem Guess CATALINA_HOME if not defined
set "CURRENT_DIR=%cd%"
if not "%CATALINA_HOME%" == "" goto gotHome
rem 设置 CATALINA_HOME 环境变量
set "CATALINA_HOME=%CURRENT_DIR%"
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
cd ..
set "CATALINA_HOME=%cd%"
cd "%CURRENT_DIR%"
:gotHome
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

set "EXECUTABLE=%CATALINA_HOME%\bin\catalina.bat"

rem Check that target executable exists
if exist "%EXECUTABLE%" goto okExec
echo Cannot find "%EXECUTABLE%"
echo This file is needed to run this program
goto end
:okExec

rem Get remaining unshifted command line arguments and save them in the
rem 设置了环境变量后接收启动参数
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs      rem 判断第一个参数是否为空,为空表示没有参数
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1  rem 将参数组成一行,接在后面
shift  rem 把参数前移一位
goto setArgs rem 又跳到 :setArgs
:doneSetArgs
rem 以刚刚 CMD_LINE_ARGS 收集的所有参数作为参数调用并执行  catalina.bat 批处理脚本
call "%EXECUTABLE%" start %CMD_LINE_ARGS%

:end
```

CATALINA_HOME 设置逻辑

![](D:\data\notes\gitnote\image\2019-12-11-17-43-29-image.png)

`shutdown.bat` 与 `startup.bat` 处理逻辑基本一致 

## `catalina.bat`

tomcat的启动类为 `org.apache.catalina.startup.Bootstrap`

服务器相关信息获取类`org.apache.catalina.util.ServerInfo`
