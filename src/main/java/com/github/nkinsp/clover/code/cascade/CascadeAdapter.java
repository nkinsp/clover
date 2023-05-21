package com.github.nkinsp.clover.code.cascade;

import java.util.List;

import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.table.EntityFieldInfo;

public interface CascadeAdapter {

	boolean support(JoinType joinType);
	
	<E> void adapter(List<E> data,EntityFieldInfo entityFieldInfo);
}
