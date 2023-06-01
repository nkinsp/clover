package com.github.nkinsp.clover.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.nkinsp.clover.code.PrimaryKeyGenerator;
import com.github.nkinsp.clover.enums.PrimaryKeyType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	
	/**
	 * 实体类对应对的表名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 主键名称
	 * @return
	 */
	String primaryKeyName() default "id";
	
	
	/**
	 * 主键类型
	 * @return
	 */
	PrimaryKeyType primaryKeyType() default PrimaryKeyType.AUTO_INCREMENT;
	
	
	/**
	 * 主键生成
	 * @return
	 */
	Class<?> primaryKeyGenerator() default PrimaryKeyGenerator.class;
	
	/**
	 * 逻辑删除
	 * @return
	 */
	boolean logicDelete() default false;
	
	/**
	 * 逻辑删除字段名
	 * @return
	 */
	String logicDeleteColumn() default "deleted";
	
	
	/**
	 * 
	 * @return
	 */
	String keySequence() default "";
	
	
	/**
	 * 是否缓存 
	 * @return
	 */
	boolean cache() default false;


}
