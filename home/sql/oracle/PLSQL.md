# PL/SQL

[toc]

## 语法

### 程序结构

```sql
declare
  -- 说明部分(变量说明,光标申明,异常说明)
begin
  --语句序列(DML语句)
exception
 -- 异常处理语句
end;
/
```

打印hello world

```sql
declare 
  -- 说明部分
begin
  --程序体
  dbms_output.put_line('hello world');
end;

-- 打开输出开关(oracle输出开关默认关闭)
set serveroutput on 
```

#### 说明部分

定义基本变量

类型 : 

* char

* varchar2

* date

* number

* boolean

* long

* 引用类型 : 

    ```sql
    my_name emp.ename%type
    -- 引用 emp 表的ename字段的类型,作为my_name的类型
    ```

* 记录型变量:

    ```sql
    emp_rec emp%rwotype;
    -- 表示 emp 表的一行赋给emp_rec变量,emp_rec相当于变量类型数组
    
    --记录型变量分量的引用
    emp_rec.ename := 'ADAMS';
    ```

    例如:

    ```sql
    declare
      -- 定义记录型变量:注意代表一行
      emp_rec emp%rawtype;
    begin
      --查询7839的一行信息
      select * into emp_rec from emp where empno=7839
      --打印姓名和薪水(emp_rec是一行数据,所以分别取ename和sal字段的值)
      dbms_output.put_line(rmp_rec.ename||'的薪水是'||emp_rec.sal);
    end;
    /
    ```

* 

举例 : 

```sql
var1 char(15);
married boolean := true;
psal number(7,2);
```

### 程序控制

#### IF语句

1. 

```sql
IF 条件 THEN 语句1;
语句2;
END IF;
```

2. 

```sql
IF 条件 THEN 语句序列1;
ELSE 语句序列2;
END IF;
```

3. 

```sql
IF 条件 THEN 语句;
ELSEIF 语句 THEN 语句;
ELSE 语句;
END IF;
```

