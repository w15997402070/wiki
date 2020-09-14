# NodeJs基础教程

## Node.js中的控制台

### console.log

### console.error

### console.dir

console.dir方法用于查看一个对象中的内容并且将该对象的信息输出到控制台中

```js
var user = new Object();
 user.name = 'test'
 user.getName = function(){
	 return this.name
 }
 user.setName = function(name){
	 this.name = name
 } 
console.dir(user)

//{ name: 'test', getName: [Function], setName: [Function] }
```

### console.time方法与console.timeEnd方法

```js
console.time(label)
console.timeEnd(label)
```

`label`中可以是任意字符串,但是起始`label`必须一样

```js
console.time("test")
var user = new Object();
 user.name = 'test'
 user.getName = function(){
	 return this.name
 }
 user.setName = function(name){
	 this.name = name
 } 
console.dir(user)
console.timeEnd("test")

//{ name: 'test', getName: [Function], setName: [Function] }
//test: 8.718ms
```

### console.trace

console.trace方法用于将当前位置处的栈信息作为标准错误信息进行输出，使用方法如下所示：

```js

```

```js

var user = new Object();
 user.name = 'test'
 user.getName = function(){
	 return this.name
 }
 user.setName = function(name){
	 this.name = name
 } 
console.dir(user)
console.trace(user)

/*
{ name: 'test', getName: [Function], setName: [Function] }
Trace: { name: 'test', getName: [Function], setName: [Function] }
    at Object.<anonymous> (D:\tmp\browser\node-demo.js:13:9)
    at Module._compile (internal/modules/cjs/loader.js:956:30)
    at Object.Module._extensions..js (internal/modules/cjs/loader.js:973:10)
    at Module.load (internal/modules/cjs/loader.js:812:32)
    at Function.Module._load (internal/modules/cjs/loader.js:724:14)
    at Function.Module.runMain (internal/modules/cjs/loader.js:1025:10)
    at internal/main/run_main_module.js:17:11
*/
```

### console.assert

console.assert方法用于对一个表达式的执行结果进行评估，如果该表达式的执行结果为false，则输出一个消息字符串并抛出AssertionError异常

## Node.js中的全局作用域及全局函数

在Node.js中，在一个模块中定义的变量、函数或方法只在该模块中可用，但可以通过exports对象的使用将其传递到模块外部

在Node.js中，定义了一个global对象，代表Node.js中的全局命名空间，任何全局变量、函数或对象都是该对象的一个属性值

```js
//查看全局对象属性
console.log(global)
```

### setTimeout函数与clearTimeout函数

setTimeout表示在当前时刻过去多少毫秒之后执行某个回调函数

```js
setTimeout(cb,ms,[arg],[...])
```

前两个参数为必须输入的参数，第一个参数值为需要执行的回调函数，第二个参数值为一个整数，用于指定经过多少毫秒后执行该回调函数，

从第三个参数开始，为需要向回调函数中传入的参数

```js
var testfunction = function(arg){
	console.log(arg)
}
var timer = setTimeout(testfunction,1000,"这是一个定时函数")

// clearTimeout() 取消setTimeout()
clearTimeout(timer)
//这是一个定时函数
```

### setInterval函数与clearInterval函数

`setInterval`表示在当前时刻过去后每隔多少毫秒执行某个回调函数,与`setTimeout`用法一样,区别时`setInterval`如果不取消会一直重复的执行,而`setTimeout`只会执行一次

```js
var testfunction = function(arg){
	console.log(arg)
}
var timer = setInterval(testfunction,1000,"这是一个定时函数")

// clearInterval() setInterval()
clearInterval(timer)
//这是一个定时函数
```

### 定时器对象的unref方法与ref方法

setTimeout方法与setInterval函数均返回一个定时器对象。在Node.js中，为定时器对象定义了一个unref方法与一个ref方法

```js
var testfunction = function(arg){
	console.log(arg)
}
var timer = setTimeout(testfunction,1000,"这是一个定时函数")

// unref 取消 setTimeout
timer.unref()
//ref 重新激活 setTimeout
timer.ref()
//这是一个定时函数
```

`unref()`取消`setTimeout`的调用

`ref`重新激活`setTimeout`的调用

### 与模块相关的全局函数及对象

#### 使用require函数加载模块

```js
var test = require(../test.js)
//或
var http = require('http')
```

require函数使用一个参数，参数值可以为带有完整路径的模块文件名，也可以为模块名。当使用Node.js中提供的模块时，在require函数中只需指定模块名即可

在加载模块文件时，将运行该模块文件中的每一行代码

模块在首次加载后将缓存在内存缓存区中。这意味着，对于相同模块的多次引用得到的都是同一个模块对象，这也意味着，对于相同模块的多次引用不会引起模块内代码的多次执行

```js
// test.js
var test = "This is test js"
console.log(test)
exports.test = test

//demo.js
var test = require('./test.js')
var test2 = require('./test.js')

//结果 只显示一遍
This is test js
```

如果想在每次引用模块时都执行一次模块内部的某些代码，可以将这些代码指定在某个方法内，然后使用exports对象导出该方法，然后在引用该模块之后立即调用被引用模块对象的该方法。

```js
// test.js
var test = "This is test js"

var testFun = function testFun(){
	console.log(test)
}

exports.test = test
exports.testFun = testFun

//demo.js
var test = require('./test.js')
var test2 = require('./test.js')
test.testFun();
test2.testFun();

//结果
This is test js
This is test js
```

#### 使用require.resolve函数查询完整模块名

```js
var testPath = require.resolve('./test.js')
console.log(testPath)

//结果 打印出完整的路径
D:\tmp\test.js
```

#### require.cache对象

require.cache对象，该对象代表缓存了所有已被加载模块的缓存区

```js
console.log(require.cache)
//指定的缓存
console.log(require.cache[require.resolve('./test.js')])
```

## 事件处理机制及事件环机制

### EventEmitter类

```js
//event代表事件名，listener代表事件处理函数，中括号内的参数代表该参数为可选参数
eventEmitter.addListener(event,listener)// 对指定事件绑定事件处理函数
eventEmitter.on(event,listener) //对指定事件绑定事件处理函数(和上面一样)
eventEmitter.once(event,listener)// 对指定事件指定只执行一次的事件处理函数
eventEmitter.removeListener(event,listener) //对指定事件解除处理函数
eventEmitter.removeAllListeners(event) //对指定事件解除所有事件处理函数
eventEmitter.setMaxListeners(n) //指定事件处理函数的最大数量
eventEmitter.listeners(event) //获取指定事件的所有事件处理函数
eventEmitter.emit(event,[arg1],[arg2],[...]) //手工触发指定事件
                                       
EventEmitter.listenerCount(emitter,event) //EventEmitter类自身拥有一个listenerCount方法，可用来获取某个对象的指定事件的事件处理函数的数量
```

指定事件绑定事件处理函数时可以使用EventEmitter类的on方法或addListener方法

```js
emitter.on(event,listener)
emitter.addListener(event,listener)
```

#### emit()方法用法

```js
var server = http.createServer();
server.on('request',function (req,res) {
    console.log(req.url);
    res.write("Hello nodeJs");
    res.end();
});
server.on('customEvent',function (arg1,arg2,arg3) {
    console.log(arg1);
    console.log(arg2);
    console.log(arg3);
});
server.emit('customEvent',"参数1","参数2","参数3");
server.listen(1337,"127.0.0.1");
```

运行`node app.js`控制台就会打印出`参数1 参数2 参数3`

## DEBUG

### 在命令行窗口中使用调试器

`node debug <需要被执行的脚本文件名>`

### 观察变量值或表达式的执行结果

`watch('观察时使用的表达式')`

`watch(i == 100)`

在使用了watch命令之后的任何时刻，可以使用watchers命令查看所有观察表达式的运行结果或变量的变量值

`watchers`

`unwatch('观察时使用的表达式')`取消观察的表达式

### 设置与取消断点	

当需要设置断点时，我们可以使用setBreakpoint命令或sb命令，方法如下所示。

`setBreakpoint(filename, line)`

`sb(filename, line)`

其中第一个参数用于指定需要设置断点的脚本文件名，第二个参数用于指定将断点设置在第几行

在当前脚本设置

`setBreakpoint(12)`

可以使用clearBreakpoint命令或cb命令取消断点，方法如下所示

`clearBreakpoint(filename, line)`

`cb(filename, line)`

### 使用node-inspector调试工具

新版nodeJs自带这个调试工具

启动命令

`node  --inspect nodejs.js`

在chrome中输入`chrome://inspect`

![image-20200324211526827](D:\data\notes\notes\nodejs\nodeJs入门\image-20200324211526827.png)

点击`inspect`就会弹出控制台

![image-20200324211647792](D:\data\notes\notes\nodejs\nodeJs入门\image-20200324211647792.png)

就可以在这上面打断点了

## Nodemon自动重启

nodejs修改代码之后自动重启

安装

```shell
npm install -g  nodemon
```

之后启动项目

```shell
nodemon app.js
```

[nodemon](https://link.jianshu.com/?t=https%3A%2F%2Fgithub.com%2Fremy%2Fnodemon)