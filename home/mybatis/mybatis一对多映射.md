# mybatis映射

## 一对一映射

实体

```java
/**
*  用户表
*/
public class SysUser{

    //省略其他字段

    /**
    *  用户角色
    */
    private SysRole role;

    public SysRole getRole{
        return role;
    }

    public void setRole(SysRole role){
        this.role = role;
    }

    //其他字段的 get/set 方法
}
```

### 使用自动映射处理一对一的关系

```xml
    <select id="selectUserAndRoleById" resultType="tk.mabatis.simple.model.SysUser">
        select
            u.id,
            u.user_name userName,
            u.user_password userPaswword,
            u.user_email userEmail,
            r.id "role.id",
            r.role_name "role.roleName"
        from sys_user u
        inner join sys_user_role ur on u.id = ur.user_id
        inner join sys_role r on ur.role_id = r.id
        where u.id = #{id}
    </select>
```

上面查询列的别名都是 "role."前缀,通过这种方式将 role 的属性都映射到了 SysUser 的 Role 属性上.

### 使用 resultMap 配置一对一的关系

```xml
    <select id="selectUserAndRoleById2" resultMap="userRoleMap">
        select
            u.id,
            u.user_name,
            u.user_password,
            u.user_email,
            r.id role_id,
            r.role_name
        from sys_user u
        inner join sys_user_role ur on u.id = ur.user_id
        inner join sys_role r on ur.role_id = r.id
        where u.id = #{id}
    </select>

    <resultMap id="userRoleMap" type="tk.mabatis.simple.model.SysUser">
        <id property="id" column="id">
        <result property="userName" column="user_name">
        <result property="userPassword" column="user_password">
        <result property="userEmail" column="user_email">
        <result property="role.id" column="role_id">
        <result property="role.roleName" column="role_name">
    </resultMap>

     /*使用继承*/
     <resultMap id="userRoleMap" extends="userMap" type="tk.mabatis.simple.model.SysUser">
        <result property="role.id" column="role_id">
        <result property="role.roleName" column="role_name">
    </resultMap>
```

### 使用 resultMap 的 association 标签配置一对一映射

```xml
   <resultMap id="userRoleMap" extends="userMap" type="tk.mabatis.simple.model.SysUser">
        <association property="role" columnPrefix="role_" javaType="tk.mabatis.simple.model.SysRole">
             <result property="id" column="id">
            <result property="roleName" column="name">
        </association>
    </resultMap>
```

![association](2019-07-17-16-27-40.png)
