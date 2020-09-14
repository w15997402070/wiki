mysql 关键字作列名报错时可以在列名上加 `

![](2018-09-21-16-00-52.png)

例如 : 
```sql
select DESC from t; 
```
会报错

```sql
select `DESC` from t;
```
这样就可以了