# java8日期时间

## 加一天

```java
/*
* @param timeFormat "yyyy-MM-dd"
*/
public static String getYesterdayByFormat(String timeFormat){
    return LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern(timeFormat));
}
```