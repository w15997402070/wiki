# Sql

[toc]



## 集合操作

集合操作主要包括并、交、差三种，为了合并多个SELECT语句的结果，可以使用集合操作符UNION，UNION ALL，INTERSECT和MINUS

### 1．UNION和UNION ALL操作(并)

UNION操作符用于获取两个或多个结果集的并集，当使用该操作符时，会自动去掉结果集中的重复行。

UNION ALL操作符返回查询所检索出的所有行，包括重复的行。



如果使用ORDER BY子句进行排序，该子句只出现在最后一个查询的后面。

例如:

```sql
SELECT ENAME, DNAME, JOB  FROM EMP E, DEPT D WHERE E.DEPTNO = D.DEPTNO AND DNAME =  'SALES'
UNION 
SELECT ENAME, DNAME, JOB FROM EMP E, DEPT D WHERE E.DEPTNO = D.DEPTNO AND JOB = 'MANAGER'
ORDER BY DNAME

-- 结果
ENAME   DNAME      JOB
CLARK	ACCOUNTING	MANAGER
JONES	RESEARCH	MANAGER
ALLEN	SALES	SALESMAN
BLAKE	SALES	MANAGER
JAMES	SALES	CLERK
MARTIN	SALES	SALESMAN
TURNER	SALES	SALESMAN
WARD	SALES	SALESMAN
```



### 交（INTERSECT）操作

INTERSECT操作符用于获取两个结果集的交集。当使用该操作符时，只会显示同时存在于两个结果集中的数据。

```sql
select ename, sal,job from emp where sal > 2500
intersect
select ename, sal, job from emp where job = 'MANAGER'

ENAME   SAL     JOB
BLAKE	2850	MANAGER
JONES	2975	MANAGER
```

### 差（MINUS）操作

MINUS操作符用于获取两个结果集的差集。当使用该操作符时，只会显示第一个结果集中存在，第二个结果集中不存在的数据。

例如 : 

下面以显示工资高于2500但岗位不是“MANAGER”的雇员

```sql
SELECT ENAME, SAL, "JOB" FROM EMP WHERE SAL > 2300 
MINUS
SELECT ENAME, SAL, "JOB" FROM EMP WHERE "JOB"='MANAGER'

-- 结果
ENAME   SAL     JOB
FORD	3000	ANALYST
KING	5000	PRESIDENT
SCOTT	3000	ANALYST
```

## 子查询

## 表连接

```sql
SELECT DNAME, ENAME FROM EMP, DEPT 
WHERE EMP.ENAME = 'SMITH'

--结果
DNAME       ENAME
ACCOUNTING	SMITH
RESEARCH	SMITH
SALES	SMITH
OPERATIONS	SMITH

-- 可以看出,一个人出现在了多个部门,为什么?
```

FROM子句中指定两张表，则这两张表就会合并在一起进行查询。两张表的合并算法叫做笛卡儿乘积（类似于两个矩阵相乘）。笛卡儿乘积的算法是，将表A的第一行与表B的所有行分别合并，得到与表B的行数相等的一系列新行；然后将表A的第二行与表B的所有行分别合并，得到与表B的行数相等的一系列新行……依此类推，直至将表A的最后一行与表B的所有行分别合并，得到与表B的行数相等的一系列新行

在FROM子句中指定了两表EMP和DEPT后合并得到的结果是，存在一张逻辑上的大表（两个表的笛卡儿乘积的结果，但是物理上并不存在），其列数等于两张表中的各个列数相加。

其行数等于两张表的行数相乘（下面的COUNT(*)函数用于返回符合条件的所有行数）

```sql
SELECT "COUNT"(*) FROM EMP;
-- 结果为 14

SELECT "COUNT"(*)FROM DEPT;
--结果为 4

SELECT "COUNT"(*)FROM EMP, DEPT;
--结果为 56
```

## CASE语句的使用

有两种类型的CASE语句。

* 简单CASE语句，使用表达式确定返回值。
*  搜索CASE语句，使用条件确定返回值

### 1．使用简单CASE语句

简单CASE表达式使用表达式确定返回值，语法如下：

```sql
CASE search_expression
	WHEN expression1 THEN result1
	WHEN expression2 THEN result2
	...
	WHEN expressionN THEN resultN
	ELSE default_result
END
```

* search_expression是待求值的表达式。
*  expression1, expression2, ..., expressionN是要与search_expression进行比较的表达式。
*  result1, result2, ..., resultN是（每一个可能的表达式所对应的）返回值。如果expression1的值等于search_expression，则返回result1，依此类推。
* default_result是无法找到相匹配的表达式时的默认返回值。

例如 : 

```sql
SELECT product_id, product_type_id,
  CASE product_type_id 
      WHEN 1 THEN 'Book'
      WHEN 2 THEN 'Video'
      WHEN 3 THEN 'DVD'
      WHEN 4 THEN 'CD'
      ELSE 'Magazine'
  END
FROM products;
```

### 2．使用搜索CASE语句

搜索CASE语句使用相关的条件来确定返回值，其语法如下。

```sql
CASE 
	WHEN condition1 THEN result1
	WHEN condition2 THEN result2
	...
	WHEN conditionN THEN resultN
	ELSE default_result
END
```

* condition1, condition2, ..., conditionN是待求值的表达式
* result1, result2, ..., resultN是返回值（对应每个可能出现的条件）。如果condition1为真，则返回result1，依次类推。
*  default_result是当没有为真的条件时返回的默认结果。

例如 : 

```sql
SELECT product_id, product_type_id,
  CASE 
      WHEN product_type_id = 1 THEN 'Book'
      WHEN product_type_id = 2 THEN 'Video'
      WHEN product_type_id = 3 THEN 'DVD'
      WHEN product_type_id = 4 THEN 'CD'
      ELSE 'Magazine'
  END
FROM products;
```

## 强大的DECODE函数

DECODE（value, search_value, result, default_value）对value与search_value进行比较，如果这两个值相等，DECODE()返回result，否则，就返回default_value

例如 : 

```sql
SELECT "DECODE"(1, 1, 2, 3) FROM dual;
-- 结果为 2 

SELECT "DECODE"(1, 2, 2, 3) FROM dual;
-- 结果为 3 
```

例 :

```sql
SELECT prd_id, available,
	DECODE(available, 'Y', 'Product is available', 'Product is not available')
FROM more_products;
```

### DECODE()传递多个搜索和结果参数

可以向DECODE()传递多个搜索和结果参数，如下例所示。

```sql
SELECT product_id, product_type_id, 
	DECODE(product_type_id,
          1, 'Book',
          2, 'Video',
          3, 'DVD',
          4, 'CD',
          'Magazine')
FROM products;
```

## 基本函数

###  字符函数

#### 	1．ASCII(c1)

其中，c1表示一个字符串，该函数返回c1第一个字母的ASCII码，其逆函数是CHR()。例如：

```sql
SELECT "ASCII"('AS') FROM dual;
-- 结果
ASCII_A
65
```

#### 2．CHR(i)

其中，i表示一个数字，该函数返回十进制表示的字符。

```sql
SELECT "CHR"(66) FROM dual;
-- 结果
"CHR"(66)
B
```

#### 3．CONCAT(c1,c2)

CONCAT(c1,c2)其中，c1,c2均为字符串，该函数将c2连接到c1的后面，如果c1为null，将返回c2；如果c2为null，则返回c1；如果c1,c2都为null，则返回null。与使用操作符||返回的结果相同。例如：

```sql
SELECT "CONCAT"('first_', 'name') first_name, 'last_' || 'name' last_name FROM dual;

--结果
FIRST_NAME LAST_NAME
first_name	last_name
```

#### 4．INITCAP(c1)

其中，c1为字符串。该函数将每个单词的第一个字母大写，其他字母小写返回。单词由空格、控制字符、标点符号限制。例如：

```sql
SELECT "INITCAP"('veni,vedi,vici') Ceasar FROM dual;
--结果
CEASAR
Veni,Vedi,Vici
```

#### 5．INSTR(c1,c2,[,i[,j]])

其中，c1,c2均为字符串，i,j为整数。该函数返回c2在c1中第j次出现的位置，搜索从c1的第i个字符开始。当没有发现所需要的字符时，则返回0；如果i为负数，那么搜索将从右到左进行，但是位置的计算还是从左到右，i和j的默认值为1。例如：

```sql
SELECT "INSTR"('Mississippi', 'i',3,3) FROM dual;
--结果
"INSTR"('Mississippi', 'i',3,3)
11

SELECT "INSTR"('Mississippi', 'i',1,3) FROM dual;
--结果
"INSTR"('Mississippi', 'i',1,3)
8

SELECT "INSTR"('Mississippi', 'i',-2,3) FROM dual;
--结果
"INSTR"('Mississippi', 'i',-2,3)
2
```

#### 	6．INSTRB(c1,c2,[,i[,j]])

与INSTR()函数一样，只是这里返回的是字节，对于单字节，INSTRB()的效果等于INSTR()。

#### 7．LENGTH(c1)

如果c1为字符串，则返回c1的长度；如果c1为null，那么将返回null值。

```sql
SELECT "LENGTH"('test') test from dual
--结果
TEST
4
```

#### 8．LENGTHB()

与LENGTH()一样，返回字节。

#### 9．LOWER(c)

返回c的小写字符

#### 10．LPAD(c1,i,c2)

其中，c1,c2均为字符串，i为整数。在c1的左侧用c2字符串补足长度i，可多次重复，如果i小于c1的长度，那么只返回长度为i的c1字符，其他的将被截去。c2的默认值为单空格，参见RPAD。

例:

```sql
SELECT "LPAD"('test', 2, 'this') test from dual;
--结果
TEST
te

SELECT "LPAD"('test', 4, 'this') test from dual;
--结果
TEST
test

SELECT "LPAD"('test', 6, 'this') test from dual;
--结果
TEST
thtest	

SELECT "LPAD"('test', 9, 'this') test from dual;
--结果
TEST
thisttest
```

#### 11．RPAD(c1,i ,c2)

在c1的右侧用c2字符串补足长度i，可多次重复，如果i小于c1的长度，那么只返回长度为i的c1字符，其他的将被截去。c2的默认值为单空格，其他的与LPAD相似。

#### 12．LTRIM(c1,c2)

将c1中最左边的字符去掉，使其第一个字符不在c2中，如果没有c2，则c1就不会改变。

例如:

```sql
SELECT "LTRIM"('Mississippi', 'Mis') FROM dual;	
--结果
"LTRIM"('Mississippi', 'Mis')
ppi  -- 将c1中最左边的字符去掉，使其`第一个字符不在c2中`

SELECT "LTRIM"('Mississippi', 'Mi') FROM dual;	
--结果
"LTRIM"('Mississippi', 'Mi')
ssissippi  -- 将c1中最左边的字符去掉，使其第一个字符不在c2中
```

#### 13．RTRIM(c1[,c2])

将c1中最右边的字符去掉，使其最后一个字符不在c2中，如果没有c2，则c1就不会改变。

#### 14．REPLACE(c1,[c2,c3])

c1,c2,c3都是字符串，此函数用c3代替出现在c1中的c2后返回。

```sql
SELECT "REPLACE"('uptown','up','down') FROM dual
-- 结果
"REPLACE"('uptown','up','down')
downtown
```

#### 15．STBSTR(c1,i[,j])

c1为一个字符串，i,j为整数，从c1的第i位开始返回长度为j的子字符串，如果j为空，则直到串的尾部。

```sql
SELECT "SUBSTR"('Message',1,4) subs from dual
--结果
subs
Mess

```

#### 16．SUBSTRB(c1,i[,j])

与SUBSTR大致相同，只是i,j是以字节计算的。

#### 17．SOUNDEX(c1)

返回与c1发音相似的词。

```sql
SELECT "SOUNDEX"('test') from dual;
--结果
"SOUNDEX"('test')
T230
```

#### 18．TRANSLATE(c1,c2,c3)

将c1中与c2相同的字符以c3代替。

```sql
SELECT "TRANSLATE"('fumble', 'uf', 'ar')  from dual
--结果
"TRANSLATE"('fumble', 'uf', 'ar')
ramble
```

#### 19．TRIM(c1 from c2)

该函数用于从字符串的头部、尾部或两端截断特定字符，参数c1为要截去的字符，c2是源字符串。

```sql
SELECT "TRIM"('A' from 'ABCDFA') FROM dual
-- 结果
"TRIM"('A' from 'ABCDFA')
BCDF
```

#### 20．UPPER(c1)

​	返回c1的大写

### 数字函数

#### 1．ABS(n)

返回n的绝对值

#### 2．ACOS(n)

反余弦函数，返回-1到1之间的数，n表示弧度。

#### 3．ASIN(n)

反正弦函数，返回-1到1之间的数，n表示弧度。

#### 4．ATAN(n)

反正切函数，返回n的反正切值，n表示弧度。

#### 6．COS(n)

返回n的余弦值，n为弧度。

#### 7．COSH(n)

返回n的双曲余弦值，n为数字。

#### 8．EXP(n)

返回e的n次幂，e=2.71828183。

#### 9．FLOOR(n)

返回小于等于n的最大整数。

#### 10．LN(n)

返回n的自然对数，n必须大于0。

#### 11．LOG(n1, n2)

返回以n1为底n2的对数。

#### 12．MOD(n1, n2)

返回n1除以n2的余数。

#### 13．POWER(n1, n2)

返回n1的n2次方。

#### 14．ROUND(n1, n2)

返回舍入小数点右边n2位的n1的值，n2的默认值为0，返回与小数点最接近的整数，如果n2为负数就舍入到小数点左边相应的位上，n2必须是整数。

#### 15．SIGN(n)

如果n为负数，则返回-1；如果n为正数，则返回1；如果n =0，则返回0。

#### 16．SIN(n)

返回n的正弦值，n表示弧度。

#### 17．SINH(n)

返回n的双曲正弦值，n表示弧度。

#### 18．SQRT(n)

返回n的平方根，n表示弧度。

#### 19．TAN(n)

返回n的正切值，n表示弧度

#### 20．TANH(n)

返回n的双曲正切值，n表示弧度。

#### 21．TRUNC(n1, n2)

返回截尾到n2位小数的n1的值，n2默认设置为0，当n2为默认设置时，会将n1截尾为整数；如果n2为负值，就截尾在小数点左边相应的位上。

### 日期函数

日期函数操作DATE数据类型，大多数都有DATE数据类型的参数，且大多数返回的也是DATE数据类型的值。

#### 1．ADD_MONTHS(d,i)

返回日期d加上i个月后的结果，i可以是任意整数。如果i是一个小数，那么数据库将隐式地将其转换成整数，截去小数点后面的部分。

#### 2．LAST_DAY(d)

此函数返回包含日期d月份的最后一天。

```sql
SELECT SYSDATE from dual;
2020-07-11 22:36:05
SELECT "LAST_DAY"(SYSDATE) from dual
2020-07-31 22:36:10

```

#### 3．MONTHS_BETWEEN(d1,d2)

返回d1和d2之间月的数目，如果d1和d2的日期相同，或者都是该月的最后一天，那么将返回一个整数；否则，返回的结果将包含一个分数。

#### 4．NEW_TIME(d1,tz1,tz2)

d1是日期数据类型，当时区tz1中的日期和时间是d时，返回时区tz2中的日期和时间。tz1和tz2是字符串。

#### 5．NEXT_DAY(d,char)

该函数用于返回指定日期后的第一个工作日（由char指定）所对应的日期。

#### 6．SYADATE

此函数没有参数，返回当前日期和时间。

#### 7．TRUNC(d,[fmt])

返回由fmt指定的单位的日期d。

### 转换函数

转换函数用于操作多数据类型，在数据类型之间进行转换。

#### 1．CHARTORWID(c)

c为一个字符串，函数将c转换为RWID数据类型。

#### 2．CONVERT(c,dset,sset)

c为一个字符串，dset、sset是两个字符集，函数将字符串c由sset字符集转换为dset字符集，sset默认设置为数据库的字符集。

#### 3．HEXTORAW(x)

x为十六进制的字符串，函数将十六进制的x转换为RAW数据类型。

#### 4．RAWTOHEX(x)

x是RAW数据类型字符串，函数将RAW数据类型转换为十六进制的数据类型。

#### 5．ROWIDTOCHAR(rowid)

函数将ROWID数据类型转换为CHAR数据类型。

#### 6．TO_CHAR(x[,fmt[,nlsparam]])

x是DATE或NUMBER数据类型，函数将x转换成fmt指定格式的CHAR数据类型。如果x为日期，则nlsparam=NLS_DATE_LANGUAGE控制返回的月和日所使用的语言。如果x为数字，则nlsparam=NLS_NUMERIC_CHARACTERS用来指定小数位和千分位的分隔符以及货币符号。

#### 7．TO_DATE([c,[fmt,nlsparam]])

c表示字符串，fmt表示一个特殊格式的字符串。返回按照fmt格式显示的c，nlsparam表示使用的语言。函数将字符串c转换成DATE数据类型。

#### 8．TO_MULTI_BYTE(c)

c表示一个字符串，函数将c的单字节字符转换成多字节字符。

#### 9．TO_NUMBER([c,[fmt[,nlsparam]])

c表示一个字符串，fmt表示一个特殊格式的字符串，函数返回值按照fmt指定的格式显示。nlsparam表示语言，函数将返回c代表的数字。

#### 10．TO_SINGLE_BYTE(c)

将字符串c中的多字节字符转换成等价的单字节字符。该函数仅当数据库字符集同时包含单字节和多字节字符时才使用。

### 组函数(聚合函数)

组函数也叫集合函数，返回基于多个行的单一结果，行的准确数量无法确定，除非查询被执行并且所有的结果都被包含在内。与单行函数不同的是，在解析时所有的行都是已知的。由于这种差别，使组函数与单行函数在要求和行为上有微小的差异。Oracle提供了丰富的基于组的、多行的函数，这些函数可以在select或select的having子句中使用，当用于select子串时，常常都和GROUP BY一起使用。

#### 1．AVG([{DISYINCT|ALL}])

返回数值的平均值，默认设置为ALL。

#### 2．MAX([{DISTINCT|ALL}])

返回选择列表项目的最大值，如果x是字符串数据类型，则返回一个VARCHAR2数据类型；如果x是一个DATE数据类型，则返回一个日期；如果x是NUMBER数据类型，则返回一个数字。注意：DISTINCT和ALL不起作用，因为最大值与这两种设置是相同的。

#### 3．MIN([{DISTINCT|ALL}])

返回选择列表项目的最小值。

#### 4．STDDEV([{DISTINCT|ALL}])

返回选择列表项目的标准差，所谓标准差是方差的平方根。

#### 5．SUM([{DISTINCT|ALL}])

返回选择列表项目的数值的总和。

#### 6．VARIANCE([{DISTINCT|ALL}])

返回选择列表项目的统计方差。

### Oracle递归函数的使用

在日常应用中，经常需要查询指定记录的所有上层或下层数据树形结构，不必发愁，Oracle已经为我们提供了专门的函数来实现这个需求。下面通过例子介绍其使用方法。

基本语法：

```sql
SELECT col1,col2,... FROM tableName
	START WITH 条件1
	CONECT BY {PRIOR 列名1=列名2|列名1=PRIOR 列名2}
WHERE 条件3,条件4,...
```



## 序列

1. ### 创建序列

    可以在plsql软件中`文件 -> 新建 -> Sequence ->创建序列页面 `

    ![创建序列步骤](D:\data\notes\notes\sql\oracle\Oracle-Sql\image-20200718161334759.png)

    ![创建序列页面](D:\data\notes\notes\sql\oracle\Oracle-Sql\image-20200718161431039.png)

    * 所有者 : 这个序列的所有者

    * 名称: 序列名称,例如 : `TEST_SEQ`

    * 最小值: 这个序列的最小值 minvalue

    * 最大值: 这个序列的最大值 maxvalue

    * 开始于 : start with

    * 增量 : increment by

    * 高速缓存大小 : cache 不填就是nocache

        缓存就是例如批量创建时锁住一段序列比如 cache 20,就是锁住20个值,这样这段值就不会被其他插入的语句使用

    * 排序: order

    ```sql
    -- Create sequence 
    create sequence TEST_SEQ
    minvalue 1
    maxvalue 9999999999999999999999999999 -- 不填就默认这个
    start with 1
    increment by 1
    nocache
    order;
    ```

    

2. ### 更改序列

    ```sql
    -- 更改序列的增量为-1,这样每次执行下一个序列就会减1
    alter sequence test.test_seq increment by -1;
    ```

    **序列清除就可以先更改增量,再执行下一个,让序列回到1**

3. ### 查询序列

    ```sql
    -- 序列创建之后,立即查询当前值是查不到的,需要先查询下一个值
    
    -- 查询下一个序列 test是所有者,test_seq 是序列名称,nextval是下一个值
    -- 查询下一个序列执行一次,序列就会增1
    select test.test_seq.nextval from dual;
    
    -- 查询当前值
    select test.test_seq.currval from dual;
    ```

## 触发器

触发器当发生触发事件（triggering event）时它隐式地执行。执行触发器的行为被称为触发此触发器

触发事件(trigger event) : 

* 对数据库表执行的DML语句（例如，INSERT、UPDATE或DELETE）。这种触发器可以在触发事件之前或之后触发。例如，如果你定义了一个触发器，它在针对STUDENT表的INSERT语句之前触发，每当你在STUDENT表中插入行之前，此触发器就触发一次。
* 由特定用户针对某个模式或任何用户执行的DDL语句（例如，CREATE或ALTER）。这种触发器通常用于审计目的，并且对Oracle数据库管理员特别有帮助。它们可以记录模式的各种更改，包括这些更改在何时，以及由哪个用户做出的。
* 系统事件，如启动数据库或关闭数据库。
* 用户事件，如登录和注销。例如，你可以定义一个在登录数据库后触发的触发器，用来记录用户名和登录时间。

### 创建触发器语法:

```sql
create [or replace] [EDITIONABLE | NONEDITIONABLE] trigger 触发器名
{before | after } triggering_event on table_name
[for each row]
[follows | precedes 另一个触发器] 
[enable/disable]
[when 条件]
declare
  声明语句
begin
  可执行语句
exception
 异常处理语句
end;
```

* `follows | precedes`允许指定触发器的顺序,它适用于在相同的表上定义，并在相同时间点上触发的触发器。例如，如果你定义了两个对STUDENT表执行插入操作之前触发的触发器，Oracle不保证这些触发器的触发顺序，除非你明确指定使用FOLLOWS/PRECEDES子句。需要注意的是，在FOLLOWS/PRECEDES子句中引用的触发器必须已经存在，并且已经被成功编译

### 禁用/启用触发器

```sql
-- 禁用触发器
alter trigger 触发器名 disable;

-- 启用触发器
alter trigger 触发器名 enable;
```



### 创建触发器

#### before触发器

此触发器在针对STUDENT表的INSERT语句之前触发，并填充STUDENT_ID、CREATED_DATE、MODIFIED_DATE、CREATED_BY和MODIFIED_BY列。STUDENT_ID列采用STUDENT_ID_SEQ序列生成的编号填充，CREATED_DATE、MODIFIED_DATE、CREATED_USER和MODIFIED_USER列分别填充了当前日期和当前用户名信息。

```sql
create or replace trigger student_bi_tg
before insert on student
for each row
begin
  :NEW.student_id := student_id_seq.nextval;
  :NEW.created_by := USER;
  :NEW.created_date := SYSDATE;
  :NEW.modified_by := USER;
  :NEW.modified_date := SYSDATE;
end;
```

在触发器体中，有一个伪记录（pseudorecord）, `:NEW`，它可以访问当前正在处理的行。换言之，它是插入到STUDENT表中的一行。:NEW伪记录是一种`TRIGGERING_TABLE%TYPE`，因此，在本例中，它的类型是`STUDENT%TYPE`。要访问：NEW伪记录的各个成员，可以使用点符号。换句话说，:NEW.CREATED_BY指的是：NEW伪记录的成员CREATED_BY，而记录名称用点符号与其成员名称分隔。

`OLD伪记录`。它可以让你访问被更新或删除记录的当前信息。因此对于INSERT语句，:OLD伪记录是未定义的，而对于DELETE语句，:NEW伪记录是未定义的。但是，当：OLD或：NEW伪记录被分别用于触发事件是：INSERT或DELETE操作的触发器时，PL/SQL编译器不产生语法错误。在这种情况下，:OLD或：NEW伪记录的成员的值被设置为NULL

#### after触发器

```sql
-- 这个语句在Navicat premium上执行失败,在plsql中执行成功
CREATE OR REPLACE TRIGGER instructor_aud
AFTER UPDATE OR DELETE ON INSTRUCTOR
DECLARE
   v_trans_type VARCHAR2(10); 
BEGIN
   v_trans_type := CASE
                      WHEN UPDATING THEN 'UPDATE'
                      WHEN DELETING THEN 'DELETE'
                   END ;
   INSERT INTO audit_trail 
      (TABLE_NAME, TRANSACTION_NAME, TRANSACTION_USER, TRANSACTION_DATE)
   VALUES 
      ('INSTRUCTOR', v_trans_type, USER, SYSDATE);
END;
```

#### 自治事务

当一个触发器触发时,由触发器执行的所有操作都成为一个事务的一部分,当这个事务被提交或回滚时，由触发器执行的操作也被提交或回滚.

例如:

```sql
update instructor set phone = '17298765897' where instructor_id = 100;
```

当这个update语句被执行时,`instructor_aud`这个触发器会触发,并增加一条记录到`audit_trail`表中.

当update语句被回滚时,触发器插入`audit_trail`表的记录也会被回滚,这样就达不到记录的目的了,如果想不管update或delete执行成功还是失败都记录,就需要使用`自治事务`

`自治事务是由通常被称为主事务的另一个事务启动的一个独立的事务。换句话说，自治事务可以发出各种DML语句并提交或回滚它们，而不会提交或回滚由主事务发出的DML语句`

要定义一个自治事务，你需要使用`AUTONOMOUS_TRANSACTION`编译指示

创建`INSTRUCTOR_AUD`触发器的修改版本

```sql
CREATE OR REPLACE TRIGGER instructor_aud
AFTER UPDATE OR DELETE ON INSTRUCTOR
DECLARE
   v_trans_type VARCHAR2(10); 
   pragma autonomous_transaction; -- 这里添加指示
BEGIN
   v_trans_type := CASE
                      WHEN UPDATING THEN 'UPDATE'
                      WHEN DELETING THEN 'DELETE'
                   END ;
   INSERT INTO audit_trail 
      (TABLE_NAME, TRANSACTION_NAME, TRANSACTION_USER, TRANSACTION_DATE)
   VALUES 
      ('INSTRUCTOR', v_trans_type, USER, SYSDATE);
	commit; -- 这里提交
END;
  
```

`如果你删除一个表，此表的数据库触发器也会被删除。`

### 触发器类型

#### 行触发器(row trigger)

触发语句中有`for each row`时这个触发器就是行触发器

如果一个update语句更新20行,行触发器就会执行20次.

例如:`before触发器`

#### 语句触发器

语句触发器为触发语句触发一次,无论触发语句影响的行有多少,语句触发器都只触发一次

例如: after触发器`

#### INSTEAD OF触发器

数据库视图上创建的触发器

### 触发器使用

不用触发器插入数据:

```sql
insert into STUDENT (STUDENT_ID, FIRST_NAME, LAST_NAME, zip, REGISTRATION_DATE,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DATE)
VALUES 
(STUDENT_ID_seq.nextval, 'John', 'Smith', '00914', SYSDATE, USER, SYSDATE, USER, SYSDATE);

SELECT * from STUDENT where FIRST_NAME = 'John';

```

使用触发器插入数据:

```sql
INSERT into STUDENT (FIRST_NAME, LAST_NAME, zip, REGISTRATION_DATE)
VALUES
('John', 'Smith', '00914', SYSDATE);
```

触发器会自动插入`STUDENT_ID,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DAT`这几个字段的数据

## 视图

创建视图

```sql
create view COURSE_cost_view
as 
  SELECT COURSE_no, DESCRIPTION, "COST"
  from COURSE 
```

视图也可以通过INSERT、UPDATE和DELETE语句来操作，但存在一些限制

如果一个视图查询执行以下任何操作，或包含以下任何结构，那么这个视图就不能通过UPDATE、INSERT和DELETE语句进行修改:

* 集合操作，如UNION、UNION ALL、INTERSECT和MINUS。
* 组函数，如AVG、COUNT、MAX、MIN和SUM。
* GROUP BY或HAVING子句。
* CONNECT BY或START WITH子句。
* DISTINCT运算符。
* ROWNUM伪列。

## 其他查询语句

查询表空间

```sql
select tablespace_name from dba_tablespaces order by tablespace_name;

-- 结果
tablespace_name
EXAMPLE
SYSAUX
SYSTEM
TEMP -- TEMPORARY TABLESPACE 临时 TABLESPACE
UNDOTBS1 
USERS --  DEFAULT TABLESPACE 默认TABLESPACE

```

```sql
-- 创建用户
create user student identified by learn 
default tablespace users -- users 对应上面查询的
temporary tablespace temp;--  temp也对应上面查询的

-- 授权
grant connect, resource to student;

-- 可以使用student/learn 测试连接了

```

## 连接查询

### 自连接查询

```sql
select e.ename 员工姓名, b.ename 老板姓名 
from emp e, emp b
where e.mgr = b.empno
```

自连接查询本质上还是把一个表当作两个表来连接查询,所以查询过程中会产生笛卡尔积.这样当表的数量很大比如一亿条数据,连接起来产生笛卡尔积就很慢.那怎样不产生笛卡尔积呢?

可以使用层次查询

![image-20200825222948892](D:\data\notes\notes\sql\oracle\Oracle-Sql\image-20200825222948892.png)



```sql
select level, empno, ename, sal, mgr
from emp
connect by prior empno = mgr
start with mgr is null -- 因为king是根节点,上面没有老板所以可以用mgr is null 表示,等价于 empno = 7839(king的员工号)
order by 1;

prior : 表示上一层
start with : 起始点 例如start with empno = 7566
level(伪列): 树的深度,这是个伪列类似于rownum

connect by 上一层的员工号 = 老板号



```

## 子查询

子查询需要注意的10个问题:

* 子查询语法中的小括号

    ```sql
    select * 
    from emp
    where sal > (select sal from emp where ename = 'scott')
    
    -- 子查询中的括号必须写
    ```

* 子查询的书写风格

* 可以使用子查询的位置: where, select, having, from

* 不可以使用子查询的位置: group by

* 强调 from后面的子查询

* 子查询和主查询可以不是同一张表

* 一般不在子查询中使用排序;但在Top-N分析问题中,必须对子查询排序

    

* 一般先执行子查询,再执行主查询;但相关主查询例外

* 单行子查询只能使用单行操作符;多行子查询只能使用多行操作符

    单行操作符 : =, > , >= ,< , <= , <>

    多行操作符: IN, ANY, ALL

* 注意:子查询中null值问题

    子查询中有null值不要使用not in ,因为 not in 相当于 <> all

    ```sql
    age not in (20, 30, null) 
    相当于 age != 20 and age != 30 and age != null
    但是 age != null永远为假,因为判断一个值是否是null不能用 != 判断要用 age is not null;
    ```

    

## `行号需要注意的问题` : 

行号永远按照默认的顺序生成

行号只能使用< , <= ;不能使用<, >=. (oracle取数据是一行一行的取)