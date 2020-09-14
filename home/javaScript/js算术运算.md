### JavaScript

[toc]



## 算术运算

```js
Math.pow（2，53）//=>9007199254740992：2的53次幂
Math.round（.6）//=>1.0：四舍五入
Math.ceil（.6）//=>1.0：向上求整
Math.floor（.6）//=>0.0：向下求整
Math.abs（-5）//=>5：求绝对值
Math.max（x，y，z）//返回最大值
Math.min（x，y，z）//返回最小值
Math.random（）//生成一个大于等于0小于1.0的伪随机数
Math.PI//t：圆周率
Math.E/e：//自然对数的底数
Math.sqrt（3）//3的平方根
Math.pow（3，1/3）//3的立方根
Math.sin（0）//三角函数：还有Math.cos，Math.atan等
Math.1og（10）//10的自然对数
Math.1og（100）和 Math.LN10//以10为底100的对数
Math.1og（512）和 Math.LN2//以2为底512的对数
Math.exp（3）//e的三次幂
```

## 字符串方法

```js
vars="hello，world"//定义一个字符串
s.charAt（0）//=>"h"：第一个字符
s.charAt（s.1ength-1）//=>"d”：最后一个字符
s.substring（1，4）//=>"e11"：第2~4个字符
s.slice（1，4）//=>"el1"：同上
s.slice（-3）//=>"rld”：最后三个字符
s.indexOf（"1"）//=>2：字符1首次出现的位置
s.1astIndexOf（"1"）//=>10：字符1最后一次出现的位置
s.indexOf（"1"，3）//=>3：在位置3及之后首次出现字符1的位置
s.split（"，"）//=>["hello"，"world"]分割成子串
s.replace（"h"，"H"）//=>"He11o，world"：全文字符替换
s.toUpperCase（）//=>"HELLO，WORLD"
```

## 模式匹配

```js
var text="testing：1，2，3"；//文本示例
var pattern=/\d+/g  //匹配所有包含一个或多个数字的实例
pattern.test（text）//=>true：匹配成功
text.search（pattern）//=>9：首次匹配成功的位置
text.match（pattern）//=>["1"，"2"，"3"]：所有匹配组成的数组
text.replace（pattern，"#"）；//=>"testing:#，#，#”
text.split（/\D+/）；//=>[""，"1"，"2"，"3]：用非数字字符截取字符串
```

