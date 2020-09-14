1. 返回值统一  -->ResultBean.java
-  ResultBean/PageResultBean是controller专用的，不允许往后传！
-  Controller做参数格式的转换，不允许把json，map这类对象传到services去，也不允许services返回json、map。(待定...)
-  参数中一般情况不允许出现Request，Response这些对象
2.

