# Class源码

[toc]

Class类是泛型,例如`String.class`实际上是一个`Class<String>`类的对象(事实上是唯一的对象)

```java
public final class Class<T> implements java.io.Serializable,
                              GenericDeclaration,
                              Type,
                              AnnotatedElement {
    @CallerSensitive
    public T newInstance()
        throws InstantiationException, IllegalAccessException
    {
        if (System.getSecurityManager() != null) {
            checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), false);
        }

        // NOTE: the following code may not be strictly correct under
        // the current Java memory model.

        // Constructor lookup
        if (cachedConstructor == null) {
            if (this == Class.class) {
                throw new IllegalAccessException(
                    "Can not call newInstance() on the Class for java.lang.Class"
                );
            }
            try {
                Class<?>[] empty = {};
                final Constructor<T> c = getConstructor0(empty, Member.DECLARED);
                // Disable accessibility checks on the constructor
                // since we have to do the security check here anyway
                // (the stack depth is wrong for the Constructor's
                // security check to work)
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Void>() {
                        public Void run() {
                                c.setAccessible(true);
                                return null;
                            }
                        });
                cachedConstructor = c;
            } catch (NoSuchMethodException e) {
                throw (InstantiationException)
                    new InstantiationException(getName()).initCause(e);
            }
        }
        Constructor<T> tmpConstructor = cachedConstructor;
        // Security check (same as in java.lang.reflect.Constructor)
        int modifiers = tmpConstructor.getModifiers();
        if (!Reflection.quickCheckMemberAccess(this, modifiers)) {
            Class<?> caller = Reflection.getCallerClass();
            if (newInstanceCallerCache != caller) {
                Reflection.ensureMemberAccess(caller, this, null, modifiers);
                newInstanceCallerCache = caller;
            }
        }
        // Run constructor
        try {
            return tmpConstructor.newInstance((Object[])null);
        } catch (InvocationTargetException e) {
            Unsafe.getUnsafe().throwException(e.getTargetException());
            // Not reached
            return null;
        }
    }                            
}
```

```java
public T newInstance() //方法返回一个实例,这个实例由类的默认构造器获得
public T cast(Object obj)   //如果给定的类型确实是T的一个子类型，cast方法就会返回一个现在声明为类型T的对象，否则，抛出一个BadCastException异常。
public T[] getEnumConstants() //如果这个类不是enum类或类型T的枚举值的数组，getEnumConstants方法将返回null。
public Constructor<T> getConstructor(Class<?>... parameterTypes)
public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes)
public Constructor<?>[] getConstructors() throws SecurityException   // getConstructor与getdeclaredConstructor方法返回一个Constructor<T>对象
    
public native Class<? super T> getSuperclass();//返回这个类的超类。如果T不是一个类或Object类，则返回null。
```

为了表达泛型类型声明，使用java.lang.reflect包中提供的接口Type。这个接口包含下列子类型：

·Class类，描述具体类型。

* TypeVariable接口，描述类型变量（如T extends Comparable<？super T>）。
* WildcardType接口，描述通配符（如？super T）。
* ParameterizedType接口，描述泛型类或接口类型（如Comparable<？super T>）。
* GenericArrayType接口，描述泛型数组（如T[]）。

```java
	public TypeVariable<Class<T>>[] getTypeParameters() //如果这个类型被声明为泛型类型，则获得泛型类型变量，否则获得一个长度为0的数组。
    public Type getGenericSuperclass()//获得被声明为这一类型的超类的泛型类型；如果这个类型是Object或不是一个类类型（classtype），则返回null。
    public Type[] getGenericInterfaces() //获得被声明为这个类型的接口的泛型类型（以声明的次序），否则，如果这个类型没有实现接口，返回长度为0的数组。
```

`java.lang.reflect.Method`

```java
public TypeVariable<Method>[] getTypeParameters()//如果这个方法被声明为泛型方法，则获得泛型类型变量，否则返回长度为0的数组。
public Type getGenericReturnType() //获得这个方法被声明的泛型返回类型。
public Type[] getGenericParameterTypes()//获得这个方法被声明的泛型参数类型。如果这个方法没有参数，返回长度为0的数组。
```

