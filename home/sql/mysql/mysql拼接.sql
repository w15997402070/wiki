-- 把一列的值拼接到一行中
SELECT CONCAT( 'id in (',  GROUP_CONCAT("'",rsl.`ID`,"'"),')' ) AS a FROM rfq_supplier_list rsl LIMIT 0,10000;

-- 单行拼接
SELECT CONCAT("'",rqr.id,"',") FROM rfq_quotation_result rqr  LIMIT 0,10000;

-- 单行和多行拼接
SELECT CONCAT( 'REQUEST_ID in (',  GROUP_CONCAT("'",tc.`REQUEST_ID`,"'"),')' )FROM
(
SELECT
  rsl.`REQUEST_ID`,
  rqr.`SUPPLIER_NUM`,
  rqr.`SUPPLIER_NAME`,
  rsl.`SUPPLIER_CODE`
  -- ,rsl.`SUPPLIER_NAME`
FROM
  rfq_quotation_result rqr,
  rfq_supplier_list rsl,
  rfq_request rr
WHERE rsl.`REQUEST_ID` = rqr.`REQUEST_ID`
AND rr.`ID` = rsl.`REQUEST_ID`
AND rr.`ID` = rqr.`REQUEST_ID`
AND rr.`RFQ_METHOD` = 'TBQ'
AND rsl.`SUPPLIER_NAME` = rqr.`SUPPLIER_NAME`
AND rsl.`SUPPLIER_CODE` != rqr.`SUPPLIER_NUM` GROUP BY rr.`ID`) tc ;

--
