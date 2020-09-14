# java SQL相关注解

## @GeneratedValue

​	@GeneratedValue（strategy=Generation Type，generator=""）：可选，用于定义主键生成策略。
如：

```java
 @Id
 @TableGenerator（name="tab cat gen"，allocationSize=1）
 @GeneratedValue（Strategy=GenerationType.Table）
```


Generator - 表示主键生成器的名称，这个属性通常和ORM框架相关，例如：Hibernate 可以指定 uuid 等主键生成方式

@GeneratedValue（strategy=Generation Type，generator=""）：可选，用于定义主键生成策略。
strategy表示主键生成策略，取值有：

* 1.GenerationType.AUTO：根据底层数据库自动选择（默认）
* 2.Generation Type.INDENTITY：根据数据库的Identity字段生成
* 3.Generation Type.SEQUENCE：使用Sequence来决定主键的取值
* 4.Generation Type.TABLE：使用指定表来决定主键取值，结合@TableGenerator使用

## @Column
@Column-可将属性映射到列，使用该注解来覆盖默认值，@Column描述了数据库表中该字段的详细定义，这对于根据JPA注解生成数据库表结构的工具非常有作用。
常用属性：

* name：可选，表示数据库表中该字段的名称，默认情形属性名称一致。
* nullable：可选，表示该字段是否允许为null，默认为true。
* unique：可选，表示该字段是否是唯一标识，默认为false。
* length：可选，表示该字段的大小，仅对String类型的字段有效，默认值255。（如果是主键不能使用默认值）
* insertable：可选，表示在ORM框架执行插入操作时，该字段是否应出现INSETRT语句中，默认为true。
* updateable：可选，表示在ORM框架执行更新操作时，该字段是否应该出现在UPDATE语句中，默认为true。对于一经创建就不可以更改的字段，该属性非常有用，如对于birthday 字段。