###DOM对象
1.document.getElementsByName();//返回带有指定名称的节点的集合
    1. 因为文档中的 name 属性可能不唯一，所有 getElementsByName() 方法返回的是元素的数组，而不是一个元素。
    2. 和数组类似也有length属性，可以和访问数组一样的方法来访问，从0开始。

2.getElementsByTagName()方法;//返回带有指定标签名的节点对象的集合。返回元素的顺序是它们 在文档中的顺序。
    1. Tagname是标签的名称，如p、a、img等标签名。
    2. 和数组类似也有length属性，可以和访问数组一样的方法来访问，所以从0开始。
