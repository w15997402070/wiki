# Mysql执行

![image-20200415154437280](D:\data\notes\notes\sql\mysql\mysql执行\image-20200415154437280.png)

查询处理的各个阶段：

* 1）FROM： 对FROM子句中的左表<left_table>和右表<right_table>执行笛卡儿积（Cartesian product），产生虚拟表VT1。
* 2) ON： 对虚拟表VT1应用ON筛选，只有那些符合<join_condition>的行才被插入虚拟表VT2中。
* 3）JOIN：如果指定了OUTER JOIN（如LEFT OUTER JOIN、RIGHT OUTER JOIN），那么保留表中未匹配的行作为外部行添加到虚拟表VT2中，产生虚拟表VT3。如果FROM子句包含两个以上表，则对上一个连接生成的结果表VT3和下一个表重复执行步骤1）～步骤3），直到处理完所有的表为止。
* 4）WHERE：对虚拟表VT3应用WHERE过滤条件，只有符合<where_condition>的记录才被插入虚拟表VT4中。
* 5）GROUP BY：根据GROUP BY子句中的列，对VT4中的记录进行分组操作，产生VT5。
* 6）CUBE | ROLLUP：对表VT5进行CUBE或ROLLUP操作，产生表VT6。
* 7）HAVING：对虚拟表VT6应用HAVING过滤器，只有符合<having_condition>的记录才被插入虚拟表VT7中。
* 8）SELECT：第二次执行SELECT操作，选择指定的列，插入到虚拟表VT8中。