package com.github.nkinsp.clover.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.LockManager;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.J2Cache;
@SuppressWarnings("unchecked")
public class J2CacheCacheManager implements CacheManager {

	
	public static class CacheLockKey{
		
		private String key;

		public CacheLockKey(String key) {
			super();
			this.key = key;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheLockKey other = (CacheLockKey) obj;
			return Objects.equals(key, other.key);
		}

		
		

	
		
		
	}
	
    private CacheChannel cacheChannel  = J2Cache.getChannel();
    
    private <K> List<String> getCacheKeys(Collection<K> keys){
    	
       return keys.stream().map(k->String.valueOf(k)).collect(Collectors.toList());
    }
	
	

	@Override
    public <T, K> T get(Class<T> tableClass, K key) {
    	
    	CacheObject object = cacheChannel.get(tableClass.getName(), key.toString(), false);
		if(object != null) {
			return (T)object.getValue();
		}
		
		return null;
    }
    

	@Override
	public <T, K> T getAndSet(Class<T> tableClass, K key, Supplier<T> supplier) {
		
	
		try {
			
			LockManager.lock(key);
			
			T object = get(tableClass, key);
			
			if(object != null) {
				return object;
			}
			
			object = supplier.get();
			set(tableClass, key, object);
			
			return object;
		} finally {
			
			LockManager.unLock(key);
			
		}
		
	}


	
	@Override
	public <T, K> void set(Class<T> tableClass, K key, T value) {
		
		cacheChannel.set(tableClass.getName(), String.valueOf(key), value);
		
	}
	
	

	@Override
	public <T, K> List<T> multiGet(Class<T> tableClass, Collection<K> keys) {
		
		return cacheChannel.get(tableClass.getName(), getCacheKeys(keys)).values().stream().map(x -> (T) x.getValue())
				.collect(Collectors.toList());
		
	}


	
	@Override
	public <T, K> void multiSet(Class<T> tableClass, Map<K, T> data) {
		
		Map<String, Object> elements = new HashMap<>(data.size());
		
		data.forEach((key,value)->elements.put(String.valueOf(key), value));
		
		cacheChannel.set(tableClass.getName(), elements, false);
		
	}
	

	@Override
	public <T, K> void multiSet(Class<T> tableClass, String keyName, List<T> values) {
		
		EntityMapper mapper = EntityMapperManager.getEntityMapper(tableClass);
		
		EntityFieldInfo fieldInfo = mapper.getByColumnName(keyName);
		
		Map<K, T> data = values.stream().collect(Collectors.toMap(k->(K)fieldInfo.invokeGet(k), v->(T)v));
		
		multiSet(tableClass, data);
		
	}
	

	@Override
	public <T, K> List<T> multiGetAndSet(Class<T> tableClass, Collection<K> keys, String keyName,
			Function<Collection<K>, List<T>> func) {
		
		List<T> values = multiGet(tableClass, keys);
		
		if(values.size() == keys.size()) {
			return values;
		}
		
		
		EntityMapper mapper = EntityMapperManager.getEntityMapper(tableClass);
		EntityFieldInfo fieldInfo = mapper.getByColumnName(keyName);
		Set<K> cacheKeys = values.stream().map(x->(K)fieldInfo.invokeGet(x)).collect(Collectors.toSet());
		List<K> noCacheKeys = keys.stream().filter(k->!cacheKeys.contains(k)).collect(Collectors.toList());
	
		
		List<T> lists = func.apply(noCacheKeys);
		ArrayList<T> dataValues = new ArrayList<>();
		dataValues.addAll(values);
		dataValues.addAll(lists);
		multiSet(tableClass,keyName, lists);
		return dataValues;
		
	}
	


	@Override
	public <T, K> void delete(Class<T> tableClass, K key) {
		cacheChannel.evict(tableClass.getName(),String.valueOf(key));
		
	}

	
	@Override
	public <T, K> void delete(Class<T> tableClass, Collection<K> keys) {
		
		String[] cacheKeys = getCacheKeys(keys).toArray(String[]::new);
		
		cacheChannel.evict(tableClass.getName(), cacheKeys);
		
	}
	
	


}
