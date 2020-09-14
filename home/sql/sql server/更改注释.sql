-- 1. 表添加注释
-- 表加注释
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'注释内容' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'表名'
--例如：
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'系统设置表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'CM01_SYSTEM'

--修改数据字段注释
EXECUTE sp_updateextendedproperty 'MS_Description', '收益浮动表', 'user', 'dbo', 'table', 'COM_IncomeFloat', 'column', 'RowId';
 
MS_Description ：调用的Proc
COM_IncomeFloat ：表名
RowId ：表字段
 
--查询字段注释
SELECT
A.name AS table_name,
B.name AS column_name,
C.value AS column_description
FROM sys.tables A
INNER JOIN sys.columns B ON B.object_id = A.object_id
LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id
WHERE A.name = 'COM_IncomeFloat'


    --为字段添加注释   
    --格式如右：execute sp_addextendedproperty 'MS_Description','字段备注信息','user','dbo','table','字段所属的表名','column','添加注释的字段名';  
    execute sp_addextendedproperty 'MS_Description','add by liyc. 诊断类别码','user','dbo','table','DiagRecord','column','DiagTypeCode';  
      
    --修改字段注释   
    execute sp_updateextendedproperty 'MS_Description','add by liyc.','user','dbo','table','DiagRecord','column','DiagTypeCode';  
      
    --删除字段注释  
    execute sp_dropextendedproperty 'MS_Description','user','dbo','table','DiagRecord','column','DiagTypeCode';  
      
    -- 添加表注释  
    execute sp_addextendedproperty 'MS_Description','诊断记录文件','user','dbo','table','DiagRecord',null,null;  
      
    -- 修改表注释  
    execute sp_updateextendedproperty 'MS_Description','诊断记录文件1','user','dbo','table','DiagRecord',null,null;  
      
    -- 删除表注释  
    execute sp_dropextendedproperty 'MS_Description','user','dbo','table','DiagRecord',null,null;  
      
    -- 说明：  
    -- 1.增加、修改、删除注释调用不同的存储过程    
    -- 2.增加、修改注释调用的存储过程参数数量和含义相同，删除备注比前两者少了一个“备注内容”的参数  
    -- 3.为表添加注释相比于为字段添加注释，最后两个参数为null  