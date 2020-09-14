-- 查询列名
select * from syscolumns where id=(select max(id) from sysobjects where xtype='u' and name='xiron_royalty') ;
-- 查询列名和注释
SELECT  
 A.name AS table_name, 
 B.name AS column_name,
 C.value AS column_description 
FROM sys.tables A  
INNER JOIN sys.columns B ON B.object_id = A.object_id  
LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id 
WHERE A.name = 'xiron_royalty'

-- 查询带序号解说：在这里，TA1是一个表，A1是表中的一个字段，表的另一个字段为ID本用于自增这儿用来排序。
-- SQL Server 中的 ROW_NUMBER() 得到一个查询出的顺序，但这个函数要求给出一个查的排序方案，因为SQL Server的存储是无关顺序的。
SELECT ROW_NUMBER()  OVER(ORDER BY ID) ROWNU,A1 FROM TA1

-- 拼接
SELECT CONVERT(NVARCHAR(50),ROWNU) + '='+ column_name + ','+CONVERT(VARCHAR(255),column_description) from (
SELECT  ROW_NUMBER() OVER(ORDER BY  B.name) ROWNU , B.name AS column_name , C.value AS column_description 
FROM sys.tables A  
INNER JOIN sys.columns B ON B.object_id = A.object_id  
LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id 
WHERE A.name = 'xiron_royalty') as t;



SELECT column_name ,column_description,type_name,max_length from (
SELECT  ROW_NUMBER() OVER(ORDER BY  B.name) ROWNU , B.name AS column_name ,s.name as type_name,B.max_length AS max_length, C.value AS column_description 
FROM sys.tables A  
INNER JOIN sys.columns B ON B.object_id = A.object_id  left join sys.types s on B.user_type_id = s.user_type_id
LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id 
WHERE A.name = 'xiron_right_item_library') as t;

SELECT * from INFORMATION_SCHEMA.TABLES t 