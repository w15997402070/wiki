# Fiddle拦截和修改浏览器请求

以qq浏览器为例



开启代理软件,

1. 设置情景模式 'filter'
2.  设置代理 'browserProxy'  -> 127.0.0.1  端口8888
3. 网页请求在代理软件中设置走 browseProxy代理

开启Fiddle软件

1. 代理设置 tools -> options,进行端口设置

   ![image-20200307160856185](D:\data\notes\notes\实用工具\Untitled\image-20200307160856185.png)

2. 设置https拦截

   会出现几个弹框,全部选yes就行了

   ![image-20200307161038740](D:\data\notes\notes\实用工具\Untitled\image-20200307161038740.png)

3. 设置打断点的具体url

   bpafter url

   例如:

   `bpafter https://www.cnblogs.com/linxiong945/ajax/TopLists.aspx `按回车键,下次请求时就会鼠标点击这个请求的那一行进入断点

   ![image-20200307161422887](D:\data\notes\notes\实用工具\Untitled\image-20200307161422887.png)

4. 修改返回值,然后点击 `run to Completion`

   ![image-20200307161512964](D:\data\notes\notes\实用工具\Untitled\image-20200307161512964.png)

   再看页面,变成了修改后的返回值

   ![image-20200307161553089](D:\data\notes\notes\实用工具\Untitled\image-20200307161553089.png)

5. 相关命令

   * bpu request开始前中断
   * bpm/bpv 在特定method中断
   * bpafter在响应到达时中断
   * bps在特定http状态码时中断

   再次输入

6. 