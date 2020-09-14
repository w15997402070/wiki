# NodeJs进程

## 进程对象的方法与事件

### memoryUsage方法

```shell
//进入node的命令行环境
> node
>  process.memoryUsage();
{
  rss: 20488192,
  heapTotal: 4608000,
  heapUsed: 2973768,
  external: 1418171
}
>
```

* rss：属性值为一个整数，表示运行Node.js应用程序的进程的内存消耗量，单位为字节。
* heapTotal：属性值为一个整数，表示为V8所分配的内存量，单位为字节
* heapUsed：属性值为一个整数，表示V8的内存消耗量，单位为字节