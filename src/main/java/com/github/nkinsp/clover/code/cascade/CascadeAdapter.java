package com.github.nkinsp.clover.code.cascade;

import java.util.List;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.TableInfo;

public interface CascadeAdapter {

	JoinType joinType();
	
	<E,R> void adapter(DbContext dbContext,TableInfo<E> tableInfo,List<R> data,EntityFieldInfo entityFieldInfo);
}
