# 1.* select下拉框获取值 *
$("#citySel option:selected").val();   //获取到下拉框被选中的value
$("#numbers option:selected").text();   //获取到下拉框被选中的optionde 文本内容

# 2. radio 获取值
var a = $("input[name='authorizationRegion']:checked").val();