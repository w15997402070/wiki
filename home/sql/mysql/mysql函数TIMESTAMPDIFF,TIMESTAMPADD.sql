一，TIMESTAMPDIFF

语法：

TIMESTAMPDIFF(interval,datetime_expr1,datetime_expr2)。

说明：

返回日期或日期时间表达式datetime_expr1 和datetime_expr2the 之间的整数差。其结果的单位由interval 参数给出。该参数必须是以下值的其中一个：

FRAC_SECOND。表示间隔是毫秒
SECOND。秒
MINUTE。分钟
HOUR。小时
DAY。天
WEEK。星期
MONTH。月
QUARTER。季度
YEAR。年

mysql> select TIMESTAMPDIFF(day,'2012-08-24','2012-08-30');
+----------------------------------------------+
| TIMESTAMPDIFF(day,'2012-08-24','2012-08-30') |
+----------------------------------------------+
|                                            6 |
+----------------------------------------------+
1 row in set (0.00 sec)

SELECT TIMESTAMPDIFF(SECOND,DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'),'2017-07-07 15:43:00') FROM DUAL;
   --TIMESTAMPDIFF(SECOND,DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'),'2017-07-07 15:43:00')
   --2766137


   二，TIMESTAMPADD

   语法：
   TIMESTAMPADD(interval,int_expr,datetime_expr)

   说明：
   将整型表达式int_expr 添加到日期或日期时间表达式 datetime_expr中。式中的interval和上文中列举的取值是一样的。
   [sql] view plain copy
   mysql> select TIMESTAMPADD(MINUTE,8820,'2012-08-24 09:00:00');
   +-------------------------------------------------+
   | TIMESTAMPADD(MINUTE,8820,'2012-08-24 09:00:00') |
   +-------------------------------------------------+
   | 2012-08-30 12:00:00                             |
   +-------------------------------------------------+
   1 row in set (0.00 sec)
