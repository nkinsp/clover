package com.github.nkinsp.clover.cache.impl;

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
import com.github.nkinsp.clover.util.EntityMapperUtils;

public class SpringDataRedisCacheManger implements CacheManager {

	
	private RedisTemplate<Object, Object> redisTemplate;
	
	
	@Override
	public Object get(Class<?> tableClass, Object key) {
		
		return redisTemplate.opsForHash().get(tableClass.getName(), key);	
	
	}

	@Override
	public Object getAndSet(Class<?> tableClass, Object key, Supplier<Object> supplier) {
		// TODO Auto-generated method stub
			
		Object value = get(tableClass, key);
		if(value == null) {
			value = supplier.get();
			if(value != null) {
				set(tableClass, key, value);
			}
		}		
		return value;
	}


	@Override
	public void set(Class<?> tableClass, Object key, Object value) {
		// TODO Auto-generated method stub
		redisTemplate.opsForHash().put(tableClass.getName(), key, value);
	}
	

	@Override
	public List<Object> multiGet(Class<?> tableClass, Collection<Object> keys) {
		// TODO Auto-generated method stub
		
		return redisTemplate.opsForHash().multiGet(tableClass.getName(), keys);
		
	}

	@Override
	public void multiSet(Class<?> tableClass, Map<Object, Object> data) {
		
		redisTemplate.opsForHash().putAll(tableClass.getName(), data);
		
	}

	
	
	@Override
	public void delete(Class<?> tableClass, Object key) {
		// TODO Auto-generated method stub
		
		redisTemplate.opsForHash().delete(tableClass.getName(), key);
		
	}

	@Override
	public void delete(Class<?> tableClass, Collection<Object> keys) {
		
		redisTemplate.opsForHash().delete(tableClass.getName(), keys);
		
	}

	
	@Override
	public void multiGetAndSet(Class<?> tableClass, Collection<Object> keys, String keyName,Function<Collection<Object>, Map<Object, Object>> func) {
		
		List<Object> values = multiGet(tableClass, keys);
		
		EntityMapper mapper = EntityMapperUtils.getEntityMapper(tableClass);
		
		EntityFieldInfo fieldInfo = mapper.getByFieldName(keyName);
		
		Set<Object> cacheKeys = values.stream().map(x->fieldInfo.invokeGet(x)).collect(Collectors.toSet());
		
		List<Object> noCacheKeys = keys.stream().filter(k->!cacheKeys.contains(k)).collect(Collectors.toList());
		
		if(!noCacheKeys.isEmpty()) {
			Map<Object, Object> data = func.apply(noCacheKeys);
			multiSet(tableClass, data);
		}
		
	}

	public SpringDataRedisCacheManger(RedisTemplate<Object, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	
	

}
