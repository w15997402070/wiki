# 表格与列表

[toc]

## 表格

### 表格边框

CSS规范中使用`border`属性来设置表格边框，该属性可以在`<table>`、`<th>`和`<td>`等表格标签中使用。

语法 : `border: border-width border-style border-color;`

“border”属性的3个子属性的描述如下：

* border-width属性：用于规定边框的宽度。
* border-style属性：用于规定边框的样式。
* border-color属性：用于规定边框的颜色。



```html
    <table>
        <caption>通讯录</caption>
        <tr>
            <th>NO</th>
            <th>姓名</th>
            <th>性别</th>
            <th>年龄</th>
            <th>联系方式</th>
            <th>电子邮箱</th>
            <th>备注</th>
        </tr>
        <tr>
            <td>001</td>
            <td>张三</td>
            <td>男</td>
            <td>20</td>
            <td>1234567</td>
            <td>123@qq.com</td>
            <td>备注1</td>
        </tr>
        <tr>
            <td>002</td>
            <td>王五</td>
            <td>男</td>
            <td>21</td>
            <td>1234567</td>
            <td>456@qq.com</td>
            <td>备注2</td>
        </tr>
        <tr>
            <td>003</td>
            <td>李七</td>
            <td>女</td>
            <td>20</td>
            <td>12345678</td>
            <td>1789@qq.com</td>
            <td>备注3</td>
        </tr>
    </table>
```

```css
table,td,th {
    border: 1px solid darkgray;
}
```



### 表格折叠边框

CSS规范中使用`border-collapse`属性来设置表格折叠边框，该属性可以将表格的双线框样式合并为单线框样式。关于表格边框`border-collapse`属性的语法如下：

`border-collapse: seprate | collapse;`

其中，“border-collapse”属性的属性值描述如下：

* separate：默认值，表格边框会被分开。
* collapse：表格边框会合并为一个单一的边框。

```css
table {
    border-collapse: collapse;
}
```

### 表格内边距

在CSS规范中，可以通过为表格标签设置“padding”属性来定义表格内边距

```css
td {   
    padding: 10px;
}
```

### 表格宽度和高度

在CSS规范中，可以通过为表格标签设置“width”属性和“height”属性来定义表格宽度和高度。

```css
td {
    padding: 10px;
    width: 20px;
    height: 20px;
}
```

### 表格文本对齐

CSS规范中使用`text-align`属性和`vertical-align`属性来设置表格文本对齐方式。

其中，`text-align`属性用于定义文本“左对齐”“居中”和“右对齐”，`vertical-align`属性用于定义文本“顶部对齐”“居中对齐”和“底部对齐”。

```css
tr.td-left {
    text-align: left;
}
tr.th-center {
    vertical-align: center;
}
```

## CSS列表

### 列表标记类型

CSS规范中使用`list-style-type`属性来设置列表标记类型，该属性具有多个可用的属性值，下面列举几个比较常用的属性值：

* disc：默认值，标记是实心圆。
* circle：标记是空心圆。
* square：标记是实心方块。
* decimal：标记是数字。
* decimal-leading-zero：0开头的数字标记，例如：01、02、03……
* lower-roman：小写罗马数字（i、ii、iii、iv、v……）
* upper-roman：大写罗马数字（I、II、III、IV、V……）
* lower-alpha：小写英文字母（a、b、c、d、e……）
* upper-alpha：大写英文字母（A、B、C、D、E……）
* none ：无标记。

```html
<table>
    <tr>
        <td>
            <ul class="disc">
                <li >HTML</li>
                <li >CSS</li>
                <li >JavaScript</li>
            </ul>
            <ul class="circle">
                <li >HTML</li>
                <li >CSS</li>
                <li >JavaScript</li>
            </ul>
            <ul class="square">
                <li >HTML</li>
                <li >CSS</li>
                <li >JavaScript</li>
            </ul>
            <ul class="decimal">
                <li >HTML</li>
                <li >CSS</li>
                <li >JavaScript</li>
            </ul>
            <ul class="decimal-leading-zero">
                <li >HTML</li>
                <li >CSS</li>
                <li >JavaScript</li>
            </ul>
        </td>
    </tr>
</table>
```

```css
ul.disc {
    list-style-type: disc;
}
ul.circle {
    list-style-type: circle;
}
ul.square {
    list-style-type: square;
}
ul.decimal {
    list-style-type: decimal;
}
ul.decimal-leading-zero {
    list-style-type: decimal-leading-zero;
}
```

### 图片列表标记

CSS规范中使用`list-style-image`属性来设置图片列表标记，所谓图片列表标记就是指使用外部图片替代原始的列表标记