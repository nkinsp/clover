package com.github.nkinsp.clover.query.conditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.util.ClassUtils;

public class ConvertManager {

	
	private static Map<Class<?>, Convert> convertMap = new ConcurrentHashMap<Class<?>, Convert>();
	
	
	public static Convert getConvert(Class<?> convertClass) {
		
		return convertMap.computeIfAbsent(convertClass, typeClass->(Convert)ClassUtils.newInstance(typeClass));
	}
	
}
