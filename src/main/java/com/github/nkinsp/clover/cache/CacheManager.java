package com.github.nkinsp.clover.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CacheManager {

	Object get(Class<?> tableClass,Object key);
	
	Object getAndSet(Class<?> tableClass,Object key,Supplier<Object> supplier);
	
	void set(Class<?> tableClass, Object key,Object value);
	
	List<Object> multiGet(Class<?> tableClass, Collection<Object> keys);
	
	void multiSet(Class<?> tableClass,Map<Object, Object> data);
	
	void multiGetAndSet(Class<?> tableClass,Collection<Object> keys,String keyName,Function<Collection<Object>, Map<Object, Object>> func);
	
	void delete(Class<?> tableClass, Object key);
	
	void delete(Class<?> tableClass,Collection<Object> keys);
	
	
}
