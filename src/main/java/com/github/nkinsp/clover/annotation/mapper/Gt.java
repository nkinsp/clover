package com.github.nkinsp.clover.annotation.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.convert.impl.DefaultConvert;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gt {

	String value() default "";
	
	Class<? extends Convert> convert() default DefaultConvert.class;
}
