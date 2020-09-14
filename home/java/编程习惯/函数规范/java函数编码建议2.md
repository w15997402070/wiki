# 函数编码建议

## **内部函数参数尽量使用基础类型**

* 内部函数参数尽量使用基础类型
* 内部函数返回值尽量使用基础类型

现象描述 : 

```java
//调用代码
double price = 5.1D;
int number = 9;
double total = caculate(price,number);

//计算金额函数
private static Double caculate(Double price, Integer number) {
        return price * number;
}
```

建议方案 : 

```java
//调用代码
double price = 5.1D;
int number = 9;
double total = caculate(price,number);

//计算金额函数
private static double caculate(double price, int number) {
        return price * number;
}
```

## **尽量避免返回的数组和列表为null**

* **尽量避免返回的数组为null，引起不必要的空指针判断**
* **尽量避免返回的列表为null，引起不必要的空指针判断**



## **封装函数传入参数**

* **当传入参数过多时，应封装为参数类**

现象描述 : 

```java
//修改用户函数
public void modifyUser(Long id,String name,Integer age,Integer sex,String address){
    //具体实现
}
```

建议方案 : 

```java
//修改用户函数
public void modifyUser(User user){
    //具体实现
}

//用户类
@Getter
@Setter
@ToString
public class User{
    private Long id;
    private String name;
    pricate Integer age;
    private Integer sex;
    private String address;
}
```

## **尽量用函数替换匿名内部类的实现**

Java匿名内部类的优缺点:

- 在匿名内部类（包括Lambda表达式）中可以直接访问外部类的成员，包括类的成员变量、函数的内部变量。正因为可以随意访问外部变量，所以会导致代码边界不清晰。
- 首先推荐用Lambda表达式简化匿名内部类，其次推荐用函数替换复杂的Lambda表达式的实现。

## **利用return精简不必要的代码**

* **删除不必要的if**
* **删除不必要的else**
* **删除不必要的变量**

现象描述 : 

```java
public boolean isPassed(Double passRate){
        if (Objects.nonNull(passRate) && passRate.compareTo(PASS_THRESHOLD) >= 0){
            return true;
        }
        return false;
}
```

建议方案 : 

```java
public boolean isPassed(Double passRate){
     return Objects.nonNull(passRate) && passRate.compareTo(PASS_THRESHOLD) >= 0
}
```

## **利用临时变量优化代码**

在一些代码中，经常会看到a.getB().getC()...getN()的写法，姑且叫做“函数的级联调用”，代码健壮性和可读性太差。建议：杜绝函数的级联调用，利用临时变量进行拆分，并做好对象空指针检查。

* **利用临时变量厘清逻辑**
* **利用临时变量精简代码**

现象描述 : 

```java
private boolean isRichUser(User user){
    return Objects.nonNull(user.getAccount())
        && Objects.nonNull(user.getAccount().getBalance());
}
```

建议方案 : 

```java
private boolean isRichUser(User user){
    UserAccount account = user.getAccount();
    if(Objects.isNull(account)){
        return false;
    }
    Duble balance = account.getBalance();
    if(Objects.isNull(balance)){
        return flase;
    }
    return true;
}
```

