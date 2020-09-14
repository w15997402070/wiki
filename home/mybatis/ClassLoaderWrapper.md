# ClassLoaderWrapper

```java
public class ClassLoaderWrapper {

  ClassLoader defaultClassLoader;
  ClassLoader systemClassLoader;

  ClassLoaderWrapper() {
    try {
        //初始化 systemClassLoader 字段
      systemClassLoader = ClassLoader.getSystemClassLoader();
    } catch (SecurityException ignored) {
      // AccessControlException on Google App Engine   
    }
  }
  
  public URL getResourceAsURL(String resource) {
    return getResourceAsURL(resource, getClassLoaders(null));
  }

  public URL getResourceAsURL(String resource, ClassLoader classLoader) {
    return getResourceAsURL(resource, getClassLoaders(classLoader));
  }

  public InputStream getResourceAsStream(String resource) {
    return getResourceAsStream(resource, getClassLoaders(null));
  }

  public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
    return getResourceAsStream(resource, getClassLoaders(classLoader));
  }

  public Class<?> classForName(String name) throws ClassNotFoundException {
    return classForName(name, getClassLoaders(null));
  }

  public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
    return classForName(name, getClassLoaders(classLoader));
  }

  InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
    for (ClassLoader cl : classLoader) {
      if (null != cl) {
        // try to find the resource as passed
        InputStream returnValue = cl.getResourceAsStream(resource);
        // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
        if (null == returnValue) {
          returnValue = cl.getResourceAsStream("/" + resource);
        }
        if (null != returnValue) {
          return returnValue;
        }
      }
    }
    return null;
  }

  URL getResourceAsURL(String resource, ClassLoader[] classLoader) {
    URL url;
    for (ClassLoader cl : classLoader) {
      if (null != cl) {
        // look for the resource as passed in...
          //查找指定的资源
        url = cl.getResource(resource);
        // ...but some class loaders want this leading "/", so we'll add it
        // and try again if we didn't find the resource
          //尝试以"/"开头,再次查找
        if (null == url) {
          url = cl.getResource("/" + resource);
        }
        // "It's always in the last place I look for it!"
        // ... because only an idiot would keep looking for it after finding it, so stop looking already.
        if (null != url) { //查找到指定的资源
          return url;
        }
      }
    }
    // didn't find it anywhere.
    return null;
  }

  Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
    for (ClassLoader cl : classLoader) {
      if (null != cl) {
        try {
          Class<?> c = Class.forName(name, true, cl);
          if (null != c) {
            return c;
          }
        } catch (ClassNotFoundException e) {
          // we'll ignore this until all classloaders fail to locate the class
        }
      }
    }
    throw new ClassNotFoundException("Cannot find class: " + name);
  }
  //返回类加载器数组,指明了类加载器的顺序
  ClassLoader[] getClassLoaders(ClassLoader classLoader) {
    return new ClassLoader[]{
        classLoader, //参数指定的类加载器
        defaultClassLoader,//系统指定的默认类加载器
        Thread.currentThread().getContextClassLoader(),//当前线程绑定的类加载器
        getClass().getClassLoader(),//加载当前类所使用的类加载器
        systemClassLoader //System ClassLoader
    };
  }

}
```

