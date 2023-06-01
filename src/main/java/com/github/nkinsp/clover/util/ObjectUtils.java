package com.github.nkinsp.clover.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

/**
 * 对象工具类
 * @author yue
 *
 */
public class ObjectUtils {

	
	/**
	 * 判断对象是否为空
	 * @param object
	 * @return
	 */
	public static boolean isEmpty(Object object) {
		
		if(object == null) {
			return true;
		}
		if("".equals(object)) {
			return true;
		}
		if (object instanceof Map) {
			if (((Map<?, ?>) object).isEmpty()) {
				return true;
			}
		}
		if (object instanceof Collection) {
			if (((Collection<?>) object).isEmpty()) {
				return true;
			}
		}
		if (object.getClass().isArray()) {
			Object[] arr = (Object[]) object;
			if (arr.length == 0) {
				return true;
			}
		}
		return false;
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T,En>  List<T> getFieldValues(List<En> ens,String filedName){
	
		
		return ens.stream().map(x->(T)getFieldValue(x, filedName)).filter(v->v != null).distinct().collect(Collectors.toList());
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static <V,R>  R getFieldValue( V target,String filedName){
		 PropertyDescriptor pd = ClassUtils.getProperty(target.getClass(),filedName);
			if(pd != null) {
				try {
					return (R) pd.getReadMethod().invoke(target);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			return null;
	}
	
	
	public static <T> T copy(Class<T> type,Object target) {
		
		 T bean = BeanUtils.instantiate(type);
		 BeanUtils.copyProperties(target, bean);
		 return bean;
	}
	
}
