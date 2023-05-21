package com.github.nkinsp.clover.code.handlers;

import com.github.nkinsp.clover.code.DbContext;

public interface ExecuteHandler<R> {

	
	 R handle(DbContext context);
	
	
}
