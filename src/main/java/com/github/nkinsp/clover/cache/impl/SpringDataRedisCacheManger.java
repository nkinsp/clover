package com.github.nkinsp.clover.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.TableInfo;


@SuppressWarnings("unchecked")
public class SpringDataRedisCacheManger implements CacheManager {

	private RedisTemplate<String, Object> redisTemplate;


	public SpringDataRedisCacheManger(RedisTemplate<String, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	
	private String getCacheKey(TableInfo<?> tableInfo, Object key) {
		return tableInfo.getCacheKeyGenerator().createKey(tableInfo, key);
	}

	@Override
	public <T> T get(TableInfo<T> tableInfo, Object key) {
		
		String cacheKey = getCacheKey(tableInfo, key);
				
		return (T) redisTemplate.opsForValue().get(cacheKey);
	}



	@Override
	public <T> T getAndSet(TableInfo<T> tableInfo, Object key, Supplier<T> supplier) {
		
		T value = get(tableInfo, key);
		
		if(value != null) {
			return value;
		}
		value = supplier.get();
		
		if(value != null) {
			
			set(tableInfo, key, value);
		}
		return value;
	}

	@Override
	public <T> void set(TableInfo<T> tableInfo, Object key, T value) {
		
		String cacheKey = getCacheKey(tableInfo, key);
		
		if(tableInfo.getCacheTime() <= 0) {
			
			redisTemplate.opsForValue().set(cacheKey, value);
			return;
		}
		
		
		redisTemplate.opsForValue().set(cacheKey, value, tableInfo.getCacheTime(), tableInfo.getCacheTimeUnit());
		

	}

	@Override
	public <T> List<T> multiGet(TableInfo<T> tableInfo, Collection<Object> keys) {
		
		
		List<String> cacheKeys = keys.stream().map(key->getCacheKey(tableInfo, key)).toList();
		
		return redisTemplate.opsForValue().multiGet(cacheKeys).stream().filter(value->value != null).map(value->(T)value).toList();
	
	}

	@Override
	public <T> void multiSet(TableInfo<T> tableInfo, Map<Object, T> data) {
	
//		redisTemplate.opsForValue().m
		
//		redisTemplate.m
		
		Map<String, Object> cacheDataMap = new HashMap<>(data.size());
		
		for (Entry<Object, T> en : data.entrySet()) {
			
			String cacheKey = getCacheKey(tableInfo, en.getKey());
			
			cacheDataMap.put(cacheKey, en.getValue());
			
		}
		redisTemplate.opsForValue().multiSet(cacheDataMap);
		
		if (tableInfo.getCacheTime() > 0) {

			@SuppressWarnings("rawtypes")
			final RedisSerializer keySerializer = redisTemplate.getKeySerializer();
			List<byte[]> keys = data.keySet().stream().map(k -> keySerializer.serialize(k))
					.collect(Collectors.toList());
			long seconds = TimeoutUtils.toSeconds(tableInfo.getCacheTime(), tableInfo.getCacheTimeUnit());

			// 批量设置过期时间
			redisTemplate.executePipelined(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					for (byte[] key : keys) {
						connection.expire(key, seconds);
					}
					return null;
				}
			});

		}
		

	}

	@Override
	public <T> void multiSet(TableInfo<T> tableInfo, List<T> values) {
		
		Map<Object, T> cacheDataMap = new HashMap<>(values.size());
		EntityFieldInfo id = tableInfo.getEntityMapper().get(tableInfo.getPrimaryKeyName());
		for (T value : values) {
			Object key = id.invokeGet(value);
			cacheDataMap.put(key, value);
		}
		multiSet(tableInfo, cacheDataMap);

	}

	@Override
	public <T> List<T> multiGetAndSet(TableInfo<T> tableInfo, Collection<Object> keys,
			Function<Collection<Object>, List<T>> func) {
		
		List<T> values = multiGet(tableInfo, keys);
		
		if(values.size() == keys.size()) {
			return values;
		}
		
		EntityFieldInfo id = tableInfo.getEntityMapper().get(tableInfo.getPrimaryKeyName());
		Set<Object> cacheKeys = values.stream().filter(x-> x!= null).map(x ->id.invokeGet(x)).collect(Collectors.toSet());
		List<Object> noCacheKeys = keys.stream().filter(k -> !cacheKeys.contains(k)).toList();

		if (!noCacheKeys.isEmpty()) {
			List<T> newValues = func.apply(noCacheKeys);
			ArrayList<T> dataValues = new ArrayList<>();
			dataValues.addAll(values);
			dataValues.addAll(newValues);
			multiSet(tableInfo,newValues);
			return dataValues;
		}
		return values;
	}

	@Override
	public <T> void delete(TableInfo<T> tableInfo, Object key) {
		
		String cacheKey = getCacheKey(tableInfo, key);
		
		redisTemplate.delete(cacheKey);

	}

	@Override
	public <T> void delete(TableInfo<T> tableInfo, Collection<Object> keys) {
		
		List<String> cacheKeys = keys.stream().map(key->getCacheKey(tableInfo, key)).toList();
		
		redisTemplate.delete(cacheKeys);

	}

}
