# 分布式session
 
## 为什么提出分布式session

会话的一致性性问题解决

服务器内部默认会话id,只在同一个服务器之间生效

## 怎么解决

1 . 使用 session内部共享机制,session同步(10台服务器[解决问题,实现非常复杂])  
2 . 使用数据库存储sessionid  
3 . memcache的效率比redis高   
4 . redis可靠性比较高(推荐使用)  

## 实际解决方案 

redis : 登陆成功后 : 服务器生成一个类似sessionid,保存到 redis 中
操作的时候从redis 中获取到sessionid