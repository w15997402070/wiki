-- 删除RFQ_QUOTATION_RESULT 重复数据(id不一样)
DELETE FROM rfq_quotation_result WHERE id IN (SELECT t.id FROM (
SELECT
  r.`ID`
FROM
  rfq_quotation_result r
GROUP BY r.`QUOTATION_ITEM_ID`
HAVING COUNT(r.`QUOTATION_ITEM_ID`) >1) t) ;

-- 
