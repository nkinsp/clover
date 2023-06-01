package com.github.nkinsp.clover.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CacheManager {

	<T,K> T get(Class<T> tableClass,K key);
	
	<T,K>T getAndSet(Class<T> tableClass,K key,Supplier<T> supplier);
	
    <T,K> void set(Class<T> tableClass, K key,T value);
	
	<T,K> List<T> multiGet(Class<T> tableClass, Collection<K> keys);
	
	<T,K> void multiSet(Class<T> tableClass,Map<K, T> data);
	
	<T,K> void multiSet(Class<T> tableClass,String keyName,List<T> values);
	
	<T,K> List<T> multiGetAndSet(Class<T> tableClass,Collection<K> keys,String keyName,Function<Collection<K>,List<T>> func);
	
	<T,K> void delete(Class<T> tableClass, K key);
	
	<T,K> void delete(Class<T> tableClass,Collection<K> keys);
	
	
}
