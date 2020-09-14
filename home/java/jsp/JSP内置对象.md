# JSP内置对象

## JSP内置对象与EL变量

| JSP内置对象 | EL名称           |
| ----------- | ---------------- |
| Page        | PageScope        |
| Request     | RequestScope     |
| Session     | SessionScope     |
| Application | ApplicationScope |

## EL隐式对象

| 序号 | 隐式对象         | 意义                                          |
| ---- | ---------------- | --------------------------------------------- |
| 1    | pageContext      | 对应于当前页面的处理                          |
| 2    | pageScope        | 与页面作用域属性的名称和值相关联的Map类       |
| 3    | requestScope     | 与请求作用域属性的名称和值相关联的Map类       |
| 4    | sessionScope     | 与会话作用域属性的名称和值相关联的Map类       |
| 5    | applicationScope | 与应用程序作用域属性的名称和值相关联的Map类   |
| 6    | param            | 按名称存储请求参数的主要值的Map类             |
| 7    | paramValues      | 将请求参数的所有值作为String数组存储的Map类   |
| 8    | Header           | 按名称存储请求头主要值的Map类                 |
| 9    | headerValues     | 将请求头的所有值作为String数组存储的Map类     |
| 10   | cookie           | 按名称存储请求附带的cookie的Map类             |
| 11   | initParam        | 按名称存储Weby应用程序上下文初始化参数的Map类 |

