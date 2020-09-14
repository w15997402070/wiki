```js
  $("form").attr("action");  //获取第一个form元素的action属性
  $("#icon").attr("src","icon.gif");  //设置src属性
  $("#banner").attr({ src : "banner.gif",
                      alt : "Advertisement",
                      width : 720,height : 64 });  //一次性设置4个属性
  $("a").attr("target","_blank");  //使所有链接在新窗口打开
  $("a").attr("target",function(){
                         if (this.host == location.host ){
                           return "_self";
                         }else{
                           return "_blank";
                         }
                       }
              );  //站内所有链接在本窗口打开,非站内链接在新窗口打开


$("a").attr("target",function(){});  //可以向这样传入函数
