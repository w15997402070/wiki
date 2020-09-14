# 基础样式

[toc]

## 背景样式

### 背景颜色

CSS规范中使用`background-color`属性来设定背景颜色，该属性可以被设定为任何合法的颜色值。

```css
backdroud: linear-gradient(45deg,red,green,blue);
```

` linear-gradient`渐变,第一个参数是角度,0deg代表从上到下,180deg代表从下到上,90从左到右,45,从左上角到右下角

### 背景图片

CSS规范中使用`background-image`属性来设定背景图像，但需要注意该属性是不可以被继承的。

如果需要使用`background-image`属性设置一个背景图像，则必须要为该属性设置一个URL值。当然，如果属性值为`none`，则代表不设置任何具体的图像。

```css
	body {
        background-image: url("images/image.png");
	}
```

### 背景重复

如果需要在页面中实现背景重复，可使用CSS规范中的`background-repeat`属性来实现，不过`background-repeat`属性会在水平和垂直两个方向上均执行重复效果。如果仅需要在水平或垂直方向上平铺，可以通过设置`repeat-x`或`repeat-y`属性值来实现。当然，如果使用`no-repeat`属性值，则代表不允许在任何方向上进行重复。

```html
	<div id="repeat-x">水平背景重复</div>
    <div id="repeat-y">垂直背景重复</div>
```

```css
    body {
        background-image: url("images/image.png");
        background-repeat: no-repeat;
    }
    div#repeat-x {
        width: auto;
        height: 100px;
        background-image: url("images/repeat.png");
        background-repeat: repeat-x;
    }
    div#repeat-y {
        width: auto;
        height: 200px;
        background-image: url("images/repeat.png");
        background-repeat: repeat-y;
    }
```

### 背景定位

如果需要在页面中实现背景定位，可使用CSS规范中的`background-position`属性来实现。`background-position`属性有“center”“left”“top”“right”和“bottom”共5个属性值，分别可以实现“居中”“靠左”“顶部”“靠右”和“底部”5个方向的定位。

```css
    div#repeat-y {
        width: auto;
        height: 200px;
        background-image: url("images/repeat.png");
        background-repeat: repeat-y;
        background-position: center;
    }
```

### 固定背景位置

一般情况下，在页面文档比较长时，浏览器会自动出现滚动条。此时，页面中的背景会随着文档向下滚动的同时，滚动条也会随之滚动。当滚动条位置超出背景的位置时，背景就不会在浏览器中显示

但在有些特殊的情况下，设计网页时需要将背景固定在页面中某个位置不动，也就是说背景不会随着浏览器滚动条而滚动，这就是我们所说的固定背景位置。在CSS规范中可以通过`background-attachment`属性来固定背景位置（设定属性值为“fixed”）。



### 相关知识

* 背景大小: `background-size: auto;`
* 背景位置: `background-position: center;`

## 字体样式

### 字体系列

* 通用字体系列：指拥有相似外观的字体系统组合（比如："Serif"就是通用字体系列中的一种）。

  CSS规范一共定义了5种通用字体系列，具体如下：

  * Serif字体
  * Sans-serif字体
  * Monospace字体
  * Cursive字体
  * Fantasy字体

* 特定字体系列：指具体的字体系列（比如："Times" "Courier" "Georgia"等，就是特定字体系列）。

在CSS规范中，使用`font-family`属性来定义字体系列，也可以将字体系列直接定义在通用的“font”属性中，具体使用就看个人喜好了。

```html
    <div>
        <p class="p-serif">CSS-字体系列(Serif)</p>
        <p class="p-sans-serif">CSS-字体系列(Sans-Serif)</p>
        <p class="p-monospace">CSS-字体系列(Monospace)</p>
    </div>
```

```css
    p.p-serif {
        font-family: Serif;
    }
    p.p-sans-serif {
        font-family: sans-serif;
    }
    p.p-monospace {
        font-family: monospace;
    }
```

`font-family`可以定义多个,浏览器会依次去查找字体,如果有就使用,例如:

```css
p.text-font-family {
    font-family: "PingFang SC","Microsoft Yahei",monospace
}
```

`"PingFang SC"`是Mac上的字体,如果是Mac就显示它

`"Microsoft Yahei"`是windows上的字体,如果是Windows就会用它

monospace是等宽字体,如果上面两个都没找到,就会随便找一个等宽字体展示,所以一般把特定平台使用的字体放前面

monospace 在CSS设置时不能加双引号,具体的字体要加双引号例如`"Microsoft Yahei"`

### 使用服务器端字体——Web Font与@font-face属性

  CSS 3中，新增了Web Fonts功能，使用这个功能，网页中可以使用安装在服务器端的字体，只要某个字体在服务器端已经安装，网页中就都能够正常显示了。

```css
@font-face {
    /*声明使用服务端字体*/
    font-family: WebFont;
    /*指定服务端字体的位置路径,在format属性值中声明字体文件的格式(可以省略)*/
    src: url('font/Fontin_Sans_R_45b.otf') format("opentype");
    font-weight: normal;
}
```

#### 显示客户端本地的字体

使用`@font-face`属性显示客户端本地的字体时，需要将字体文件路径的URL属性值修改为“local（）”形式的属性值，并且在“local”后面的括号中写入使用的字体。

```css
@font-face {
    font-family: Arial;
    src: local("Arial");
}
@font-face {
    font-family: Arial;
    font-weight: bold;
    src: local("Arial Black");
}
div {
    font-family: Arial;
    font-size: 40px;
}
```

浏览器将首先在客户端本地寻找是否存在Helvetica Neue字体，如果存在则直接使用，如果不存在则使用服务器端的MyHelvetica字体。

```css
@font-face {
    font-family: MyHelvetica;
    src: local("Helvetica Neue"),
    url(MgOpenModernaRegular.ttf);
}
```

#### 自定义字体

```css
@font-face {
    font-family: "IF";
    src: url("./IndieFlower.ttf");
}
.custom-font {
    font-family: IF;
}
```

src中可以引用远程网络上的字体,应用远程时注意跨域问题

### 字体风格

在CSS规范中，总体上定义了三种不同类型的字体风格，具体描述如下：

* 正常字体风格（normal）：文本字体正常显示。
* 斜体字体风格（italic）：文本字体斜体显示。
* 倾斜字体风格（oblique）：文本字体倾斜显示。

使用`font-style`定义字体风格

```css
    p.p-serif {
        font-family: Serif;
        font-style: italic;
    }
```

"inherit"表示继承其父级标签元素的字体风格

### 字体加粗

在CSS规范中，通过“font-weight”属性来定义字体加粗。在使用“font-weight”属性时，可以为其设定不同的属性值，具体描述如下：

* 正常（normal）：文本字体正常加粗度。
* 加粗（bold）：文本字体加粗。
* 偏移（bolder & lighter）：文本字体加粗程度向上或向下偏移。
* 加粗度（100～900）：数值100～900为字体指定了9级加粗度。数值100对应最细的字体变形，数值900对应最粗的字体变形。数值400对应normal，而数值700对应bold。

```html
    <div>
        <p class="p-weight400">CSS-字体加粗(400)</p>
        <p class="p-bold">CSS-字体加粗(bold)</p>
    </div>
```

```css
    .p-weight400{
        font-weight: 400;
    }
    .p-bold {
        font-weight: bold;
    }
```

### 字体变形

在CSS规范中，通过`font-variant`属性来定义字体变形。所谓字体变形就是将英文字母全部设定为小型的大写字母，其与英文大写字母是不同的。在使用`font-variant`属性时，可以为其设定不同的属性值，具体描述如下：

正常（normal）：正常文本字体。

变形（variant）：文本字体显示为小型的大写字母。

```html
    <div>
        <p class="p-normal">CSS-字体变形 (normal)</p>
        <p class="p-variant">CSS-字体变形 (variant)</p>
    </div>
```

```css
    .p-normal {
        font-variant: normal;
    }
    .p-variant {
        font-variant: small-caps;
    }
```

### 字体大小

在CSS规范中，通过`font-size`属性来定义字体大小。在使用`font-size`属性时，可以为其设定不同类型的属性值，具体描述如下：

预定义值：medium、large、small、x-large、x-small等。

像素值（px）：“px”代表固定的像素大小。

相对值（em）：“em”代表相对于当前字体大小的比例，例如：“2em”代表2倍于当前字体尺寸。

百分比（%）：百分比（%）代表相对于父级元素的字体尺寸大小。

另外，在CSS定义中“1em”等于固定像素尺寸16px，且浏览器<body>标签元素的默认字体大小就是16个像素（16px）。

## 文本样式

### 文本对齐方式

在CSS规范中，通过`text-align`属性来定义文本对齐方式。在使用`text-align`属性时，可以为其设定不同的属性值，具体描述如下：

左对齐（left）：文本向左对齐效果。

右对齐（right）：文本向右对齐效果。

居中（center）：文本居中对齐效果。

### 文本缩进

在CSS规范中，通过`text-indent`属性来定义文本缩进，文本缩进主要用HTML文档中段落的首行缩进格式化效果。在使用`text-indent`属性时，可以为其设定正值、负值或百分比。

### 文本间隔

在CSS规范中，文本间隔包括字间隔和字母间隔两种类型。

所谓字间隔就是指单词之间的间隔，字间隔通过`word-spacing`属性来定义

而字母间隔则指字符或字母之间的间隔。字母间隔则通过`letter-spacing`属性来定义。

在使用`word-spacing`属性和`letter-spacing`属性时，可以为其设定任意的数值，包括正值、零和负值，具体描述如下：

正值：增加间隔。

零：正常的标准间隔，相当于normal。

负值：缩小间隔。

```html
    <div>
        <p class="p-word-spacing-positive">CSS-字间隔(word-spacing)</p>
        <p class="p-word-spacing-negative">CSS-字间隔(word-spacing)</p>
        <p class="p-letter-spacing-positive">CSS-字母间隔(letter-spacing)</p>
        <p class="p-letter-spacing-negative">CSS-字母间隔(letter-spacing)</p>
    </div>
```

```css
    p.p-word-spacing-positive {
        word-spacing: 50px;
    }
    p.p-word-spacing-negative {
        word-spacing: -0.5em;
    }
    p.p-letter-spacing-positive {
        letter-spacing: 4px;
    }
    p.p-letter-spacing-negative {
        letter-spacing: -4px;
    }
```

### 文本修饰

​	在CSS规范中，通过`text-decoration`属性来定义文本修饰方式。

在使用`text-decoration`属性时，可以为其设定不同的属性值，

具体描述如下：

* none：正常文本，没有修饰。
* underline：下划线修饰。
* overline：上划线修饰。
* line-through：贯穿线修饰。
* blink：文本闪烁修饰。

### 文本方向

在CSS规范中，通过`direction`属性来定义文本方向，所谓文本方向就是“从左开始”或“从右开始”的文本。在使用`direction`属性时，可以为其设定不同的属性值，具体描述如下：

左方向（ltr）：从左开始的文本方向。

右方向（rtl）：从右开始的文本方向。

### 处理文本空白符

在CSS规范中，通过`white-space`属性来定义文本空白符的处理方式。在使用`white-space`属性时，可以为其设定不同的属性值，具体描述如下：

* normal：使用该值会将换行字符（回车）转换为空格，会将一行中多个空格的序列也会转换为一个空格。
* pre：使用该值会保留原文本中的空白符和换行字符（回车）。
* nowrap：使用该值会阻止原文本进行换行。

```html
	<div>
        <p class="p-white-space-normal">
            CSS - 处理文本
        空白符
        (White-space)
        </p>
        <p class="p-white-space-pre">
            CSS - 处理文本
            空白符
            (White-space)
        </p>
        <p class="p-white-space-nowrap">
            CSS - 处理文本
            空白符
            (White-space)
        </p>
    </div>
```

```css
    .p-white-space-normal {
        white-space: normal;
    }
    .p-white-space-pre {
        white-space: pre;
    }
    .p-white-space-nowrap {
        white-space: nowrap;
    }
```

## 文本美化

### 文本阴影

在CSS3规范中，通过`text-shadow`属性来定义文本阴影效果。

关于`text-shadow`属性的语法如下：

`text-shadow: h-shadow v-shadow blur color`

* h-shadow：表示水平阴影，该值必选。
* v-shadow：表示垂直阴影，该值必选。
* blur：模糊距离，该值可选。
* color：阴影颜色，该值可选。

```html
	<div>
        <p class="p-text-shadow-style1">CSS3 - 文本阴影效果(text-shadow)</p>
        <p class="p-text-shadow-style2">CSS3 - 文本阴影效果(text-shadow)</p>
        <p class="p-text-shadow-style3">CSS3 - 文本阴影效果(text-shadow)</p>
        <p class="p-text-shadow-style4">CSS3 - 文本阴影效果(text-shadow)</p>
        <p class="p-text-shadow-style5">CSS3 - 文本阴影效果(text-shadow)</p>
    </div>
```

```css
    .p-text-shadow-style1 {
        text-shadow: 0px 0px 8px #888;
    }
    .p-text-shadow-style2 {
        text-shadow: 4px 4px 8px #666;
    }
    .p-text-shadow-style3 {
        text-shadow: 0px 16px 8px #ccc;
    }
    .p-text-shadow-style4 {
        text-shadow: 16px 0px 8px #ccc;
    }
    .p-text-shadow-style5 {
        text-shadow: 16px 16px 0px #ccc;
    }
```

### 文本溢出

在CSS3规范中，通过`text-overflow`属性来定义文本溢出效果。在使用`text-overflow`属性时，可以为其设定不同的属性值，具体描述如下：

* ellipsis：显示省略符号来代表被修剪的文本。

* clip：对文本进行修剪。

* string：使用给定的字符串来代表被修剪的文本。

```html
<div>
    <p class="p-text-overflow-ellipsis">CSS3 - 文本溢出效果(text-overflow)</p>
    <p class="p-text-overflow-clip">CSS3 - 文本溢出效果(text-overflow)</p>
    <p class="p-text-overflow-string">CSS3 - 文本溢出效果(text-overflow)</p>
</div>
```

```css
    .p-text-overflow-ellipsis {
        overflow: hidden;
        white-space: nowrap;
        width: 120px;
        text-overflow: ellipsis;
    }
    .p-text-overflow-clip {
        overflow: hidden;
        white-space: nowrap;
        width: 120px;
        text-overflow: clip;
    }
    .p-text-overflow-string {
        overflow: hidden;
        white-space: nowrap;
        width: 120px;
        /*在chrome显示不了,在Firefox可以*/
        text-overflow: "****";
    }
```

overflow,overflow-x和overflow-y

visible：默认值。表示不剪切容器中的任何内容、不添加滚动条，元素将被剪切为包含对象的窗口大小，而且clip属性设置将失效。

auto：在需要时剪切内容并添加滚动条。也就是说当内容超过容器的宽度或者高度时，溢出的内容将会隐藏在容器中，并且会添加滚动条，用户可以拖动滚动条查看隐藏在容器中的内容。

hidden：内容溢出容器时，所有内容都将隐藏，而且不显示滚动条。

scroll：不管内容有没有溢出容器，

overflow-x都会显示横向的滚动条，而overflow-y会显示纵向的滚动条。

no-display：当内容溢出容器时不显示元素，此时类似于元素添加了display:none声明一样。

no-content：当内容溢出容器时不显示内容，此时类似于添加了visibility:hidden声明一样

### 文本边框轮廓

在CSS3规范中，通过`outline`属性来定义文本边框轮廓效果。关于`outline`属性的语法如下：

`text-outline: outline-color outline-style outline-width;`

* outline-color：定义文本边框轮廓的颜色，“outline-color”可以直接作为属性使用。

* outline-style：定义文本边框轮廓的样式，“outline-style”可以直接作为属性使用，

  其属性值如下：

  * none ：默认值，定义无边框轮廓。
  * dotted：定义点状的边框轮廓。
  * dashed：定义虚线边框轮廓。
  * solid：定义实线边框轮廓。
  * double：定义双线边框轮廓。

* outline-width：定义文本边框轮廓的宽度，“outline-width”可以直接作为属性使用，

  其属性值如下：

  * thin：定义细边框轮廓。
  * medium：默认值，定义中等的边框轮廓。
  * thick：定义粗的边框轮廓。
  * length：定义边框轮廓粗细的值。

```html
<div>
    <p class="p-text-shadow-style1">CSS3 - 文本阴影效果(text-shadow)</p>
    <p class="p-text-shadow-style2">CSS3 - 文本阴影效果(text-shadow)</p>
    <p class="p-text-shadow-style3">CSS3 - 文本阴影效果(text-shadow)</p>
    <p class="p-text-shadow-style4">CSS3 - 文本阴影效果(text-shadow)</p>
    <p class="p-text-shadow-style5">CSS3 - 文本阴影效果(text-shadow)</p>
</div>
```

```css
    .p-text-shadow-style1 {
        text-shadow: 0px 0px 8px #888;
        outline: #888 solid thin;
    }
    .p-text-shadow-style2 {
        text-shadow: 4px 4px 8px #666;
        outline: #888 double medium;
    }
    .p-text-shadow-style3 {
        text-shadow: 0px 16px 8px #ccc;
        outline: #888 solid thick;
    }
    .p-text-shadow-style4 {
        text-shadow: 16px 0px 8px #ccc;
        outline: #888 dotted thin;
    }
    .p-text-shadow-style5 {
        text-shadow: 16px 16px 0px #ccc;
        outline: #888 dashed thin;
    }
```

### 让文本自动换行

在CSS 3中，使用`word-break`属性来让文字自动换行

word-break属性可以使用的值

* normal : 使用浏览器默认换行规则
* keep-all: 只能在半角空格或连字符处换行
* bbreak-all : 允许在单词内换行

在某些浏览器中不支持`keep-all`属性

### 让长单词与URL地址自动换行

在CSS 3中，使用`word-wrap`属性来实现长单词与URL地址的自动换行

### 指定用户是否可选取文字的user-select属性

user-select属性用于指定用户是否可通过拖拽鼠标来选取元素中的文字，如果样式属性值被指定为none则禁止选取。可使用的样式属性值如下所示：

* none：禁止选取。

* text：可以选取。

* all：只能选取全部文字。如果双击子元素，那么被选取的部分将是以该子元素向上回溯的最高祖先元素。

* element：可以选取文字，但选择范围受元素边界的约束。

  ```html
  <div>
      <p class="text-user-select">用户不得选取这段文字</p>
  </div>
  ```

  ```css
  .text-user-select {
      padding: 10px;
      -webkit-user-select: none;
      -moz-user-select: none;
      user-select: none;
      background: #eee;
  }
  ```



## 颜色样式

### 十六进制颜色

在CSS规范中，十六进制颜色是最常用的一种定义方法，且能够兼容绝大多数的主流浏览器。关于十六进制颜色值定义的语法如下：

`#RRGGBB`

其中，“#”符号表示十六进制数值，RR（红色）、GG（绿色）、BB（蓝色）十六进制整数表示颜色的组成，所有的颜色值必须介于#000000与#FFFFFF之间。

### RGB颜色

在CSS规范中，RGB颜色是另一种最常用的定义方法，且同样能够兼容绝大多数的主流浏览器。关于RGB颜色值定义的语法如下：

`rgb(red,green,blue)`

其中，“rgb()”函数用于定义颜色值，red（红色）、green（绿色）、blue（蓝色）十进制整数表示颜色的组成，所有的颜色值必须介于0与255之间；或者介于0%与100%之间。

例如 : `rgb(240,240,240)`,`rgb(64,64,64)`

### RGBA颜色

在CSS规范中，RGBA颜色是对RGB颜色的功能扩展，其增加了一个通道用于定义颜色的不透明度。关于RGBA颜色值定义的语法如下:

`rgba(red,green,blue,alpha)`

其中，“rgb()”函数用于定义颜色值。Red（红色）、green（绿色）、blue（蓝色）十进制整数表示颜色的组成，所有的颜色值必须介于0与255之间，或者介于0%与100%之间。alpha参数是介于0（完全透明）与1.0（完全不透明）的数值。

例如: `rgba(64,64,64,0.5)`

### HSL

通过对色调（H）、饱和度（S）、亮度（L）三个颜色通道的变化以及它们相互之间的叠加得到各式各样的颜色的

H：色调（Hue）。取整数值（<length>），可以为任意整数，其中0（或360或-360）表示红色，60表示黄色，120表示绿色，180表示青色，240表示蓝色，300表示洋红。当它们的值大于360时，实际的值等于该值除360之后的余数。例如，如果色调的值是480，则实际的颜色值为480除以360之后得到的余数120。

S：饱和度（Saturation）。就是颜色的深浅度和鲜艳程序，取百分数（<percentage>），可以取值0～100%之间的任意值，其中0表示灰度（没有该颜色），100%表示饱和度最高（该颜色最鲜艳）。

L：亮度（Lightness）。取值和饱和度（S）一样，可以取值0～100%之间的任意值，其中0最暗（黑色），100%最亮（白色）。

## 其他

通过opacity属性能够使任何元素呈现出半透明效果

```css
opacity: alphavalue || inherit
```

alphavalue：默认值为1，可以取0～1任意浮点数。其中取值为1时，元素是完全不透明的；反之，取值为0时，元素是完全透明不可见。其值不可以是负值。

inherit：表示继承父元素的opacity设置的值，即继承父元素的不透明性。