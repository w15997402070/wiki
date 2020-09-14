## Node相关类

[toc]

## Bufffer处理二进制数据

创建buffer对象

```js
var buffer1 = Buffer.alloc(1024);
```

fill方法填充内容

```js
buffer.fill(value,[offset],[end])
```

在Buffer对象的fill方法中，可以使用三个参数，其中第一个参数为必须指定的参数，参数值为需要被写入的数值，第二个参数与第三个参数为可选参数，其中第二个参数用于指定从第几个字节处开始写入被指定的数值，默认值为0，即从缓存区的起始位置写入，第三个参数用于指定将数值一直写入到第几字节处，默认值为Buffer对象的大小，即书写到缓存区底部

```js
slice();
buffer.toString([encoding], [start], [end])
buffer.write(string, [offset], [length], [encoding])

//将Buffer对象中保存的二进制数据复制到另一个Buffer对象中时
buffer.copy(targetBuffer, [targetStart], [sourceStart], [sourceEnd])

//将buffer对象转换成json
JSON.stringify(buffer)

//经过转换后的字符串还原为一个数组
JSON.parse(string)
```

### StringDecoder对象

```js
var StringeDecoder = require('string_decoder').StringDecoder;
var decoder = new StringDecoder([encoding])

//将Buffer对象中的数据转换为字符串
decoder.write(buffer)

```

### Buffer类的类方法

```js

Buffer.isBuffer(buffer);//方法用于判断一个对象是否为一个Buffer对象

Buffer.byteLength(string, [encoding]);//计算一个指定字符串的字节数
Buffer.concat[list, [totalLength]]//将几个Buffer对象结合创建为一个新的Buffer对象
Buffer.isEncoding(encoding);//检测一个字符串是否为一个有效的编码格式字符串
```

## 操作文件系统

### 读写文件

Node.js中，提供一个fs模块，以实现文件及目录的读写操作

文件读写

```js
fs.readFile(filename, [options], callback);
```

```js
var fs = require("fs");
//同步方法读取文件
var data = fs.readFileSync('./foo.js','utf8');
console.log(data);
//异步读取文件
fs.readFile('./foo.js','utf8',function (err,data) {
    console.log(data);
});
```

使用三个参数，其中filename参数与callback参数为必须指定的参数，options参数为可选参数。filename参数用于指定读取文件的完整文件路径及文件名，options参数值为一个对象，在其中指定读取文件时需要使用的选项，在该参数值对象中可以使用flag属性指定对该文件采取什么操作，默认值为'r'（如果指定读取的文件不存在，则抛出异常），可指定值如下所示。

* 'r'：读取文件，如果文件不存在则抛出异常。
* 'r+'：读取并写入文件，如果文件不存在则抛出异常。
* 'rs'：以同步方式读取文件并通知操作系统忽略本地文件系统缓存，如果文件不存在则抛出异常。由于该属性值的使用将使操作系统忽略本地文件系统缓存机制，因此在操作网络文件系统时建议使用该属性值，但由于其对性能产生一定的负面影响，所以在其他场合下不建议使用。
* 'w'：写入文件。如果文件不存在则创建该文件，如果该文件已存在则清空文件内容。
* 'wx'：作用与'w'类似，但是以排他方式写入文件。
* 'w+'：读取并写入文件。如果文件不存在则创建该文件，如果该文件已存在则清空文件内容。
* 'wx+'：作用与'w+'类似，但是以排他方式打开文件。
* 'a'：追加写入文件，如果文件不存在则创建该文件。
* 'ax'：作用与'a'类似，但是以排他方式写入文件。
* 'a+'：读取并追加写入文件，如果文件不存在则创建该文件。
* 'ax+'：作用与'a+'类似，但是以排他方式打开文件。

在options参数值中，可使用encoding属性指定使用何种编码格式来读取该文件，可指定属性值为“utf8”、“ascii”与“base64”

#### 写文件

```js
//写文件
fs.writeFile('./writeTest.txt','这是第一行.\r\n这是第二行',function (err) {
    if (err){
        console.log("写文件失败");
    }else {
        console.log("写文件成功");
    }
});

//写缓存的数据
var buffer1 = Buffer.from("这是写入文件数据1");
fs.writeFile('./writeTest.txt',buffer1,function (err) {
    if (err){
        console.log("写文件失败");
    }else {
        console.log("写文件成功");
    }
});
```

```js
//同步方式写入文件
fs.writeFileSync('./writeTest.txt',buffer1);
//在文件后面添加数据
fs.appendFile(path, data, [options], callback)
```

#### 从指定位置处开始读写文件

打开文件

```js
fs.open(filename, flags, [mode], callback)
//回调函数
//第二个参数值为一个整数值，代表打开文件时返回的文件描述符
function(err, fd){
    
}
```

打开文件后就可以在指定文件位置读写文件

`fs.read(fd, buffer, offset, length, position, callback)`

* fd参数值必须为open方法所使用的回调函数中返回的文件描述符或openSync方法返回的文件描述符

* buffer参数值为一个Buffer对象，用于指定将文件数据读取到哪个缓存区中

* offset参数用于指定向缓存区中写入数据时的开始写入位置（以字节为单位)

* length参数用于指定从文件中读取的字节数

* position参数用于指定读取文件时的开始位置（以字节为单位）

* callback参数用于指定文件读取操作执行完毕时执行的回调函数

  ```js
  //bytesRead参数值为一个整数值，代表实际读取的字节数（由于文件的开始读取位置+指定读取的字节数可能大于文件长度，指定读取的字节数可能并不等于实际读取到的字节数）
  //buffer参数值为被读取的缓存区对象
  function (err, bytesRead, buffer){
      
  }
  ```

`fs.write(fd, buffer, offset, length, position, callback)`

* fd参数值必须为open方法所使用的回调函数中返回的文件描述符或openSync方法返回的文件描述符

* buffer参数值为一个Buffer对象，用于指定从哪个缓存区中读取数据

* offset参数用于指定从缓存区中读取数据时的开始读取位置（以字节为单位）

* length参数用于指定从缓存区中读取的字节数

* position参数值用于指定写入文件时的开始位置

* callback参数用于指定文件写入操作执行完毕时执行的回调函数

  ```js
  //written参数值为一个整数值，代表被写入的字节数
  //buffer参数值为一个Buffer对象，代表被读取的缓存区对象。
  function (err, written, buffer) {
      
  }
  ```

#### 关闭方法

```js
fs.close(fd, [callback]);
fs.closeSync(fd);

//fs模块中的fsync方法对文件进行同步操作，即将内存缓冲区中的剩余数据全部写入文件
//在调用关闭方法之前可以先同步缓冲区数据
fs.fsync(fd, [callback]);
```

### 创建与读取目录

#### 创建目录

```js
fs.mkdir(path, [mode], callback);
```

#### 读取目录

```js
fs.readdir(path, callback);
```

### 查看与修改文件或目录的信息

#### 查看文件或目录的信息

```js
fs.stat(path, callback);
fs.lstat(path, callback);//查看符号链接文件的信息时，必须使用lstat方法

//回调函数
function (err, stats){
    
}
```

stats参数值为一个fs.Stats对象。该对象拥有如下所示的一些方法，在这些方法中均不使用任何参数

对象的方法: 

* isFile方法：用于判断被查看的对象是否为一个文件，如果是的话则返回true，如果不是的话则返回false。
* isDirectory方法：用于判断被查看的对象是否为一个目录，如果是的话则返回true，如果不是的话则返回false。
* isBlockDevice方法：用于判断被查看的文件是否为一个块设备文件，如果是的话则返回true，如果不是的话则返回false，该方法仅在UNIX操作系统下有效。
* isCharacterDevice方法：用于判断被查看的文件是否为一个字符设备文件，如果是的话则返回true，如果不是的话则返回false，该方法仅在UNIX操作系统下有效。
* isSymbolicLink方法：用于判断被查看的文件是否为一个符号链接文件，如果是的话则返回true，如果不是的话则返回false，该方法仅在lstat方法的回调函数中有效。
* isFIFO方法：用于判断被查看的文件是否为一个FIFO文件，如果是的话则返回true，如果不是的话则返回false，该方法仅在UNIX操作系统下有效。
* isSocket：用于判断被查看的文件是否为一个socket文件，如果是的话则返回true，如果不是的话则返回false，该方法仅在UNIX操作系统下有效。

对象的属性: 

* dev：该属性值为文件或目录所在设备ID，该属性值仅在UNIX操作系统下有效。
* ino：该属性值为文件或目录的索引编号，该属性值仅在UNIX操作系统下有效。
* mode：该属性值为使用数值形式代表的文件或目录的权限标志。
* nlink：该属性值为文件或目录的硬连接数量。
* uid：该属性值为文件或目录的所有者的用户ID，该属性值仅在UNIX操作系统下有效。
* gid：该属性值为文件或目录的所有者的组ID，该属性值仅在UNIX操作系统下有效。
* rdev：该属性值为字符设备文件或块设备文件所在设备ID，该属性值仅在UNIX操作系统下有效。
* size：该属性值为文件尺寸（即文件中的字节数）。
* atime：该属性值为文件的访问时间。
* mtime：该属性值为文件的修改时间。
* ctime：该属性值为文件的创建时间。

#### 检查文件或目录是否存在

```js
fs.exists(path, callback)
//回调函数
function (exist){
    if(exist){
        console.log("文件存在");
    }else{
        console.log("文件不存在");
    }
}
```

#### 获取文件或目录的绝对路径

```js
fs.realpath(path, [cache], callback);
//回调函数
function (err, resolvedPath) {
    
}
```

#### 修改文件访问时间及修改时间

```js
//path参数用于指定需要被修改时间的文件的完整路径及文件名
//atime参数用于指定修改后的访问时间
//mtime参数用于指定修改后的修改时间
fs.utimes(path, atime, mtime, callback)

//回调函数
function (err) {
    
}
```

#### 修改文件或目录的读写权限

```js 
//mode参数用于指定修改后的文件或目录读写权限
//callback参数用于指定修改文件读写权限操作完成后执行的回调函数，该回调函数使用一个参数，参数值为修改文件读写权限操作失败时触发的错误对象。
fs.chmod(path, mode, callback)

//回调
function (err) {
    
}
```

#### 移动文件或目录

```js
fs.rename(oldPath, newPath, callback)
```

#### 创建与删除文件的硬链接

```js
//创建文件硬链接
fs.link(srcpath, dstpath, callback)

//删除文件硬链接
fs.unlink(path, callback)
```

#### 截断文件

```js
fs.truncate(filename, len, callback)
```

#### 删除空目录

```js
fs.rmdir(path, callback)
```

#### 监视文件或目录

```js
fs.watchFile(filename, [options], listener)

//回调
//curr参数值为一个fs.Stats对象，代表被修改之后的当前文件
//prev参数值也为一个fs.Stats对象，代表被修改之前的当前文件
function (curr, prev){
    
}
```

停止对watch方法中指定监视的文件或目录所执行的监视操作

```js
var watcher = fs.watch(filename, [options], [listener])

watcher.close();
```

### 流

#### 使用ReadStream对象读取文件

`fs.createReadStream(path, [options])`

```js
var fs = require("fs");

var file = fs.createReadStream('./writeTest.txt');

file.on('open',function (fd) {
    console.log("开始读取文件");
});
file.on('data',function (data) {
    console.log("读取到数据: "+data);
});
file.on('end',function () {
    console.log("文件读取完毕");
});
file.on('close',function () {
    console.log("文件被关闭");
});
file.on('error',function (error) {
    console.log("文件读取出错");
});

//暂停
//file.pause();

//恢复
//file.resume();
```

#### 使用WriteStream对象写入文件

`fs.createWriteStream(path, [options])`

```js
//WriteStream对象具有一个write方法，用于将流数据写入到目标对象中
writable.write(chunk, [encoding], [callback])

//WriteStream对象具有一个end方法，在写入文件的场合中，当没有数据再被写入流中时可调用该方法关闭文件
writable.end([chunk], [encoding], [callback])
```

```js
var file = fs.createReadStream("./writeTest.txt");
var out = fs.createWriteStream('./writeMessage.txt');
file.on('data', function (data) {
    out.write(data);
});
out.on('open', function (fd) {
    console.log('需要被写入的文件已被打开');
});

file.on('end', function () {
    out.end('再见', function () {
        console.log('共写入%d字节数据',out.bytesWritten);
    });
});
```

#### pipe方法执行文件的复制操作

`readStream.pipe(destination, [options])`

```js
var fs = require('fs');
var file = fs.createReadStream("./writeTest.txt");
var out = fs.createWriteStream('./writeMessage.txt');
file.pipe(out);
```

### 对路径进行操作

在Node.js中，提供了一个path模块，在这个模块中，提供了许多实用的、可被用来处理与转换路径的方法及属性

#### （1）normalize方法

1）解析路径字符串中的“..”字符串与“.”字符串，返回解析后的标准路径。

2）将多个斜杠字符串转换为一个斜杠字符串，例如将“\\”转换为“\”。

3）将Windows操作系统中的反斜杠字符串转换为正斜杠字符串。

4）如果路径字符串以斜杠字符串结尾，则在转换后的完整路径字符串末尾保留该斜杠字符串。

#### 2）join方法

该方法将多个参数值字符串结合为一个路径字符串

`path.join([path], [path2], [...])`

#### （3）resolve方法

在resolve方法中，可以指定一个或多个参数，每个参数值均为一个字符串

`path.join([path], [path2], [...])`

#### （4）relative方法

该方法用于获取两个路径之间的相对关系

`path.relative(from, to)`

#### （5）dirname方法

获取一个路径中的目录名

`path.dirname(p)`

#### （6）basename方法

获取一个路径中的文件名

`path.basename(p, [ext])`

#### （7）extname方法

获取一个路径中的扩展名

`path.extname(p)`

#### （8）path.sep属性

属性值为操作系统指定的文件分隔符,可能的属性值为“\\”（在Windows操作系统中）或“/”（在UNIX操作系统中）

#### （9）path.delimiter属性

属性值为操作系统指定的路径分隔符，可能的属性值为“；”（在Windows操作系统中）或“：”（在UNIX操作系统中）。



