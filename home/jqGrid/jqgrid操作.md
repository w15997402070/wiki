# jqgrid 基本操作

## 冻结列

```js
// frozen:true 表示冻结列  (冻结列只能从前面的列开始,而且不能跨列)
colModel :[
      {label: '年份', name: 'subYear', index: 'subYear', width: 40,frozen:true,align:"left"},
]
//再加上下面这一行
jQuery("#jqGrid").jqGrid('setFrozenColumns');
```

冻结列出现的问题 : 

* 高度不一致问题

![1567763813455](D:\data\notes\notes\jqGrid\jqgrid操作.assets\1567763813455.png)

解决 : 

```js
 loadComplete:function(){
            setTimeout(function () {
                 hack()
            }, 50)
        }
//    hack 固定列表格对齐
    function hack() {
        $('.frozen-bdiv.ui-jqgrid-bdiv').css('top', $('.frozen-div').height() + 'px');
        $('#jqGrid_frozen tr:not(".jqgfirstrow")').each(function (i, v) {
            $(v).css('height', $('#jqGrid tr:not(".jqgfirstrow")').eq(i).height() + 'px');
        });
        $('.ui-jqgrid .ui-jqgrid-bdiv').css('height', 'auto');
    }
```

* 出现空白列问题

  ![空白列](D:\data\notes\notes\jqGrid\jqgrid操作.assets\1567763994203.png)

解决 : 调整冻结列左右两边的宽度,直到不出现空白列

![1567764101308](D:\data\notes\notes\jqGrid\jqgrid操作.assets\1567764101308.png)

## 设置序号列名称和宽度

```js
//设置序号列名称
jQuery("#jqGrid").jqGrid('setLabel',0, '序号', '');

//设置序号列
rownumbers: true,
//设置宽度
rownumWidth : 50
```

## 一般操作

```js
//获取多行数据
var ids = $("#jqGrid").jqGrid('getGridParam', 'selarrrow');
//获取行数据
var rowData = $("#jqGrid").jqGrid('getRowData', ids[i]);
//获取行jquery对象
var $tr = $("#jqGrid").find('#'+ids[i]);
```



## 参数清除操作

```js
//第一次调用
var data =  {type : 1};
function reloadJqGrid(data){
     $("#jqGrid").jqGrid('setGridParam', {
                postData: data,
                page: 1
     }).trigger("reloadGrid");   
}

reloadJqGrid(data);

//第二次调用
var data2 = {type :2 };

reloadJqGrid(data2);
//第三次调用
var data3 = { };

reloadJqGrid(data2);

```

上面操作中 

第一次调用时 `data` 会把` type = 1 `参数传到后台,

第二次调用时 `data` 中 `type = 2 `会覆盖第一次参数,参数也会传到后台

第三次调用时 `data` 中 没有type字段,而第二次的参数 `type = 2` 还是传到了后台 

但是第三次没有type字段也就是不需要type字段传到后台,这样就需要下面的设置:

加上`true`参数

```js
function reloadJqGrid(data){
     $("#jqGrid").jqGrid('setGridParam', {
                postData: data,
                page: 1
     },true).trigger("reloadGrid");   
}
reloadJqGrid(data);

//第二次调用
var data2 = {type :2 };

reloadJqGrid(data2);
//第三次调用
var data3 = {};

reloadJqGrid(data2);
```

同样三次调用,加了true字段之后,每次都会把前面的字段清空.