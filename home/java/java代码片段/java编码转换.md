# 编码转换

**Unicode  - > UTF-8**

```java
    try {
        String s = "\u5639\u563b";
        byte[] bytes = s.getBytes("UTF-8");
        System.out.println(new String(bytes));
    }catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
```

