## Servlet接口

Servlet 接口

```java
package javax.servlet;

import java.io.IOException;

public interface Servlet {
    // 初始化方法
    void init(ServletConfig config) throws ServletException;

    ServletConfig getServletConfig();
    //处理业务方法
    void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;

    String getServletInfo();
    //销毁调用
    void destroy();
}
```

ServletConfig

```java
import java.util.Enumeration;

public interface ServletConfig {

    public String getServletName();

    public ServletContext getServletContext();

    public String getInitParameter(String name);

    public Enumeration<String> getInitParameterNames();
}
```

GenericServlet 通用的,协议无关的Servlet

```java
package javax.servlet;

import java.io.IOException;
import java.util.Enumeration;

public abstract class GenericServlet implements Servlet, ServletConfig,
        java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private transient ServletConfig config;


    public GenericServlet() {
        // NOOP
    }

    @Override
    public void destroy() {
        // NOOP by default
    }

    @Override
    public String getInitParameter(String name) {
        return getServletConfig().getInitParameter(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        return getServletConfig().getInitParameterNames();
    }
    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }
    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        this.init();
    }

    public void init() throws ServletException {
        // NOOP by default
    }

    public void log(String msg) {
        getServletContext().log(getServletName() + ": " + msg);
    }

    public void log(String message, Throwable t) {
        getServletContext().log(getServletName() + ": " + message, t);
    }

    @Override
    public abstract void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException;

    @Override
    public String getServletName() {
        return config.getServletName();
    }
}
```

HttpServlet 一个创建 HttpServlet的 抽象类,提供子类继承,子类必须重写下面的一个方法

* doGet

* doPost

* doPut

* doDelete

* init

* destory

* getServletInfo

```java
package javax.servlet.http;

public abstract class HttpServlet extends GenericServlet {

     protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String method = req.getMethod();

        if (method.equals(METHOD_GET)) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                // servlet doesn't support if-modified-since, no reason
                // to go through further expensive logic
                doGet(req, resp);
            } else {
                long ifModifiedSince;
                try {
                    ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
                } catch (IllegalArgumentException iae) {
                    // Invalid date header - proceed as if none was set
                    ifModifiedSince = -1;
                }
                if (ifModifiedSince < (lastModified / 1000 * 1000)) {
                    // If the servlet mod time is later, call doGet()
                    // Round down to the nearest second for a proper compare
                    // A ifModifiedSince of -1 will always be less
                    maybeSetLastModified(resp, lastModified);
                    doGet(req, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            }

        } else if (method.equals(METHOD_HEAD)) {
            long lastModified = getLastModified(req);
            maybeSetLastModified(resp, lastModified);
            doHead(req, resp);

        } else if (method.equals(METHOD_POST)) {
            doPost(req, resp);

        } else if (method.equals(METHOD_PUT)) {
            doPut(req, resp);

        } else if (method.equals(METHOD_DELETE)) {
            doDelete(req, resp);

        } else if (method.equals(METHOD_OPTIONS)) {
            doOptions(req,resp);

        } else if (method.equals(METHOD_TRACE)) {
            doTrace(req,resp);

        } else {
            //
            // Note that this means NO servlet supports whatever
            // method was requested, anywhere on this server.
            //

            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = new Object[1];
            errArgs[0] = method;
            errMsg = MessageFormat.format(errMsg, errArgs);

            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
        }
    }
}
```

## ServletRequest接口

ServletRequest接口 的实现类封装了客户端请求的所有信息,如果使用Http协议,则包括Http的请求行和请求头部.

Http 对应的请求对象类型为 HttpServletRequest类

* 获取头部信息
  
  * String getHeader(String name)
  
  * long getDateHeader(String name)
  
  * Enumeration<String> getHeaders(String name)
  
  * Enumeration<String> getHeaderNames();
  
  * getIntHeader(String name)

* 获取请求路径
  
  * String getContextPath()
  
  * String getServletPath()
  
  * String getPathInfo()
  
  * requestUrI = contextPath + servletPath + pathInfo

* 获取cookie的方法
  
  * getCookies()

* 获取客户端语言环境
  
   对应 Accept-Language
  
  * getLocale()
  
  * getLocales()

* 获取客户端编码
  
  getCharacterEncoding() 对应 Content-Type

## ServletContext接口

ServletContext接口定义了运行所有Servlet的web应用视图

* 某个Web应用的Servlet全局存储空间，某Web应用对应的所有Servlet共有的各种资源和功能的访问。

* 获取Web应用的部署描述配置文件的方法，例如 getlnitParameter() 和getInitParameterNames。

* 添加Servlet到ServletContext里面的方法，例如addServlet。

* 添加Filter（过滤器）到ServletContext里面的方法，例如addFilter。

* 添加Listener（监听器）到ServletContext里面的方法，例如addListener。

* 全局的属性保存和获取功能，例如setAtribute、getAtribute、getAtributeNames和removeAttribute等。

* 访问Web应用静态内容的方法，例如 getResource 和 getResourceAsStream，以路径作为参数进行查询，此参数要以"/”开头，相对于Web应用上下文的根或相对于Web应用

## ServletResponse接口

ServletResponse接口的对象封装了服务器要返回客户端的所有信息.如果使用Http,则包含了Http的响应行,响应头部和响应体

ServletResponse 接口对应的 Http的实现对象为 HttpServletResponse

## Filter接口

Filter接口允许Web容器对请求和响应做统一处理

可以使用`@WebFilter`注解

## 会话

Http对应的会话接口是`HttpSession`

Cookie是常用的会话跟踪机制,其中cookie的标准名字必须为 `JSESSIONID`

## 注解

Web应用中,使用的注解的类只有被放到`WEB-INF/classes` 目录或`WEB-INF/lib`目录下的jar,注解才会被Web容器处理

* `@WebServlet` 注解用于在Web项目中定义Servlet，它必须指定urlPatterns或value属性，默认的name属性为完全限定类名，`@WebServlet`注解的类必须继承javax.servlet.http.HttpServlet类。

* `@WebFilter`注解用于在Web项目定义Filter，它必须指定urlPatterns、servletNames或value属性，默认的filterName属性为完全限定类名，使用@WebFilter注解的类必须实现javax.servlet.Filter。

* `@WeblnitParam` 注解用于指定传递到Servlet或Filter的初始化参数，它是WebServlet和WebFilter注解的一个属性。

* `@WebListener` 注解用于定义Web应用的各种监听器，使用@WebListener注解的类必须实现以下接口中的一个：
  
  * `javax. servlet. ServletContextListener`
  
  * `javax. servlet. ServletContextAttributelListener`
  
  *  `javax. servlet. ServletRequestListener` 
  
  * `javax. servlet. ServletRequestAtributeListener `
  
  * `javax. servlet. http. HttpSessionListener `
  
  * `javax. servlet. http. HttpSessionAttributeListener `
  
  * `javax. servlet. http. HttpSessionldListener`

* `@MultipartConfig`注解用于指定Servlet请求期望的是`mime/multipart`类型。

## 请求分发器

负责把请求转发给另外一个Servlet处理,或在响应中包含另外一个Servlet的输出

RequestDispatcher 接口提供了此实现机制


