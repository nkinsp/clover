package com.github.nkinsp.clover.annotation;

import com.github.nkinsp.clover.query.QueryWrapper;

public @interface OneToMany {

	
	Class<?> joinTable();
	
	
	String joinColumn();
	
	
	
}
