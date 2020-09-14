# 基本sql

## insert语句

```xml
<insert id="insert">
    insert into sys_user(id,user_name,user_passward,user__eamil)
    values (#{id},#{userName},#{userPassword},#{userEmail})
</insert>

<!-- 返回数据库自增主键 -->
<insert id="insert2" useGeneratedKeys="true" keyProperty="id">
    insert into sys_user(user_name,user_passward,user__eamil)
    values (#{userName},#{userPassword},#{userEmail})
</insert>

<!---->

```

### insert标签包含的属性

- id：命名空间中的唯一标识符，可用来代表这条语句。
- parameterType：即将传入的语句参数的完全限定类名或别名。这个属性是可选的，因为MyBatis 可以推断出传入语句的具体参数，因此不建议配置该属性。
- flushCache：默认值为true，任何时候只要语句被调用，都会清空一级缓存和二级缓存。
- timeout：设置在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。
- statementType：对于STATEMENT、PREPARED、CALLABLE，MyBatis会分别使用对应的statement、PreparedStatement、Callablestatement，默认值为PREPARED。
- useGeneratedkeys：默认值为false。如果设置为true，MyBatis会使用JDBC的getGeneratedKeys方法来取出由数据库内部生成的主键。
- keyProperty:MyBatis 通过 getGeneratedKeys获取主键值后将要赋值的属性名。
如果希望得到多个数据库自动生成的列，属性值也可以是以逗号分隔的属性名称列表。
- keycolumn：仅对INSERT和UPDATE有用。通过生成的键值设置表中的列名，这个设置仅在某些数据库（如PostgreSQL）中是必须的，当主键列不是表中的第一列时需要设置。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。
- databaseId：如果配置了databaseIdProvider（4.6节有详细配置方法），MyBatis会加载所有的不带databaseId的或匹配当前 databaseId的语句。如果同时存在带databaseId和不带 databaseId的语句，后者会被忽略。

### 使用selectKey返回主键

1. Mysql获取方式 order 设置为 AFTER

```xml
<!-- mapper接口中的方法  int insert3(SysUser sysUser) -->
<insert id="insert3" useGeneratedKeys="true" keyProperty="id">
    insert into sys_user(user_name,user_passward,user__eamil)
    values (#{userName},#{userPassword},#{userEmail})
    <selectKey keyColumn="id" resultType="long" keyProperty="id" order="AFTER">
        SElECT LAST_INSERT_ID()
    </selectkey>
</insert>
```

2. oracle获取方式 order 设置为 BEFORE

```xml
<!-- mapper接口中的方法  int insert3(SysUser sysUser) -->
<insert id="insert3" useGeneratedKeys="true" keyProperty="id">
    <selectKey keyColumn="id" resultType="long" keyProperty="id" order="BEFORE">
        SElECT LAST_INSERT_ID()
    </selectkey>
    insert into sys_user(id,user_name,user_passward,user__eamil)
    values (#{id},#{userName},#{userPassword},#{userEmail})
</insert>
```

*注意* : Oracle方式的INSERT语句中明确写出了 id 列和值 #{id}，因为执行selectKey中的语句后id就有值了，我们需要把这个序列值作为主键值插入到数据库中，所以必须指定id列，如果不指定这一列，数据库就会因为主键不能为空而抛出异常。
