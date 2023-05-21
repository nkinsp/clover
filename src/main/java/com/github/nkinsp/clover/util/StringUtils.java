package com.github.nkinsp.clover.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringUtils {

	
	private static Map<String, String> camelToUnderMap = new ConcurrentHashMap<String, String>();
	
	private static Map<String, String> underToCamelMap = new ConcurrentHashMap<String, String>();
	
	
	/**
	 * 驼峰转下划线
	 * 
	 * @author hanjiang.Yue
	 * @param str
	 * @return
	 */
	public static String camelToUnder(String str) {

		return camelToUnderMap.computeIfAbsent(str, key -> {
			if (str.contains("_")) {
				return str.toLowerCase();
			}
			StringBuilder builder = new StringBuilder();
			try {
				char[] array = key.toCharArray();
				for (int i = 0; i < array.length; i++) {
					char charStr = array[i];
					if (i > 0 && Character.isUpperCase(charStr)) {
						builder.append("_");
					}
					builder.append(charStr);
				}
				return builder.toString().toLowerCase();
			} finally {
				builder = null;
			}
		});

	}
	
	/**
	 * 下划线转驼峰
	 * @author hanjiang.Yue
	 * @param str
	 * @return
	 */
	public static String  underToCamel(String str) {
		
		return underToCamelMap.computeIfAbsent(str, key->{
			StringBuilder builder = new StringBuilder();
			try {
				char[] array = key.toCharArray();
				for (int i = 0; i < array.length; i++) {
					char charStr = array[i];
					if (charStr == '_') {
						array[i + 1] = Character.toUpperCase(array[i + 1]);
						continue;
					}
					builder.append(charStr);
				}
				return builder.toString();
			} finally {
				builder = null;
			}
		});
	
	}
	
	public static boolean isEmpty(Object value) {
		return null == value|| "".equals(value);
	}
	
	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}
}
