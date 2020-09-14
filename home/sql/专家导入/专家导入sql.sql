
-- 查询出数据
select  tmp.LOGIN_ACCOUNT,tmp.SEX,max(tmp.TECHNOLOGY) as TECHNOLOGY,max(tmp.COMMERCE) as COMMERCE,tmp.ONE_LEVEL_CLASS,'' as TWO_LEVEL_CLASS ,tmp.ON_JOB,tmp.ID  from
(
select t.`ID`,t.`LOGIN_ACCOUNT`,t.`COMMERCE` as c,
   case when t.sex = '男' then '1'
          else '0'
         end as SEX,
    case when t.`COMMERCE` = '技术组' then  '1'
              else '0'
          end as TECHNOLOGY,
  CASE WHEN t.`COMMERCE` = '商务组' THEN  '1'
      ELSE '0'
  END AS COMMERCE,
  CASE WHEN t.`ONE_LEVEL_CLASS` = '1' THEN  t.`TWO_LEVEL_CLASS`
      ELSE ''
  END AS ONE_LEVEL_CLASS,
  case when t.ON_JOB = '是' then '1'
          else '0'
         end as ON_JOB
 from tbq_expert_message t  )
tmp group by tmp.LOGIN_ACCOUNT
 ;

 -- 更新数据
UPDATE tbq_expert_message t SET t.`ON_JOB` = '1' WHERE t.`ON_JOB` = '是';
UPDATE tbq_expert_message t SET t.`ON_JOB` = '0' WHERE t.`ON_JOB` = '否';

UPDATE tbq_expert_message t SET t.`ONE_LEVEL_CLASS` = t.`TWO_LEVEL_CLASS` WHERE t.`ONE_LEVEL_CLASS` = '1';
UPDATE tbq_expert_message t SET t.`TWO_LEVEL_CLASS` = '' ;


UPDATE tbq_expert_message t SET t.`COMMERCE` = '1' WHERE t.`COMMERCE` = '商务组';
UPDATE tbq_expert_message t SET t.`SAVE_QUALITY` = '0' ;
UPDATE tbq_expert_message t SET t.`TECHNOLOGY` = '0' WHERE t.`TECHNOLOGY` IS NULL ;
UPDATE tbq_expert_message t SET t.`TECHNOLOGY` = '1' ,t.`COMMERCE` = '0' WHERE t.`COMMERCE` = '技术组';

-- 导出时的查询
SELECT UUID() AS ID,t.*  FROM tbq_expert_message t ;



SELECT
 UUID() AS ID,
tem.LOGIN_ACCOUNT,
tem.USER_ID,
tem.EMPLOYEE_NAME,
tem.CREDENTIAL_NUMBER,
te.SEX,
tem.BIRTHDAY,
tem.CELLPHONE_NUMBER,
tem.PROVINCE_CODE,
tem.PROVINCE,
tem.CITY_CODE,
tem.CITY,
tem.GRADUATE_SCHOOL,
tem.PROFESSION,
tem.GRADUATION_TIME,
tem.JOIN_WORK_TIME,
tem.TELEPHONE,
tem.FAX,
tem.EMAIL,
tem.CONTACT_ADDRESS,
tem.POSTALCODE,
te.ON_JOB,
tem.EXPERT_COMPANY_NAME,
tem.EXPERT_COMPANY_CODE,
tem.EXPERT_DEPARTMENT,
te.ONE_LEVEL_CLASS,
te.TWO_LEVEL_CLASS,
tem.POSITION,
tem.TECHNICAL_TITLE,
tem.DECLARE_PROFESSION,
tem.QUALIFICATION,
te.COMMERCE,
te.TECHNOLOGY,
tem.SAVE_QUALITY,
tem.EXPERT_COMPANY_CODE AS PURCHASE_USER_ACCOUNT


FROM (
SELECT  tmp.LOGIN_ACCOUNT,tmp.USER_ID,tmp.SEX,
 MAX(tmp.TECHNOLOGY) AS TECHNOLOGY,
 MAX(tmp.COMMERCE) AS COMMERCE,
MAX(tmp.ONE_LEVEL_CLASS) AS ONE_LEVEL_CLASS ,MAX(tmp.TWO_LEVEL_CLASS) AS TWO_LEVEL_CLASS,tmp.ON_JOB,tmp.ID  FROM
(
SELECT t.`ID`,t.`LOGIN_ACCOUNT`,t.USER_ID,`COMMERCE` AS c,
   CASE WHEN t.sex = '男' THEN '1'
          ELSE '0'
         END AS SEX,
    CASE WHEN t.`TECHNOLOGY` = '技术组' THEN  '1'
              ELSE '0'
          END AS TECHNOLOGY,
  CASE WHEN t.`TECHNOLOGY` = '商务组' THEN  '1'
      ELSE '0'
  END AS COMMERCE,
  CASE WHEN t.`TWO_LEVEL_CLASS` = '1' THEN  t.`ONE_LEVEL_CLASS`
      ELSE ''
  END AS ONE_LEVEL_CLASS,
  CASE WHEN t.`TWO_LEVEL_CLASS` = '2' THEN  t.`ONE_LEVEL_CLASS`
      ELSE ''
  END AS TWO_LEVEL_CLASS,
  CASE WHEN t.ON_JOB = '是' THEN '1'
          ELSE '0'
         END AS ON_JOB
 FROM tbq_expert_message t  )
tmp
 GROUP BY tmp.USER_ID
) te LEFT JOIN tbq_expert_message tem ON te.ID = tem.`ID`;
