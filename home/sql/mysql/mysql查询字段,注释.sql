  SELECT CONCAT(e,a,b,c,d) FROM (
SELECT
    @i := @i+1 AS e,
    '=' AS a,
    C.COLUMN_NAME AS b,
    ',' AS c,
    C.column_comment AS d
FROM
  INFORMATION_SCHEMA.Columns C,
  (SELECT @i := 0) t2
WHERE C.table_name = 'zs_ctt_index_update_log'
  AND C.table_schema = 'testoyt') t;

  