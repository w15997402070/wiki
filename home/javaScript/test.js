function insertAt(parent,child,n){
  if( n < 0 || n > parent.childNodes.length){
    throw new Error("invalid index");
  }else if (n == parent.childNodes.length ) {
    parent.appendChild(child);
  }else{
    parent.insertBefore(child,parent.childNodes[n]);
  }
}

function sortRows(table,n,comparator){
  var tbody = table.tbodies[0];
  var rows = tbody.getElementsByTagName("tr");
  rows = Array.prototype.slice.call(rows,0);

  rows.sort(function(row1,row2){
    var cell1 = row1.getElementsByTagName("td")[n];
    var cell2 = row2.getElementsByTagName("td")[n];
    var val1 = cell1.textContent || cell1.innerText;
    var val2 = cell2.textContent || cell2.innerText;
    if (comparator) {
      return comparator(val1,val2);
    }

    if (val1 < val2 ){ return -1 ;}
    if (val1 > val2) { return 1 ; }
    else{ return 0 ; }
  })
  //在<tbody>中按他们的顺序把行添加到最后
  //这将自动把他们移走而没必要预先删除它们
  //如果<tbody>中还包含了除了<tr>的任何其他元素,这些节点将会悬浮到顶部
  for (var i = 0; i < rows.length; i++) {
    tbody.appendChild(rows[i]);
  }
}

//查找表格的<th>元素(假设只有一行),让他们可单击
//以便单击列标题,按该列对行排序
function makeSortable(table){
  var headers = table.getElementsByTagName("th");
  for (var i = 0; i < headers.length; i++) {
     (function{  //嵌套函数来创建本地作用域
       headers[i].onclick = function () { sortRows(table,n);}
     }(i));  //将i的值赋给局部变量
  }
}
