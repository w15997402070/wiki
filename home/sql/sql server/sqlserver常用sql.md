# sqlserver 常用sql

## 联表更新

```sql
update t1 set col1 = t2.col2 from table1 t1 inner join table2 t2 on t1.col2 = t2.col2
```



## 分组后拼接字符串 (多列拼接一行)

```sql
 SELECT
        s.id,
        s.general_shiyang,
        p.product_status,
        STUFF((
          SELECT ',' + da.area_name
          FROM xiron_dep_area da
          WHERE ','+da.sales_assistant +',' LIKE ( '%,'+'a'+',%')
          FOR XML PATH('')), 1, 1, '') as area_name 
        FROM
        xiron_subscriptions s
        LEFT JOIN view_xiron_product p ON s.product_id = p.id
--         LEFT JOIN xiron_dep_area a ON ','+a.sales_assistant +',' LIKE ( '%,'+'a'+',%')
        where s.status IN ('4','5','6','7','8','9')
```

## 声明变量

### 创建局部变量

```sql
DECLARE {{@local_variable data_type} 
            | { @cursor_variable_name CURSOR }
            | { table_type_definition}
         } [...n]

```

- @local_variable : 变量的名称	.变量名必须以@符号开头
- data_type : 是任何由系统提供的或用户定义的数据类型,变量不能是text,ntext或image数据类型
- @cursor_variable_name : 是游标变量的名称,游标变量必须以@符开头并遵从标识符规则
- CURSOR : 指定变量是局部游标变量
- table_type_definition : 定义表数据类型.表声明包括列定义,名称,数据类型和约束
- n : 是表示可以指定多个变量并对变量赋值的占位符.当声明表变量时,表变量必须是DECLARE 语句中正在声明的唯一变量

声明局部变量后要给局部变量赋值,可以用SET 或SELECT 语句 : 

```sql
SET @local_variable = expression
SELECT @local_variable = expression[..n]
```

