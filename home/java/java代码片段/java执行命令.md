# java执行命令

执行命令 : 

```java
    String command = "ls";
    try {
        Process exec = Runtime.getRuntime().exec(command);
    } catch (IOException e) {
        e.printStackTrace();
    }
```

执行命令后获取返回的流 : 

```java
    String command = "ls";
    try {
        Process exec = Runtime.getRuntime().exec(command);
        InputStream inputStream = exec.getInputStream();
        int c;
        while ((c = inputStream.read()) != -1){
            process((char)c);
        }
        inputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
```

给命令设置输入流:

```java
    String command = "cat";
    try {
        Process exec = Runtime.getRuntime().exec(command);
        OutputStream outputStream = exec.getOutputStream();
        outputStream.write("some text".getBytes());
        outputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
```

