# java POI导出excel加锁定

	## 设置锁定

```java
Sheet sheet = workbook.getSheetAt(0);
//LOCK_PASSWORD 锁的密码
sheet.protectSheet(LOCK_PASSWORD);
```

## 设置不锁定可以填数据的单元格

不锁定的单元格设置:

```java
private CellStyle getUnLockStyle(Workbook workbook){
    CellStyle style = workbook.createCellStyle();
    style.setLocked(false);
    return style;
}
```

锁定的单元格设置:

```java
private CellStyle getLockStyle(Workbook workbook){
    CellStyle style = workbook.createCellStyle();
    style.setLocked(true);
    return style;
}
```

单元格设置 : 

```java
CellStyle lockStyle = getLockStyle(workbook);
CellStyle unLockStyle = getUnLockStyle(workbook);
int lastRowNum = sheet.getLastRowNum();
int unLockNum = 10;
for (int i = 0; i <= lastRowNum; i++) {
    Row row = sheet.getRow(i);
    int firstCellNum = row.getFirstCellNum();
    int lastCellNum = row.getLastCellNum();
    for (int k = firstCellNum;k < lastCellNum;k++){
        Cell cell = row.getCell(k);
        //设置不锁定的单元格
        if ( k == unLockNum ){
            cell.setCellStyle(unLockStyle);
        }else {
            cell.setCellStyle(lockStyle);
        }
    }
}
```

## excel锁定之后,设置筛选功能

```java
        XSSFWorkbook workbook = new XSSFWorkbook();
	    XSSFSheet sheet = workbook.createSheet("Locking");
        //设置密码
        sheet.protectSheet(LOCK_PASSWORD);
        //设置筛选的区域
        CellRangeAddress cellAddresses = CellRangeAddress.valueOf("B1:D8");
        sheet.setAutoFilter(cellAddresses);
        //需要先设置密码锁定,不然这里会报空指针
        CTSheetProtection sheetProtection = sheet.getCTWorksheet().getSheetProtection();
        sheetProtection.setSelectLockedCells(true);
        sheetProtection.setSelectUnlockedCells(false);
        sheetProtection.setFormatCells(true);
        sheetProtection.setFormatColumns(true);
        sheetProtection.setFormatRows(true);
        sheetProtection.setInsertColumns(true);
        sheetProtection.setInsertRows(true);
        sheetProtection.setInsertHyperlinks(true);
        sheetProtection.setDeleteColumns(true);
        sheetProtection.setDeleteRows(true);
        sheetProtection.setSort(true);
        //这里控制筛选,false就是可以筛选
        sheetProtection.setAutoFilter(false);
        sheetProtection.setPivotTables(true);
        sheetProtection.setObjects(true);
        sheetProtection.setScenarios(true);
```

