-- 查询所有外键
SELECT CONCAT('ALTER TABLE ',b.TABLE_NAME,'  DROP FOREIGN  KEY ',a.CONSTRAINT_NAME,' ;') FROM information_schema.REFERENTIAL_CONSTRAINTS a,information_schema.TABLES b WHERE a.TABLE_NAME=b.TABLE_NAME AND table_schema='rfq_db_test';  

ALTER TABLE rfq_preaudit  DROP FOREIGN  KEY FK_Reference_17 ;
ALTER TABLE rfq_preaudit_supplier  DROP FOREIGN  KEY FK_Reference_16 ;
ALTER TABLE rfq_project_user  DROP FOREIGN  KEY FK_Reference_5 ;
ALTER TABLE rfq_project_user_privilege  DROP FOREIGN  KEY FK_Reference_6 ;
ALTER TABLE rfq_quotation  DROP FOREIGN  KEY FK_Reference_7 ;
ALTER TABLE rfq_quotation_fee  DROP FOREIGN  KEY FK_Reference_9 ;
ALTER TABLE rfq_quotation_item  DROP FOREIGN  KEY FK_Reference_8 ;
ALTER TABLE rfq_quotation_lock  DROP FOREIGN  KEY FK_Reference_10 ;
ALTER TABLE rfq_quotation_result  DROP FOREIGN  KEY FK_Reference_11 ;
ALTER TABLE rfq_request_contacts  DROP FOREIGN  KEY FK_Reference_3 ;
ALTER TABLE rfq_request_item  DROP FOREIGN  KEY FK_Reference_2 ;
ALTER TABLE rfq_request_item_copy  DROP FOREIGN  KEY rfq_request_item_copy_ibfk_1 ;
ALTER TABLE rfq_request_item_copy2  DROP FOREIGN  KEY rfq_request_item_copy2_ibfk_1 ;
ALTER TABLE rfq_rules  DROP FOREIGN  KEY FK_Reference_1 ;
ALTER TABLE rfq_score_input  DROP FOREIGN  KEY FK_Reference_13 ;
ALTER TABLE rfq_score_input  DROP FOREIGN  KEY FK_Reference_15 ;
ALTER TABLE rfq_score_item  DROP FOREIGN  KEY FK_Reference_12 ;
ALTER TABLE rfq_score_supplier  DROP FOREIGN  KEY FK_Reference_14 ;
ALTER TABLE rfq_supplier_list  DROP FOREIGN  KEY FK_Reference_4 ;