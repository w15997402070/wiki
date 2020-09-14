### 区别

    <!-- 3. resultType (resultType 与 resultMap 二选一配置)
         resultType用以指定返回类型，指定的类型可以是基本类型，可以是java容器，也可以是javabean -->
     resultType="hashmap"

     <!-- 4. resultMap (resultType 与 resultMap 二选一配置)
         resultMap用于引用我们通过 resultMap标签定义的映射类型，这也是mybatis组件高级复杂映射的关键 -->
     resultMap="personResultMap"


resultMap设置
```xml
     <!--
        1.type 对应类型，可以是javabean, 也可以是其它
        2.id 必须唯一， 用于标示这个resultMap的唯一性，在使用resultMap的时候，就是通过id指定
     -->
    <resultMap type="" id="">

        <!-- id, 唯一性，注意啦，这个id用于标示这个javabean对象的唯一性， 不一定会是数据库的主键（不要把它理解为数据库对应表的主键）
            property属性对应javabean的属性名，column对应数据库表的列名
            （这样，当javabean的属性与数据库对应表的列名不一致的时候，就能通过指定这个保持正常映射了）
        -->
        <id property="" column=""/>

        <!-- result与id相比， 对应普通属性 -->    
        <result property="" column=""/>

        <!--
            constructor对应javabean中的构造方法
         -->
        <constructor>
            <!-- idArg 对应构造方法中的id参数 -->
            <idArg column=""/>
            <!-- arg 对应构造方法中的普通参数 -->
            <arg column=""/>
        </constructor>

        <!--
            collection，对应javabean中容器类型, 是实现一对多的关键
            property 为javabean中容器对应字段名
            column 为体现在数据库中列名
            ofType 就是指定javabean中容器指定的类型
        -->
        <collection property="" column="" ofType=""></collection>

        <!--
            association 为关联关系，是实现N对一的关键。
            property 为javabean中容器对应字段名
            column 为体现在数据库中列名
            javaType 指定关联的类型
         -->
        <association property="" column="" javaType=""></association>
    </resultMap>
```
