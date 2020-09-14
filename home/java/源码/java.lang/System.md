# java.lang.System类主要方法

## System.currentTimeMillis()

获取毫秒级的时间戳(1970年1月1日0时起的毫秒数)

## System.arraycopy(Object src,  int  srcPos, Object dest, int destPos,int length)

    src      the source array.源数组
    srcPos   starting position in the source array.原数组复制的起始位置
    dest     the destination array.目标数组
    destPos  starting position in the destination data. 目标数组的起始位置
    length   the number of array elements to be copied.复制的数组长度

## System.getProperties(),返回Properties

获取系统的环境变量信息

## System.setProperty(String key, String value)

设置系统的环境变量参数

## System.lineSeparator()

On UNIX systems, it returns  "\n";  
on Microsoft Windows systems it returns  "\r\n".