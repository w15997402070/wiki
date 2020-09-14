
SELECT COUNT(*) FROM rfq_attachments ra WHERE ra.`REQUEST_ID` IS NULL ;

SELECT CONCAT("'",ra.`REQUEST_ID`,"',") FROM rfq_attachments ra ;

SELECT CONCAT( 'id in (',  GROUP_CONCAT("'",rsl.`ID`,"'"),')' ) AS a FROM rfq_supplier_list rsl LIMIT 0,10000;

SELECT CONCAT("'",rqr.id,"',") FROM rfq_quotation_result rqr  LIMIT 0,10000;

SELECT CONCAT("'",rsl.id,"',") FROM rfq_supplier_list rsl LIMIT 5001,1000;
SELECT CONCAT("DELETE FROM rfq_preaudit_supplier WHERE PAS_ID = '",PAS_ID,"' ;") FROM rfq_preaudit_supplier;


SELECT * FROM rfq_attachments ra LIMIT 0,10000;

SELECT ra.*,CONCAT("update rfq_attachments ra set ra.DOWNLOAD_URL = '", CONCAT(SUBSTRING_INDEX(ra.`DOWNLOAD_URL`,"/",3),"/201711/",SUBSTRING_INDEX(ra.`DOWNLOAD_URL`,"/",-1)),"'  where ra.ATTACHMENT_ID = '",ra.`ATTACHMENT_ID`,"' ;  ") AS updat FROM rfq_attachments ra LIMIT 0,100;

-- 附件
SELECT * FROM `rfq_attachments` r WHERE r.`DOWNLOAD_FILENAME` = '1484215759817';

SELECT
  rr.`ID`,
  rr.`REC_CREATE_TIME`,
  rr.`UNIFIED_RFQ_NUM`,
  rr.`OU_RFQ_NUM`,
  rr.`TITLE`,
  ra.`DOWNLOAD_FILENAME`,
  ra.`ORIGINAL_FILENAME`,
  ra.`ORIGINAL_FILE_TYPE`,
  ra.`FILE_SIZE`,
  ra.`TYPE`,
  ra.`SUPPLIER_CODE`
FROM
  rfq_attachments ra,
  rfq_request rr
WHERE ra.`REQUEST_ID` = rr.`ID`
AND ra.`REQUEST_ID` IN
  (SELECT
    rr.`ID`
  FROM
    rfq_request rr
  WHERE rr.`OU_ID` = '211710'
    AND rr.`RFQ_METHOD` = 'TBQ')
    ORDER BY rr.`REC_CREATE_TIME`;

    SELECT * FROM rfq_attachments ra WHERE ra.`DOWNLOAD_FILENAME` = '20161122193158_99_812359122'

    SELECT a.id, a.UNIFIED_RFQ_NUM, a.TITLE, a.REC_CREATE_TIME, b.TYPE, b.DOWNLOAD_FILENAME FROM rfq_request a, rfq_attachments b WHERE
 a.id = b.REQUEST_ID AND
-- a.REC_CREATE_TIME > '2017-01-01 00:00:00' and a.REC_CREATE_TIME < '2017-09-05 00:00:00' and
a.id = b.REQUEST_ID AND ou_id = '211710' AND  a.`RFQ_METHOD` = 'TBQ' ORDER BY a.REC_CREATE_TIME, a.RFQ_METHOD;



SELECT
  rr.`ID`,
  ra.`ATTACHMENT_ID`,
  rr.`REC_CREATE_TIME`,
  rr.`UNIFIED_RFQ_NUM`,
  rr.`OU_RFQ_NUM`,
  rr.`OU_ID`,
  rr.`OU_NAME`,
  rr.`TITLE`,
  ra.`DOWNLOAD_FILENAME`,
  ra.`ORIGINAL_FILENAME`,
  ra.`ORIGINAL_FILE_TYPE`,
  ra.`FILE_SIZE`,
  ra.`TYPE`,
  ra.`SUPPLIER_CODE`
FROM
  rfq_attachments ra,
  rfq_request rr
WHERE ra.`REQUEST_ID` = rr.`ID`
AND ra.`REQUEST_ID` IN
  (SELECT
    rr.`ID`
  FROM
    rfq_request rr
  WHERE rr.`OU_ID` != '211710'
    AND rr.`RFQ_METHOD` = 'TBQ')
    ORDER BY rr.`REC_CREATE_TIME`;
-- 附件结束
