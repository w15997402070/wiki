
-- 时间保留毫秒
-- DATETIME(3) (字段长度)
ALTER TABLE `rfq_quotation`  ADD  DAC_QUOTATION_DATE DATETIME(3) NULL COMMENT '报价精确时间' ;

UPDATE `rfq_quotation` rq SET rq.`DAC_QUOTATION_DATE` = '2017-05-09 10:48:17.196' WHERE `REQUEST_ID` = '34fee4d6-fdcd-4492-bf56-0563c13bf4cd' ;
-- 查询含有毫秒值的时间
SELECT NOW(3);
