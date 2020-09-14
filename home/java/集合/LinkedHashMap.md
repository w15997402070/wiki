# LinkedHashMap

## LinkedHashMap中的自动删除功能

LinkedHashMap保存的值是有序的

LinkedHashMap中有一个removeEldestEntry方法，如果这个方法返回true，Map中最前面添加的内容将被删除，它是在添加属性的put或putAll方法被调用后自动调用的。这个功能主要是用在缓存中，用来限定缓存的最大数量，以防止缓存无限地增长。当新的值添加后，如果缓存达到了上限，最开头的值就会被删除，当然这需要设置，设置方法就是覆盖removeEldestEntry方法，当这个方法返回true时就表示达到了上限，返回false就是没达到上限，而size()方法可以返回现在所保存对象的数量，一般用它和设置的值做比较就可以了。

```java
  // org.springframework.web.servlet.view.AbstractCachingViewResolver       
          private final Map<Object, View> viewCreationCache =
			new LinkedHashMap<Object, View>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
				@Override
				protected boolean removeEldestEntry(Map.Entry<Object, View> eldest) {
					if (size() > getCacheLimit()) {
						viewAccessCache.remove(eldest.getKey());
						return true;
					}
					else {
						return false;
					}
				}
			};
```

