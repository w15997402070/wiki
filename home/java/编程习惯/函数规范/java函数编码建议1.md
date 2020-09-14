# 编码建议

## 查询列表返回需要的字段

可以节省带宽,查询加快

## 1. String 比较

```java
//现象描述：
//不完善的写法：
thisName != null && thisName.equals(name);
//更完善的写法：
(thisName == name) || (thisName != null && thisName.equals(name));
//建议方案：
Objects.equals(name, thisName);
```

- 函数式编程，业务代码减少，逻辑一目了然；
- 通用工具函数，逻辑考虑周全，出问题概率低。

## 2. 集合判空

```java
//现象描述：
!(list == null || list.isEmpty());
//建议方案：
import org.apache.commons.collections4.CollectionUtils;
CollectionUtils.isNotEmpty(list);
```

## 3. 拆分超大函数

###  **每一个代码块都可以封装为一个函**

每一个代码块必然有一个注释，用于解释这个代码块的功能。

如果代码块前方有一行注释，就是在提醒你——可以将这段代码替换成一个函数，而且可以在注释的基础上给这个函数命名。如果函数有一个描述恰当的名字，就不需要去看内部代码究竟是如何实现的。

```java
//现象描述

// 每日生活函数
public void liveDaily() {    // 吃饭
    // 吃饭相关代码几十行

    // 编码
    // 编码相关代码几十行

    // 睡觉
    // 睡觉相关代码几十行
}
 

```

```java
 //建议
 // 每日生活函数
 public void liveDaily() {    
    eat();    // 吃饭
    code();   // 编码
    sleep(); // 睡觉
}
// 吃饭函数
private void eat() {    
    // 吃饭相关代码
}
// 编码函数
private void code() {   
    // 编码相关代码
}
// 睡觉函数
private void sleep() {    
    // 睡觉相关代码
}
```

### **每一个循环体都可以封装为一个函**

```java
//现象描述

// 生活函数
public void live() {    
    while (isAlive) {       
        eat();   // 吃饭     
        code();  // 编码 
        sleep();// 睡觉
    }
}

```

```java
//建议方案

// 生活函数
public void live() {    
    while (isAlive) {       
        // 每日生活
        liveDaily();
    }
}
// 每日生活函数
private void liveDaily() {    
    eat();   // 吃饭
    code();   // 编码 
    sleep(); // 睡觉
}
```

### 每一个条件体都可以封装为一个函**

```java
//现象描述

// 外出函数
public void goOut() {    // 判断是否周末
    // 判断是否周末: 是周末则游玩
    if (isWeekday()) {       
        // 游玩代码几十行
    }    // 判断是否周末: 非周末则工作
    else {        
        // 工作代码几十行
    }
}
```

```java
//建议方案

// 外出函数
public void goOut() {    // 判断是否周末
    // 判断是否周末: 是周末则游玩
    if (isWeekday()) {
        play();
    }    // 判断是否周末: 非周末则工作
    else {
        work();
    }
}
// 游玩函数
private void play() {    
    // 游玩代码几十行
}
// 工作函数
private void work() {   
    // 工作代码几十行
}
```



## **减少函数代码层级**

### **利用return提前返回函数**

```java
//现象描述
// 获取用户余额函数
public Double getUserBalance(Long userId) {    
    User user = getUser(userId);    
    if (Objects.nonNull(user)) {       
        UserAccount account = user.getAccount();        
        if (Objects.nonNull(account)) {           
            return account.getBalance();        
        }   
    }   
    return null;
}
```

```java
//建议方案
// 获取用户余额函数
public Double getUserBalance(Long userId) {    
    // 获取用户信息
    User user = getUser(userId);    
    if (Objects.isNull(user)) {        
        return null;
    }    // 获取用户账户
    UserAccount account = user.getAccount();   
    if (Objects.isNull(account)) {       
        return null;
    }    // 返回账户余额
    return account.getBalance();
}
```

### **利用continue提前结束循环**

```java
//现象描述

// 获取合计余额函数
public double getTotalBalance(List<User> userList) {    // 初始合计余额
    double totalBalance = 0.0D;    // 依次累加余额
    for (User user : userList) {        // 获取用户账户
        UserAccount account = user.getAccount();       
        if (Objects.nonNull(account)) {           
            // 累加用户余额
            Double balance = account.getBalance();           
            if (Objects.nonNull(balance)) {
                totalBalance += balance;
            }
        }
    }    // 返回合计余额
    return totalBalance;
}
```

```java
//建议方案

// 获取合计余额函数
public double getTotalBalance(List<User> userList) {    
    // 初始合计余额
    double totalBalance = 0.0D;    // 依次累加余额
    for (User user : userList) {        // 获取用户账户
        UserAccount account = user.getAccount();       
        if (Objects.isNull(account)) {           
            continue;
        }        // 累加用户余额
        Double balance = account.getBalance();        
        if (Objects.nonNull(balance)) {
            totalBalance += balance;
        }
    }    // 返回合计余额
    return totalBalance;
}
```

## **封装条件表达式函数**

### **把简单条件表达式封装为函数**

```java
// 获取门票价格函数
public double getTicketPrice(Date currDate) {    
    if (Objects.nonNull(currDate) && currDate.after(DISCOUNT_BEGIN_DATE)
        && currDate.before(DISCOUNT_END_DATE)) {       
        return TICKET_PRICE * DISCOUNT_RATE;
    }   
    return TICKET_PRICE;
}
```

```java
//建议方案

// 获取门票价格函数
public double getTicketPrice(Date currDate) {   
    if (isDiscountDate(currDate)) {       
        return TICKET_PRICE * DISCOUNT_RATE;
    }   
    return TICKET_PRICE;
}
// 是否折扣日期函数
private static boolean isDiscountDate(Date currDate) {    
    return Objects.nonNull(currDate) 
        && currDate.after(DISCOUNT_BEGIN_DATE)
        && currDate.before(DISCOUNT_END_DATE);
}
```

### **把复杂条件表达式封装为函数**

```java
//现象描述

// 获取土豪用户列表
public List<User> getRichUserList(List<User> userList) {    
    // 初始土豪用户列表
    List<User> richUserList = new ArrayList<>();    // 依次查找土豪用户
    for (User user : userList) {       
        // 获取用户账户
        UserAccount account = user.getAccount();        
        if (Objects.nonNull(account)) {            // 判断用户余额
            Double balance = account.getBalance();           
            if (Objects.nonNull(balance) && balance.compareTo(RICH_THRESHOLD) >= 0) {                // 添加土豪用户
                richUserList.add(user);
            }
        }
    }    // 返回土豪用户列表
    return richUserList;
}
```

```java
// 获取土豪用户列表
public List<User> getRichUserList(List<User> userList) {    
    // 初始土豪用户列表
    List<User> richUserList = new ArrayList<>();    
    // 依次查找土豪用户
    for (User user : userList) {       
        // 判断土豪用户
        if (isRichUser(user)) {           
            // 添加土豪用户
            richUserList.add(user);
        }
    }    
    // 返回土豪用户列表
    return richUserList;
}
// 是否土豪用户
private boolean isRichUser(User user) {   
    // 获取用户账户
    UserAccount account = user.getAccount();    
    if (Objects.isNull(account)) {       
        return false;
    }    // 获取用户余额
    Double balance = account.getBalance();    
    if (Objects.isNull(balance)) {      
        return false;
    }    
    // 比较用户余额
    return balance.compareTo(RICH_THRESHOLD) >= 0;
}
```

## **尽量避免不必要的空指针判断**

### **调用函数保证参数不为空，被调用函数尽量避免不必要的空指针判断**

```java
//现象描述
// 创建用户信息
User user = new User();
... 
    // 赋值用户相关信息
    createUser(user);
// 创建用户函数
private void createUser(User user){   
    // 判断用户为空
    if(Objects.isNull(user)) {        
        return;
    }    // 创建用户信息
    userDAO.insert(user);
    userRedis.save(user);
}
```

```java
//建议方案

// 创建用户信息
User user = new User();
... 
    // 赋值用户相关信息
    createUser(user);
// 创建用户函数
private void createUser(User user){    
    // 创建用户信息
    userDAO.insert(user);
    userRedis.save(user);
}
```

### **被调用函数保证返回不为空,调用函数尽量避免不必要的空指针判断**

```java
//现象描述

// 保存用户函数
public void saveUser(Long id, String name) {    
    // 构建用户信息
    User user = buildUser(id, name);    
    if (Objects.isNull(user)) {        
        throw new BizRuntimeException("构建用户信息为空");
    }    // 保存用户信息
    userDAO.insert(user);
    userRedis.save(user);
}
// 构建用户函数
private User buildUser(Long id, String name) {
    User user = new User();
    user.setId(id);
    user.setName(name);    
    return user;
}
```

```java
//建议方案

// 保存用户函数
public void saveUser(Long id, String name) {    
    // 构建用户信息
    User user = buildUser(id, name);    // 保存用户信息
    userDAO.insert(user);
    userRedis.save(user);
}
// 构建用户函数
private User buildUser(Long id, String name) {
    User user = new User();
    user.setId(id);
    user.setName(name);   
    return user;
}
```

### **赋值逻辑保证列表数据项不为空，处理逻辑尽量避免不必要的空指针判断**

```java
// 查询用户列表
List<UserDO> userList = userDAO.queryAll();
if (CollectionUtils.isEmpty(userList)) {    
    return;
}
// 转化用户列表
List<UserVO> userVoList = new ArrayList<>(userList.size());
for (UserDO user : userList) {
    UserVO userVo = new UserVO();
    userVo.setId(user.getId());
    userVo.setName(user.getName());
    userVoList.add(userVo);
}
// 依次处理用户
for (UserVO userVo : userVoList) {    
    // 判断用户为空
    if (Objects.isNull(userVo)) {        
        continue;
    }    
    // 处理相关逻辑
    ...
}
```

```java
//建议方案
// 查询用户列表
List<UserDO> userList = userDAO.queryAll();
if (CollectionUtils.isEmpty(userList)) {    
    return;
}
// 转化用户列表
List<UserVO> userVoList = new ArrayList<>(userList.size());
for (UserDO user : userList) {
    UserVO userVo = new UserVO();
    userVo.setId(user.getId());
    userVo.setName(user.getName());
    userVoList.add(userVo);
}
// 依次处理用户
for (UserVO userVo : userVoList) {    
    // 处理相关逻辑
    ...
}
```

### **MyBatis查询函数返回列表和数据项不为空，可以不用空指针判断**

MyBatis是一款优秀的持久层框架，是在项目中使用的最广泛的数据库中间件之一。通过对MyBatis源码进行分析，查询函数返回的列表和数据项都不为空，在代码中可以不用进行空指针判断。

```java
// 查询用户函数
public List<UserVO> queryUser(Long id, String name) {    
    // 查询用户列表
    List<UserDO> userList = userDAO.query(id, name);   
    if (Objects.isNull(userList)) {        
        return Collections.emptyList();
    }   
    // 转化用户列表
    List<UserVO> voList = new ArrayList<>(userList.size());  
    for (UserDO user : userList) {        
        // 判断对象为空
        if (Objects.isNull(user)) {           
            continue;
        }        
        // 添加用户信息
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        voList.add(vo);
    }    
    // 返回用户列表
    return voList;
}
```

```java
//建议方案

// 查询用户函数
public List<UserVO> queryUser(Long id, String name) {    
    // 查询用户列表
    List<UserDO> userList = userDAO.query(id, name);   
    // 转化用户列表
    List<UserVO> voList = new ArrayList<>(userList.size());    
    for (UserDO user : userList) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        voList.add(vo);
    }   
    // 返回用户列表
    return voList;
}
```

