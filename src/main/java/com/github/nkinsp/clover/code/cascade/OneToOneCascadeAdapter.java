package com.github.nkinsp.clover.code.cascade;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.util.ObjectUtils;

public class OneToOneCascadeAdapter implements CascadeAdapter{

	private DbContext dbContext;
	
	@Override
	public boolean support(JoinType joinType) {
		return joinType == JoinType.ONE_TO_ONE;
	}

	@Override
	public <E> void adapter(List<E> data, EntityFieldInfo entityFieldInfo) {
		// TODO Auto-generated method stub
		
		if (CollectionUtils.isEmpty(data)) {
			return;
		}			
		List<Object> fieldValues = ObjectUtils.getFieldValues(data, entityFieldInfo.getFieldName());
	
//		dbContext.fi
		
		
		
	}

}
