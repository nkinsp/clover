package com.github.nkinsp.clover.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.nkinsp.clover.enums.JoinType;




@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CascadeMapperColumn {

	
	/**
	 * 当前对象字段
	 * @return
	 */
	String joinColumn() default "";
	
	/**
	 * 联合类型
	 * @return
	 */
	JoinType joinType();
	
	/**
	 * 联合的table
	 * @return
	 */
	Class<?> joinTable() default void.class;
	
	/**
	 * 联合的中间表
	 * @return
	 */
	Class<?> joinMiddleTable() default void.class;
	
	/**
	 * 联合表的字段
	 * @return
	 */
	String inverseColumn() default "";
	
	
	
}
