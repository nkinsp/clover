package com.github.nkinsp.clover.code.cascade;

import java.util.List;

import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.TableInfo;

public interface CascadeAdapter {

	JoinType joinType();
	
	<E,R> void adapter(TableInfo<E> tableInfo,List<R> data,EntityFieldInfo entityFieldInfo);
}
