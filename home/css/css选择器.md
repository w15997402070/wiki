# CSS选择器

[toc]

## CSS选择器

### 标签选择器

根据HTML页面中的标签元素来选择

```css
body {
    margin: 8px;
    padding: 2px;
    font: normal 16px/1.6em 黑体;
}
```



### 类选择器

根据类名("."符号)类选择

```html
<div class="div_class">
    <h3 class="h3_class">h3类选择器</h3>
    <p class="p_class">p类选择器</p>
</div>
```

```css
.div {
    margin: 8px;
    padding: 2px;
}
```



### id选择器

根据HTML页面中定义的元素"id"值来选择

### 后代选择器

根据HTML页面中定义的特定元素的后代来选择

```html
<div>
    <ul>
        <li>第1级-1
        	<ol>
                <li>第2级-1</li>
                <li>第2级-2</li>
                <li>第2级-3
                    <ol>
                        <li>第3级-1</li>
                        <li><em>第3级-2</em></li>
                        <li>第3级-3</li>
                    </ol>
                </li>
            </ol>
        </li>
        <li>第1级-2</li>
        <li>第1级-3</li>
    </ul>
</div>
```

```css
ul {
    font: normal 14px/1.2em 宋体;
}
ul ol {
    font: italic 16px/1.6em 黑体;
}
ul em {
    font: bold 20px/1.6em yahei;
}
```



### 子选择器

根据HTML页面中定义的特定元素的直接后代来选择

子元素仅指第一级后代元素

CSS子选择器使用了符号"大于号(>)",即子结合符

同上面的HTML结构

```css
ul {
    font: normal 12px/1.2em 宋体;
}
ul > li {
    font: italic 16px/1.6em 黑体;
}
ol > li {
    font: bold 16px/1.6em 楷体;
}
```



### 相邻兄弟选择器

针对兄弟元素来选择

CSS相邻兄弟选择器是指可选择紧接在另一元素后的元素，且二者有相同的父元素。相邻兄弟选择器使用了符号“加号（+）”，即相邻兄弟结合符

```html
<body>
    <header>CSS相邻兄弟选择器</header>
    <div>
        <h3>CSS相邻兄弟选择器</h3>
        <h3>CSS相邻兄弟选择器</h3>
        <h3>CSS相邻兄弟选择器</h3>
        <p>CSS相邻兄弟选择器</p>
        <p>CSS相邻兄弟选择器</p>
        <p>CSS相邻兄弟选择器</p>
        <h4>CSS相邻兄弟选择器</h4>
        <h4>CSS相邻兄弟选择器</h4>
        <h4>CSS相邻兄弟选择器</h4>
    </div>
</body>
```

```css
h3 + p {
    font: bold 20px/1.6em yahei;
}
h4 + h4 {
    font: italic 16px/1.6em yahei;
}
```



### 通用选择器

使用"*"符号来选择

### 群组选择器

当若干元素样式属性一样时来选择

### 属性选择器

根据HTML页面中定义的元素属性来选择

#### 属性选择器

```html
<div>
    <h3>h3属性选择器</h3>
    <h3 title="selector">h3属性选择器</h3>
    <p>p属性选择器</p>
    <p title="selector">
        p属性选择器
    </p>
</div>
```

```css
[title] {
    font: normal 20px/1.6em yahei;
}
```

定义CSS属性选择器标记时需要使用符号“[]”将属性名称包含进去（[title]）页面中定义的标签元素包含有该属性，则CSS属性选择器就会将样式作用到该标签元素。

#### 属性和值选择器

```html
	<div>
        <h3>h3属性和值选择器</h3>
        <h3 title="attribute">h3属性和值选择器</h3>
        <p>h3属性和值选择器</p>
        <p title="value">h3属性和值选择器</p>
    </div>
```

```css
[title=value] {
    font: normal 20px/1.6em yahei;
}
```



#### 属性和多个值选择器

CSS规范为匹配多个属性值定义了两种方式：

* 一种方式适用于多个属性值用空格间隔的，CSS选择器定义方式为“[属性名称~=属性值]”，注意符号“~”的使用方式。

  定义的CSS属性和多个值选择器（[title~=value]）适用于多个属性值用`空格`间隔的

* 一种方式适用于属性值用连字符间隔的，CSS选择器定义方式为“[属性名称|=属性值]”，注意符号“|”的使用方式。

  定义的CSS属性和多个值选择器（[lang|=ch]）适用于多个属性值用`连字符`间隔的

#### 子串匹配属性选择器

* [属性名称^="属性值"]：使用“^”符号，则选择以"属性值"开头的所有元素。
* [属性名称$="属性值"]：使用“$”符号，则选择以"属性值"结尾的所有元素。
* [属性名称*="属性值"]：使用“*”符号，则选择包含"属性值"的所有元素。

```html
	<div>
        <p title="www.xxx.cn">p 子串匹配属性选择器</p>
        <p title="abc.yyy.com">p 子串匹配属性选择器</p>
        <p title="xxx.value.zzz">p 子串匹配属性选择器</p>
    </div>
```

```css
 		/*子串匹配属性选择器*/
        p[title^="www"] {
            font: bold 24px/2.0em yahei;
        }
        p[title$="com"] {
            font: italic 20px/1.6em yahei;
        }
        p[title*="value"] {
            font: normal 16px/1.6em yahei;
        }
```



#### 表单属性选择器

```html
	<form>
        <input type="text" value="输入框">
        <input type="button" value="按钮">
        <input type="password" value="密码框">
    </form>
```

```css
	input[type="text"] {
            margin: 8px;
            font: normal 16px/1.6em yahei;
    }
```



### 伪类选择器

用于向某些选择器添加特殊的效果

CSS伪类选择器包括多种形式：

* 锚伪类选择器:
* focus伪类选择器:
* first-child伪类选择器

#### 1．锚伪类选择器

```html
 	<div>
        <a href="http://www.w3c.org">css伪类选择器</a>
    </div>
```

```css
 	/*伪类选择器*/
    a:link {
        color: #FF0000;
        font: normal 12px/1.2em 宋体;
    }
    a:visited {
        color: #00FF00;
        font: normal 12px/1.2em 宋体;
    }
    a:hover {
        color: #FF00FF;
        font: normal 14px/1.4em 宋体;
    }
    a:active {
        color: #0000FF;
        font: normal 14px/1.2em 宋体;
    }
```

* a:link标记定义了第一个锚伪类，其中“:link”表示未被访问的链接地址。
* a:visited标记定义了第二个锚伪类，其中“:visited”表示已被访问的链接地址。
* a:hover标记定义了第三个锚伪类，其中“:hover”表示鼠标移动到链接地址上。
* a:active标记定义了第四个锚伪类，其中“:active”表示已链接地址被选定的状态。

#### 2．:focus伪类选择器

:focus伪类选择器用于设定标签元素获得输入焦点时的样式

```html
	<div>
        <input type="text">:focus 伪类选择器
    </div>
```

```css
	input:focus {
       background-color: lightgray;
    }
```



#### 3．:first-child伪类选择器

:first-child伪类选择器用于设定属于其父元素的首个子元素的样式。

```html
	<div>
        <p>:first-child伪类选择器</p>
        <p>:first-child伪类选择器</p>
        <p>:first-child伪类选择器</p>
    </div>
```

```css
	p:first-child {
        font: bold 16px/1.2em 黑体;
    }
```

`p:first-child`标记定义了一个`<p>`标签元素的：first-child伪类，其中“:first-child”表示属于其父元素的首个子元素。

### 伪元素选择器

用于向某些选择器设置特殊效果

## 选择器权值

1. 权值越大越优先

* 内联样式表的权值最高,值为1000
* id选择器的权值为100
* class类选择器的权值为10
* HTML标签选择器的权值为1

2. 权值相等时,后定义的样式表要优于先定义的样式表
3. 网页设计者设置的css样式优先级高于浏览器默认设置的CSS样式
4. 继承的CSS样式不如后来指定的CSS样式
5. 在同一组属性设置中标有"!important"规则的优先级最大