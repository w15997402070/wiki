# Java编程常用规范

[toc]

## 日期时间

### 在日期格式化时，传入pattern中表示年份统一使用小写的y

```
说明：在日期格式化时，yyyy表示当天所在的年，大写的YYYY表示week in which year（JDK 7之后引入的概念），意思是当天所在的周属于的年份。一周从周日开始，周六结束，只要本周跨年，返回的YYYY就是下一年
```

```java
new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
```

### 在日期格式中，分清楚大写的M和小写的m、大写的H和小写的h分别代表的意义

1）表示月份，用大写的M；

2）表示分钟，用小写的m；

3）表示24小时制，用大写的H；

4）表示12小时制，用小写的h。

### 获取当前毫秒数：是System.currentTimeMillis(); 而不是new Date().getTime()

说明：如果想获取更加精确的纳秒级时间值，则使用System.nanoTime的方式。在JDK 8中，针对统计时间等场景，推荐使用Instant类。

## 集合

### 判断所有集合内部的元素是否为空，应使用isEmpty()方法，而不是使用size()==0的方式

说明：在某些集合中，前者的时间复杂度为O(1)，而且可读性更好。



### ArrayList的subList结果不可强转成ArrayList，否则会抛出ClassCastException异常，即java.util. RandomAccessSubList cannot be castto java.util. ArrayList

说明：subList()返回的是ArrayList的内部类SubList，并不是ArrayList本身，而是ArrayList的一个视图，对于SubList的所有操作最终会反映到原列表上。

### Collections类返回的对象，如：emptyList()/ singletonList()等都是immutable list，不可对其添加或者删除元素。

### 使用集合转数组的方法，必须使用集合的toArray (T[] array)，传入的是类型完全一致、长度为0的空数组。

```java
List<String> list = new ArrayList<>(2);
list.add("test");
list.add("test2");
String[] strArr = list.toArray(new String[0]);
```

说明：使用toArray带参方法，数组空间大小的length：

1）等于0，动态创建与size相同的数组，性能最好；

2）大于0但小于size，重新创建大小等于size的数组，增加GC负担；

3）等于size，在高并发情况下，在数组创建完成之后，size正在变大的情况下，负面影响与第2条相同；

4）大于size，空间浪费，且在size处插入null值，存在NPE隐患。

### 在使用Collection接口任何实现类的addAll()方法时，都要对输入的集合参数进行NPE判断。

说明：ArrayList#addAll方法的第一行代码即Object[] a = c.toArray();，其中，c为输入集合参数，如果为null，则直接抛出异常。

### 当使用工具类Arrays.asList()把数组转换成集合时，不能使用其修改集合相关的方法，它的add/remove/clear方法会抛出UnsupportedOperationException异常。

说明：asList的返回对象是一个Arrays内部类，并没有实现集合的修改方法。Arrays.asList体现的是适配器模式，只是转换接口，后台的数据仍是数组。

### 使用entrySet遍历Map类集合K/V，而不是用keySet方式遍历

说明：keySet方式其实遍历了两次，一次是转为Iterator对象，另一次是从hashMap中取出Key所对应的Value。而entrySet只遍历了一次就把Key和Value都放到了entry中，效率更高。如果是JDK 8，则使用Map.forEach方法

## Mysql

### 主键索引名为pk_字段名，唯一索引名为uk_字段名，普通索引名则为idx_字段名。

说明：pk_即primary key，uk_即unique key，idx_即index的简称。

### 小数类型为decimal，禁止使用float和double类型。

说明：在存储时，float和double类型存在精度损失的问题，很可能在比较值的时候，得到不正确的结果。如果存储的数据范围超过decimal的范围，那么建议将数据拆成整数和小数并分开存储。

varchar是可变长字符串，不预先分配存储空间，长度不要超过5000个字符，如果存储长度大于此值，则应定义字段类型为text，独立出来一张表，用主键来对应，避免影响其他字段的索引效率。

### SQL语句

不要使用count(列名)或count(常量)来替代count(`*`)，count(`*`)是SQL92定义的标准统计行数的语法，与数据库无关，与NULL和非NULL无关。

说明：count(*)会统计值为NULL的行，而count(列名)不会统计此列值为NULL的行。

count(distinct column) 计算该列除NULL外的不重复行数。注意，count(distinct column1, column2)，如果其中一列全为NULL，那么即使另一列有不同的值，也返回为0。

当某一列的值全为NULL时，count(column)的返回结果为0，但sum(column)的返回结果为NULL，因此使用sum()时需注意避免NPE问题。

使用ISNULL()判断是否为NULL值。

说明：NULL与任何值的直接比较都为NULL。

1）NULL<>NULL的返回结果是NULL，而不是false。

2）NULL=NULL的返回结果是NULL，而不是true。

3）NULL<>1的返回结果是NULL，而不是true。

TRUNCATE TABLE比DELETE速度快，且使用的系统和事务日志资源少，但TRUNCATE无事务且不触发trigger，有可能造成事故，故不建议在开发代码中使用此语句。