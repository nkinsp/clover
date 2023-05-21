package com.github.nkinsp.clover.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.nkinsp.clover.enums.JoinType;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cascade {

	
	/**
	 * 类型
	 * @return
	 */
	JoinType joinType();
	
	String  joinColumn() default "";
	
	Class<?> joinTable();
	
	String inverseJoinColumn() default "";
	
	String middleTable() default "";
}
