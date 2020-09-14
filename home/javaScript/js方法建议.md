
# js方法建议

## parseInt()方法

```js
parseInt("08",10) //结果为8，

parseInt("09",10) //结果为9。
```

因此，建议读者在使用parseInt时，一定要提供这个基数参数

## 判断数字

```js
isNaN(NaN)         // true
isNaN(0)           // false
isNaN('oops')      // true
isNaN('0')         // false
```

判断一个值是否可用做数字的最佳方法是使用isFinite函数
判断一个数字的函数

```js
var isNumber = function isNumber(value) {
    return typeof value === 'number' && isFinite(value);
}
```

## 访问对象属性的语法:

`objectName.propertyName`

###  Date 日期对象

```js
var Udate=new Date()； // 定义Udate变量为当前的日期，时间

var d = new Date(2012, 10, 1); // 设定当前日期为2012年10月1日

document.write(Udate.getFullYear()); //获取当前年份，输出为当前年份

Udate.setFullYear(2006); //将当前日期的年设置为2006年

同样的获取星期，设置星期的代码分别为 getDay(),setDay(),获取星期返回的数值为0~6,0代表周日

获取日的代码为 getDate(), 获取月的代码为getMonth

返回，设置时间的代码：get/setTime().

mydate.setTime(mydate.getTime() + 60 *60*1000);

//设置时间为 mydata所代表时间往后推迟一个小时，60分钟*60秒*1000毫秒

```

###  String 字符串对象



```js
var mystr = "Hello World!"; //定义字符串对象

var myl=mystr.length; // 获取字符串对象的长度，myl的值为12，空格也算进长度

var mynum=mystr.toUpperCase(); //将字符串中的所有字母转换为大写

mystr.charAt(2) //返回指定位置的字符，返回字符l

var str="I love JavaScript!"

document.write(str.indexOf("I") + "<br />"); //返回指定的字符串首次出现的位置,返回0

document.write(str.indexOf("v",8)); // 返回从位置8开始搜索，后边字符首次出现的位置，返回9

var mystr = "www.imooc.com";

document.write(mystr.split(".")+"<br>"); // 用.号将字符串分割成字符串数组，输出www,imooc,com

document.write(mystr.split(".", 2)+"<br>"); // 2代表分割的次数，输出www,imooc

var abc = mystr.split("."); // 变量abc为数组abc[www,imooc,com]

提取字符串substring()

var mystr="I love JavaScript";

document.write(mystr.substring(7)); //提取从位置7开始至结束为止的字符，输出JavaScript

document.write(mystr.substring(2,6)); //提取从位置2开始至6-1位置的字符，输出love

提取指定数目的字符substr() ，stringObject.substr(startPos,length)

var mystr="I love JavaScript!";

document.write(mystr.substr(7)); // 提取从位置7开始至结束为止的字符，输出JavaScript

document.write(mystr.substr(2,4)); // 提取从位置2开始往后的4个字符，输出love
```

### Math对象

```js
向上取整ceil()

document.write(Math.ceil(6.3) + "<br />") // 输出结果为7

向下取整floor()

document.write(Math.floor(-5.1)+ "<br>") // 输出结果为-6

四舍五入round()

document.write(Math.round(-6.4)+ "<br>"); // 输出结果为-6

document.write(Math.round(-6.6)); // 输出结果为-7

随机数 random()

document.write(Math.random()); // 随机输出0~1之间的一个数值

Array 数组对象

var 数组名= new Array(); //定义一个空数组

var 数组名 =new Array(n); //定义有n个空元素的数组

var myArray = [2, 8, 6]; //直接定义数组内容，myArray[0]=2

myArray.length；返回：myArray数组的长度

数组连接concat()

var mya=[1,2,3];

document.write(mya.concat(4,5)+"<br>"); // 将数组mya与4,5结合起来，输出1,2,3,4,5

var myb=[4,5,6]

var myc=[7,8,9]

document.write(mya.concat(myb,myc)+"<br>"); //将数组mya与数组myb，myc结合起来，输出1,2,3,4,5,6,7,8,9

指定分隔符连接数组元素join() // 把数组中的所有元素放入一个字符串

var myarr=["I","Love","Javascript"];

document.write(myarr.join()); // 括号内为空的话，输出用逗号隔开的各元素，输出I,Love,Javascript

document.write(myarr.join(".")); //输出用点号隔开的各元素，输出I.Love.Javascript

颠倒数组元素顺序reverse()

var mya=[1,2,3];

document.write(mya.reverse()) //输出3,2, 颠倒数组的元素

document.write(mya) // 输出3,2,1 该方法会改变原来的数组，而不会创建新的数组

选定元素slice(start，end) //不包括end位置的元素

var myarr = new Array(1,2,3,4,5,6);

document.write(myarr.slice(0,4) + "<br>"); //输出1，2，3，4

document.write(myarr); // 输出1，2，3，4，5,6 该方法不改变原来的数组，不创建新数组
```


 