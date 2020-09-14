# 创建Http服务器

[toc]

## 创建服务器

创建一个HTTP服务器

```js
var http = require("http");

var server = http.createServer([requestListener]);
//requestListener是一个回调函数,用于指定当接收到客户端请求时所执行的处理
function (request, response){
    
}
```

createServer方法返回被创建的服务器对象，如果不在createServer方法中使用参数，也可以通过监听该方法创建的服务器对象的request事件（当接收到客户端请求时触发），并且指定该事件触发时调用的回调函数的方法来指定当接收到客户端请求时所需执行的处理

```js
server.on('request',function (request, response) {
    console.log(request.url);
    response.write("Hello nodeJs");
    response.end();
});
```

在创建了HTTP服务器后，需要指定该服务器所要监听的地址（可以为一个IP地址，也可以为一个主机名）及端口，这时，可以使用该HTTP服务器的listen方法，

```js
server.listen(port, [host], [backlog], [callback])
```

```js
server.listen(1337,"127.0.0.1");
```



*  port参数值用于指定需要监听的端口号,参数值为0时将为HTTP服务器分配一个随机端口号

* host参数用于指定需要监听的地址，如果省略该参数，服务器将监听来自于任何IPv4地址的客户端连接

* backlog参数值为一个整数值，用于指定位于等待队列中的客户端连接的最大数量，一旦超越这个长度，HTTP服务器将开始拒绝来自于新的客户端的连接，该参数的默认参数值为511

* 当对HTTP服务器指定了需要监听的地址及端口后，服务器端将立即开始监听来自于该地址及端口的客户端连接，这时触发该服务器的listening事件，可使用listen方法的callback参数来指定listening事件触发时调用的回调函数，该回调函数中不使用任何参数.如果不在listen方法中使用callback参数，也可以通过监听HTTP服务器对象的listening事件，并且指定该事件触发时调用的回调函数的方法来指定HTTP服务器开始监听时所需执行的处理

  ```js
  server.on('listening',function(){
      
  });
  ```

  关闭服务器:

  ```js
  server.close();
  //或
  server.on('close', function(){
      
  });
  ```

  监听发生错误的事件

  ```js
  server.on('error', function(e){
      
  });
  ```



## 获取客户端请求信息

### Query String模块

分别用来转换完整URL字符串与URL中的查询字符串

#### Query String模块中的parse方法将该字符串转换为一个对象

```js
queryString.parse(str, [seq], [eq], [options])
```

* str参数用于指定被转换的查询字符串
* sep参数用于指定该查询字符串中的分割字符，默认参数值为“&”
* eq参数用于指定该查询字符串中的分配字符，默认参数值为“=”
* options参数值为一个对象，可以在该对象中使用一个整数值类型的maxKeys属性来指定转换后的对象中的属性个数，如果将maxKeys属性值设定为0，其效果等于不使用maxKeys属性值。

#### Query String模块中的stringify方法将对象转换为一个查询字符串

```js
queryString.stringify(obj, [seq], [eq])
```

* obj参数用于指定被转换的对象
* sep参数用于指定查询字符串中所使用的分割字符，默认参数值为“&”
* eq参数用于指定查询字符串中使用的分配字符，默认参数值为“=”

### url模块

parse方法将URL字符串转换为一个对象，根据URL字符串中的不同内容，该对象中可能具有的属性及其含义如下所示:

* href：被转换的原URL字符串。
* protocol：客户端发出请求时使用的协议。
* slashes：在协议与路径中间是否使用“//”分隔符。
* host：URL字符串中的完整地址及端口号，该地址可能为一个IP地址，也可能为一个主机名。
* auth：URL字符串中的认证信息部分。
* hostname：URL字符串中的完整地址，该地址可能为一个IP地址，也可能为一个主机名。
* port：URL字符串中的端口号。
* pathname：URL字符串中的路径，不包含查询字符串。
* search：URL字符串中的查询字符串，包含起始字符“？”。
* path：URL字符串中的路径，包含查询字符串。
* query：URL字符串中的查询字符串，不包含起始字符“？”，或根据该查询字符串而转换的对象（根据parse方法所用参数而决定query属性值）。
* hash：URL字符串中的散列字符串，包含起始字符“#”。

#### parse方法

```js
url.parse(urlStr, [parseQueryString])
```

* urlStr参数用于指定需要转换的URL字符串
* parseQueryString参数为一个布尔类型的参数，当参数值为true时，内部使用Query String模块将查询字符串转换为一个对象，参数值为false时不执行该转换操作，默认参数值为false。

#### format方法

url模块中的format方法将URL字符串经过转换后的对象还原为一个URL字符串

```js
url.format(urlObj)
```

## 发送服务器端响应流

```js
server.on('request',function (request, response) {
    console.log(request.url);
    response.write("Hello nodeJs");
    response.end();
});
```

### writeHead方法来发送响应头信息

```js
response.writeHead(statusCode, [reasonPhrase], [headers]);
```

* statusCode参数用于指定一个三位的HTTP状态码，例如404
* reasonPhrase参数值为一个字符串，用于指定对于该状态码的描述信息
* 第三个参数值为一个对象，用于指定服务器端创建的响应头对象

在响应头中包含的一些常用字段如下所示:

* content-type：用于指定内容类型。
* location：用于将客户端重定向到另一个URL地址。
* content-disposition：用于指定一个被下载的文件名。
* content-length：用于指定服务器端响应内容的字节数。
* set-cookie：用于在客户端创建一个cookie。
* content-encoding：用于指定服务器端响应内容的编码方式。
* Cache-Control：用于开启缓存机制。
* Expires：用于指定缓存过期时间。
* Etag：用于指定当服务器端响应内容没有变化时不重新下载数据。

### setHeader方法单独设置响应头信息

```js
response.setHeader(name, value)
```

### write方法发送响应内容

```js
//参数值可以为一个Buffer对象或一个字符串
response.write(chunk, [encoding])
```

### end方法结束响应内容

```js
//参数与write方法参数的作用相同
response.end([chunk], [encoding])
```

## HTTP客户端

### 向其他网站请求数据

在http模块中，可以使用request方法向其他网站请求数据

```js
var request = http.request(options, callback);
//request方法返回一个http.ClientRequest对象，代表一个客户端请求
```

options参数值为一个对象或字符串，用于指定请求的目标URL地址，如果参数值为一个字符串，将自动使用url模块中的parse方法转换为一个对象

在options参数值对象或使用parse方法转换后的对象中，可以指定的属性及属性值如下所示：

* host：用于指定域名或目标主机的IP地址，默认属性值为“localhost”。
* hostname：用于指定域名或目标主机的IP地址，默认属性值为“localhost”。如果hostname属性值与host属性值都被指定，优先使用hostname属性值。
* port：用于指定目标服务器用于HTTP客户端连接的端口号。
* localAddress：用于指定专用于网络连接的本地接口。
* socketPath：用于指定目标Unix域端口。
* method：用于指定HTTP请求方式，默认属性值为“GET”。
* path：用于指定请求路径及查询字符串，默认属性值为“/”。
* headers：用于指定客户端请求头对象。
* auth：用于指定认证信息部分，例如“user：password”。
* agent：用于指定HTTP代理,在Node.js中，使用http.Agent类代表一个HTTP代理

callback参数来指定当获取到目标网站所返回的响应流时调用的回调函数

```js
function (response) {
    
}
```

也可以通过response事件进行监听并指定事件回调函数的方法来指定当获取到其他网站返回的响应流时执行的处理

```js
request.on('response', function(response){
    
});
```

write方法向目标网站发送数据

```js
request.write(chunk, [encoding])
```

end方法来结束本次请求

```js
request.end([chunk], [encoding])
```

终止本次请求

```js
request.abort();
```

监听错误

```js
request.on('error', function(err){
    
});
```

在建立连接的过程中，当为该连接分配端口时，触发http.ClientRequest对象的socket事件，可以通过对该事件进行监听并且指定事件回调函数的方法来指定当分配端口时所需执行的处理，该事件回调函数的指定方法如下所示。

```js
//socket参数值为用于分配的socket端口对象
request.on('socket', function(socket){
    
});
```

GET方式向其他网站请求数据

```js
http.get(options, callback)
```

## 制作代理服务器

## 创建HTTPS服务器与客户端