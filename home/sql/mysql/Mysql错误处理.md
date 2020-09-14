# 错误处理

## `Index column size too large. The maximum column size is 767 bytes.`

MySQL 5.6可能会有这个错

处理方法

```sql
# 设置这两个参数
set global innodb_large_prefix=on;
set global innodb_file_format=Barracuda;

# 如果还不能解决
# 查看表是不是utf8mb4类型的,如果是,改成utf8再试
```

