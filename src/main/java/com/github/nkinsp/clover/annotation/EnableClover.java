package com.github.nkinsp.clover.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.nkinsp.clover.config.DbContextConfiguration;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DbContextConfiguration.class)
public @interface EnableClover {

	String[] basePackages() default {};  
	
	String dbContextBeanName() default "dbContext";
	
}
