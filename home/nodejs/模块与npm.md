# 模块与npm

在Node.js中，以模块为单位划分所有功能。一个Node.js应用程序由大量的模块组成，每一个模块都是一个JavaScript脚本文件

在Node.js中，你也可以自己编写或从网上下载以下几种模块文件：

1）后缀名为.js的JavaScript脚本文件。

2）后缀名为.json的JSON文本文件。

3）后缀名为.node的经过编译后的二进制模块文件。

在加载这些模块文件时，需要指定该文件的完整路径及文件名

`require('./script/foo.js')`

## 从模块外部访问模块内的成员

### 使用exports对象

在一个模块文件中定义的本地（即非函数内定义的）变量、函数或对象只在该模块内有效，当你需要从模块外部引用这些变量、函数或对象时，需要在该模块文件内使用exports对象，方法如下所示。

```js
//foo.js
var myMsg = "hello";

var funcName =function () {
    return 'I am funcName function';
};

exports.msg = myMsg;
exports.funcName = funcName;

//fooTest.js
var foo = require('./foo');

console.log(foo.msg);
console.log(foo.funcName());
```

### 将模块定义为类

你也可以在模块文件内将exports对象书写为“module.exports”，但是在需要将模块定义为一个类时，只能使用“module.exports”的书写方法。

```js
//foo.js
var _name,_age;
var name='',age=0;

var foo = function(name,age){
    _name = name;
    _age = age;
};
foo.prototype.GetName = function () {
    return _name;
};
foo.prototype.SetName = function (name) {
    _name = name;
};
foo.prototype.GetAge = function () {
    return _age;
};
foo.prototype.SetAge = function (age) {
    _age = age;
};
foo.prototype.name = name;
foo.prototype.age = age;
module.exports = foo;
```

```js
//fooTest.js
var Foo = require('./foo');
var myFoo = new Foo("Tom", 40);
console.log(myFoo.name);//空的
console.log(myFoo.age);//0
console.log(myFoo.GetName());//Tom
console.log(myFoo.GetAge());//40
```

### 为模块类定义类变量或类函数

```js
//在foo.js中加入
foo.staticName = '';
foo.staticFunc = function() {
  console.log(foo.staticName);
};
```

```js
//在fooTest.js中加入
Foo.staticName = 'static name';
Foo.staticFunc();
```

## 组织与管理模块

### 从node_modules目录中加载模块

如果在require函数中只使用如下所示的方法指定文件名，但不指定路径，

```js
require('foo.js');
```

则Node.js将该文件视为node_modules目录下的一个文件

### 使用目录来管理模块

在Node.js中，可以将目录名指定为模块名，以便可以通过目录来管理模块，只需为该目录指定一个入口点。

一种最简单的将目录名指定为模块名的方法是在应用程序根目录下创建一个node_modules子目录，然后在其中为你的模块创建一个目录，并且在其中放入一个index.js文件，当你使用如下所示的代码时，Node.js将自动加载该目录下的index.js模块。接下来，我们在应用程序根目录下创建一个node_modules子目录，在其中创建一个foo子目录，并在其中创建一个index.js文件

一种更灵活的方式是在应用程序根目录下的node_modules子目录的foo子目录下创建一个package.json文件，并且在其中使用如下所示的代码创建模块名与主文件。

```json
{
    "name":"foo",
    "main":"./lib/foo.js"
}
```

### 从全局目录中加载模块

如果你在操作系统的环境变量中设置了NODE_PATH变量，并将变量值设置为一个有效的磁盘目录，当你在require函数中只指定模块名，而没有指定模块文件的路径，而且Node.js从其他路径中寻找不到需要被加载的模块文件时，Node.js将从NODE_PATH变量值所指向的磁盘目录中寻找并加载该模块文件。

## 模块对象的属性

* `module.id`：属性值为当前模块的ID。在默认情况下，主模块的ID属性值为“.”，其他模块的ID属性值为该模块文件的绝对路径。在模块文件中可以修改当前模块的ID属性值。
* `module.filename`：属性值为当前模块文件的文件名。
* `module.loaded`：属性值为布尔值，当属性值为false时表示模块尚未加载完毕，属性值为true时表示模块加载完毕
* `module.parent`：属性值为当前模块的父模块对象，即调用当前模块的模块对象
* `module.children`：属性值为一个数组，其中存放了当前模块的所有子模块对象，即当前模块中已加载的所有模块对象

## 包与npm包管理工具

在Node.js中，一个包事实上是一个目录，其中包含了用于对包进行描述的JSON格式的package.json文件。在一个包中，

### 一个`包`通常包含如下所示的一些内容：

1）在包的根目录中存放package.json文件。

2）在bin子目录中存放二进制文件。

3）在lib子目录中存放JavaScript文件。

4）在doc子目录存放对包或包的使用方法进行说明的文档文件。

5）在test子目录中存放一些对包进行单元测试用的文件。

### package.json

在一个package.json文件中，一个package.json文件中通常应该包含的字段及其作用如下所示：

1）name，包名。包名是唯一的，由小写字母、数字和下划线组成，不能含空格。

2）preferglobal，是否支持全局安装。字段值为true时支持全局安装，字段值为false时不支持全局安装。

3）description，包说明。对包进行简要描述。

4）version，版本号。

5）author，作者信息数组。每个数组元素中可包含name（作者姓名）字段、email（作者E-mail）字段、web（作者网址）字段。

6）maintainers，包维护者信息数组。每个数组元素中可包含name（包维护者姓名）字段、email（包维护者E-mail）字段、web（包维护者网址）字段。

7）bugs，bug的提交地址，可以是网址或电邮地址。

8）licenses，许可证数组。每个元素要包含type（许可证名称）和url（链接到许可证文本的地址）字段。

9）repository，仓库托管地址数组。每个元素要包含type（仓库的类型，如Git）、url（仓库地址）和path（相对于仓库的路径，可选）字段。

10）keywords，关键字数组，通常用于搜索。

11）dependencies，本包所依赖的包。是一个关联数组，由包名和版本号组成。

```json
//request模块的json
{
  "_from": "request@latest",
  "_id": "request@2.88.0",
  "_inBundle": false,
  "_integrity": "sha512-NAqBSrijGLZdM0WZNsInLJpkJokL72XYjUpnB0iwsRgxh7dB6COrHnTBNwN0E+lHDAJzu7kLAkDeY08z2/A0hg==",
  "_location": "/request",
  "_phantomChildren": {},
  "_requested": {
    "type": "tag",
    "registry": true,
    "raw": "request@latest",
    "name": "request",
    "escapedName": "request",
    "rawSpec": "latest",
    "saveSpec": null,
    "fetchSpec": "latest"
  },
  "_requiredBy": [
    "#USER",
    "/",
    "/cloudant-follow",
    "/couchapp",
    "/coveralls",
    "/nano",
    "/node-gyp",
    "/npm-registry-client"
  ],
  "_resolved": "https://registry.npmjs.org/request/-/request-2.88.0.tgz",
  "_shasum": "9c2fca4f7d35b592efe57c7f0a55e81052124fef",
  "_spec": "request@latest",
  "_where": "/Users/zkat/Documents/code/work/npm",
  "author": {
    "name": "Mikeal Rogers",
    "email": "mikeal.rogers@gmail.com"
  },
  "bugs": {
    "url": "http://github.com/request/request/issues"
  },
  "bundleDependencies": false,
  "dependencies": {
    "aws-sign2": "~0.7.0",
    "aws4": "^1.8.0",
    "caseless": "~0.12.0",
    "combined-stream": "~1.0.6",
    "extend": "~3.0.2",
    "forever-agent": "~0.6.1",
    "form-data": "~2.3.2",
    "har-validator": "~5.1.0",
    "http-signature": "~1.2.0",
    "is-typedarray": "~1.0.0",
    "isstream": "~0.1.2",
    "json-stringify-safe": "~5.0.1",
    "mime-types": "~2.1.19",
    "oauth-sign": "~0.9.0",
    "performance-now": "^2.1.0",
    "qs": "~6.5.2",
    "safe-buffer": "^5.1.2",
    "tough-cookie": "~2.4.3",
    "tunnel-agent": "^0.6.0",
    "uuid": "^3.3.2"
  },
  "deprecated": false,
  "description": "Simplified HTTP request client.",
  "devDependencies": {
    "bluebird": "^3.2.1",
    "browserify": "^13.0.1",
    "browserify-istanbul": "^2.0.0",
    "buffer-equal": "^1.0.0",
    "codecov": "^3.0.4",
    "coveralls": "^3.0.2",
    "function-bind": "^1.0.2",
    "istanbul": "^0.4.0",
    "karma": "^3.0.0",
    "karma-browserify": "^5.0.1",
    "karma-cli": "^1.0.0",
    "karma-coverage": "^1.0.0",
    "karma-phantomjs-launcher": "^1.0.0",
    "karma-tap": "^3.0.1",
    "phantomjs-prebuilt": "^2.1.3",
    "rimraf": "^2.2.8",
    "server-destroy": "^1.0.1",
    "standard": "^9.0.0",
    "tape": "^4.6.0",
    "taper": "^0.5.0"
  },
  "engines": {
    "node": ">= 4"
  },
  "files": [
    "lib/",
    "index.js",
    "request.js"
  ],
  "greenkeeper": {
    "ignore": [
      "hawk",
      "har-validator"
    ]
  },
  "homepage": "https://github.com/request/request#readme",
  "keywords": [
    "http",
    "simple",
    "util",
    "utility"
  ],
  "license": "Apache-2.0",
  "main": "index.js",
  "name": "request",
  "repository": {
    "type": "git",
    "url": "git+https://github.com/request/request.git"
  },
  "scripts": {
    "lint": "standard",
    "test": "npm run lint && npm run test-ci && npm run test-browser",
    "test-browser": "node tests/browser/start.js",
    "test-ci": "taper tests/test-*.js",
    "test-cov": "istanbul cover tape tests/test-*.js"
  },
  "version": "2.88.0"
}

```

## npm包管理工具

搜索并查看forever包的信息

```shell
npm search forever
```

查看官方包仓库中forever包所用package.json文件中的信息

```shell
npm view forever
```

需要下载forever包时，可以在命令行提示窗口中输入以下命令

```shell
npm install forever
```

全局下载

```shell
npm install -g forever
```

查看全局包安装路径

```shell
npm root -g
```

可以通过以下命令来修改Node.js的全局包的安装路径

```shell 
node config set prefix "d:\node"

//编辑配置文件
npm config edit
```

该命令将把Node.js的全局包的安装路径修改为“d:\node\node_modules”

通过如下所示的命令来查看命令行提示窗口当前目录下所安装的所有包

```shell
npm list
```

通过如下所示的命令来卸载命令行提示窗口当前目录下安装的某个包

```shell
npm uninstall <包名>
```

可以通过如下所示的命令来卸载Node.js的全局包的安装路径下安装的某个包

```shell
npm uninstall -g <包名>
```

通过如下所示的命令来更新命令行提示窗口当前目录下安装的某个包

```shell
npm update <包名>
```

通过如下所示的命令来更新Node.js的全局包的安装路径下安装的某个包

```shell
npm update -g <包名>
```

通过如下所示的命令来更新命令行提示窗口当前目录下安装的所有包

```shell
npm update
```

可以通过如下所示的命令来更新Node.js的全局包的安装路径下安装的所有包

```shell
npm update -g
```

