package com.github.nkinsp.clover.code;

import com.github.nkinsp.clover.table.TableInfo;

public interface KeyGenerator {

	
	Object createId(DbContext dbContext,TableInfo tableInfo); 
	
	
}
