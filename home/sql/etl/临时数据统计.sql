-- 临时中间库数据统计
SELECT TABLE_NAME ,TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME LIKE 'rfq%' AND TABLE_SCHEMA = 'rfq_db_test'

-- oracle库数据量统计
-- 报价物料表
SELECT count(1) from RFQ_REQUEST_ITEM r where R.REQUEST_ID IN (SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0')
-- 询单主表
SELECT count(1) from RFQ_REQUEST r where r.id  IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 供应商列表
SELECT count(1) from RFQ_SUPPLIER_LIST r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 报名表
-- SELECT count(1) from RFQ_PREAUDIT r where R.REQUEST_ID IN (SELECT rr.ID FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0')
-- 角色表
SELECT count(1) from RFQ_PROJECT_USER r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0';
-- 报名供应商表
SELECT count(1) from RFQ_PREAUDIT_SUPPLIER r where R.PA_ID IN (SELECT rr.PA_ID from RFQ_PREAUDIT rr where rr.REQUEST_ID in SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0');
-- 报价物料表
SELECT count(1) from RFQ_QUOTATION_ITEM r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 报价结果表
SELECT count(1) from RFQ_QUOTATION_RESULT r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 条款表
SELECT count(1) from RFQ_TERMS r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 角色权限表
SELECT count(1) from RFQ_PROJECT_USER_PRIVILEGE r where r.puId SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 报价锁表
SELECT count(1) from RFQ_QUOTATION_LOCK r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 报价表
SELECT count(1) from RFQ_QUOTATION r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID' 
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0' and r.IS_DELETED='0' and r.CURRENT_FLAG = '1'
-- 规则表
SELECT count(1) from RFQ_RULES r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0'
-- 附件表
SELECT "COUNT"(1) from RFQ_ATTACHMENTS r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0' and r.IS_DELETED_FLAG='0'  and r.type='R'  and r.download_url ='/rfq/attachments/'
UNION SELECT "COUNT"(1) from RFQ_ATTACHMENTS ra where RA.QUOTATION_ID IN (SELECT r."ID" from RFQ_QUOTATION r where R.REQUEST_ID IN SELECT rr.`ID` FROM rfq_request rr  WHERE RFQ_METHOD='BID'
 AND ou_id IN ('110','112','144')
AND IS_DETELED = '0' and r.CURRENT_FLAG = '1' and r.IS_DELETED='0') and RA.type IN( 'Q_BPMS','Z_BPMS') and RA.IS_DELETED_FLAG='0' and  RA.download_url ='/rfq/attachments/'
