package com.github.nkinsp.clover.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.LockManager;

@SuppressWarnings("unchecked")
public class SpringDataRedisCacheManger implements CacheManager {

	
	private RedisTemplate<Object, Object> redisTemplate;
	
	

	
	@Override
	public <T, K> T get(Class<T> tableClass, K key) {
		 
			
		return (T) redisTemplate.opsForHash().get(tableClass.getName(), key);	
	}
	


	@Override
	public <T, K> T getAndSet(Class<T> tableClass, K key, Supplier<T> supplier) {
		
		try {
			LockManager.lock(key);
			T value = get(tableClass, key);
			if(value == null) {
				value = supplier.get();
				if(value != null) {
					set(tableClass, key, value);
				}
			}		
			return value;
			
		}finally {
			LockManager.unLock(key);
		}
		
	
	}
	

	@Override
	public <T, K> void set(Class<T> tableClass, K key, T value) {
		
		redisTemplate.opsForHash().put(tableClass.getName(), key, value);
		
	}
	
	

	@Override
	public <T, K> List<T> multiGet(Class<T> tableClass, Collection<K> keys) {
		
		
		
		return  (List<T>)redisTemplate.opsForHash().multiGet(tableClass.getName(),(Collection<Object>) keys);
		 	
		
	}
	

	@Override
	public <T, K> void multiSet(Class<T> tableClass, Map<K, T> data) {
		
		redisTemplate.opsForHash().putAll(tableClass.getName(), data);
		
	}

	
	@Override
	public <T, K> void multiSet(Class<T> tableClass, String keyName, List<T> values) {
		
		EntityMapper mapper = EntityMapperManager.getEntityMapper(tableClass);
		
		EntityFieldInfo fieldInfo = mapper.getByColumnName(keyName);
		
		Map<K, T> data = values.stream().collect(Collectors.toMap(k->(K)fieldInfo.invokeGet(k), v->(T)v));
		
		multiSet(tableClass, data);
		
	}

	
	
	@Override
	public <T, K> void delete(Class<T> tableClass, K key) {
		
		redisTemplate.opsForHash().delete(tableClass.getName(), key);
		
	}
	

	
	@Override
	public <T, K> void delete(Class<T> tableClass, Collection<K> keys) {
		
		redisTemplate.opsForHash().delete(tableClass.getName(), keys);
		
	}

	

	@Override
	public <T, K> List<T> multiGetAndSet(Class<T> tableClass, Collection<K> keys, String keyName,
			Function<Collection<K>, List<T>> func) {
	
		List<T> values = multiGet(tableClass, keys);
		
		EntityMapper mapper = EntityMapperManager.getEntityMapper(tableClass);
		
		EntityFieldInfo fieldInfo = mapper.getByFieldName(keyName);
		
		Set<K> cacheKeys = values.stream().map(x->(K)fieldInfo.invokeGet(x)).collect(Collectors.toSet());
		
		List<K> noCacheKeys = keys.stream().filter(k->!cacheKeys.contains(k)).collect(Collectors.toList());
		
		if(!noCacheKeys.isEmpty()) {
			List<T> lists = func.apply(noCacheKeys);
			ArrayList<T> dataValues = new ArrayList<>();
			dataValues.addAll(values);
			dataValues.addAll(lists);
			multiSet(tableClass,keyName, lists);
			return dataValues;
		}
		
		return values;
	}
	

	public SpringDataRedisCacheManger(RedisTemplate<Object, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	
	

}
