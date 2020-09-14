# 分页插件不查询总数

先设置排序字段
PageHelper.startPage(model.getPage(), model.getPageSize(), model.getOrderBy());

再设置不查询总数(如果不用排序可以直接用下面这一句,如果要排序,这一句需要在上面一句下面)
PageHelper.startPage(model.getPage(), model.getPageSize(), false);