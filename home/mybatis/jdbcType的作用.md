# jdbcType类型

```
 1 JDBC Type           Java Type  
 2 CHAR                String  
 3 VARCHAR             String  
 4 LONGVARCHAR         String  
 5 NUMERIC             java.math.BigDecimal  
 6 DECIMAL             java.math.BigDecimal  
 7 BIT                 boolean  
 8 BOOLEAN             boolean  
 9 TINYINT             byte  
10 SMALLINT            short  
11 INTEGER             INTEGER  
12 BIGINT              long  
13 REAL                float  
14 FLOAT               double  
15 DOUBLE              double  
16 BINARY              byte[]  
17 VARBINARY           byte[]  
18 LONGVARBINARY       byte[]  
19 DATE                java.sql.Date  
20 TIME                java.sql.Time  
21 TIMESTAMP           java.sql.Timestamp  
22 CLOB                Clob  
23 BLOB                Blob  
24 ARRAY               Array  
25 DISTINCT            mapping of underlying type  
26 STRUCT              Struct  
27 REF                 Ref  
28 DATALINK            java.net.URL[color=red][/color] 
```

## jdbcType的作用

 当传入字段值为null,时,需要加入. 否则报错.