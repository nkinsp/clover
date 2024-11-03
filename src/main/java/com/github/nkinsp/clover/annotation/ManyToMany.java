package com.github.nkinsp.clover.annotation;

public @interface ManyToMany {

	
	
	Class<?> joinMiddleTable();
	
	
	Class<?> joinTable();
	
	
	String joinColumn();
	
	
	String inverseColumn();
	
	
}
