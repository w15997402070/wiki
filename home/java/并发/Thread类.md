# Thread类

[toc]

## 线程中断



## 线程优先级

```java
public final static int MIN_PRIORITY = 1;
public final static int NORM_PRIORITY = 5; //默认
public final static int MAX_PRIORITY = 10;
//设置线程优先级
public final void setPriority(int newPriority) {
        ThreadGroup g;
        checkAccess();
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        if((g = getThreadGroup()) != null) {
            if (newPriority > g.getMaxPriority()) {
                newPriority = g.getMaxPriority();
            }
            setPriority0(priority = newPriority);
        }
    }
private native void setPriority0(int newPriority);
public final int getPriority() {
        return priority;
    }
```

