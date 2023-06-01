package com.github.nkinsp.clover.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.nkinsp.clover.query.DefaultSelectColumnMapperRender;
import com.github.nkinsp.clover.query.SelectColumnMapperRender;



@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectMapperColumn {

	
	String value() default "";
	
	Class<? extends SelectColumnMapperRender<Object>> render() default DefaultSelectColumnMapperRender.class;
	
}
