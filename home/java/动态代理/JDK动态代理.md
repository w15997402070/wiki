# JDK动态代理

代理步骤 : 

* 编写服务类和接口,这个是真正的服务提供者,在JDK代理中接口是必须的
* 编写代理类,提供绑定和代理方法

接口

```Java
public interface HelloService {
    public void sayHello(String name);
}
```

实现类 : 

```Java
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        System.out.println("hello "+name);
    }
}
```

代理 : 

jdk动态代理要实现 `InvocationHandler`的方法 : 

```Java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HelloServiceProxy implements InvocationHandler {

    private Object target;

    public Object bind(Object target){
        this.target = target;
        //第一个参数 : 类加载器
        //第二个参数 : 代理对象接口
        //第三个参数 :this代表当前对象
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("jdk动态代理");
        Object result = null;
        System.out.println("方法准备start");
        result = method.invoke(target,args);
        System.out.println("方法调用end");
        return result;
    }
}

```

测试 : 

```Java
public class HelloServiceTest {
    public static void main (String [] args){
        HelloServiceProxy helloHandler= new HelloServiceProxy();
        HelloService proxy = (HelloService)helloHandler.bind(new HelloServiceImpl());
        proxy.sayHello("jdk代理name");
    }
}
```

## cglib动态代理

接口和实现类和上面一样 : 

代理类 : 

```Java
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class HelloServiceCglib implements MethodInterceptor {

    private Object target;

    public Object getInstance(Object target){
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        //回调方法
        enhancer.setCallback(this);
        //创建代理对象
        return enhancer.create();
    }
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("cglib动态代理");
        Object result = null;
        System.out.println("方法准备start");
        result = methodProxy.invokeSuper(obj,args);
        System.out.println("方法调用end");
        return result;
    }
}
```

测试 : 

```Java
import com.test.proxy.jdkProxy.HelloService;
import com.test.proxy.jdkProxy.HelloServiceImpl;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class HelloServiceCglibTest {

   public static void main (String [] args){
       HelloServiceCglib helloServiceCglib = new HelloServiceCglib();
       HelloService instance = (HelloService)helloServiceCglib.getInstance(new HelloServiceImpl());
       instance.sayHello("cglibName");
   }
}
```

