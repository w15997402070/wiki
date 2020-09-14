# MetaObject

三个常用方法 : 

```
1.MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) 用于包装对象 SystemMetaObject.forObject(object) 代替了这个方法
2. Object getValue(String name) 获取对象属性值,支持OGNL
3. setValue(String name, Object value) 修改对象属性值
```