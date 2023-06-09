package com.github.nkinsp.clover.code.cascade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.BaseRepository;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.CascadeInfo;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ObjectUtils;

public class OneToOneCascadeAdapter implements CascadeAdapter{

	
	@Override
	public JoinType joinType() {
		// TODO Auto-generated method stub
		return JoinType.ONE;
	}

	@Override
	public <E,R> void adapter(DbContext dbContext,TableInfo<E> tableInfo,List<R> data, EntityFieldInfo entityFieldInfo) {
	
	
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		CascadeInfo info = entityFieldInfo.getCascadeInfo();
		
		EntityMapper mapper = tableInfo.getEntityMapper();
		
		EntityFieldInfo joinColumnField = mapper.get(info.getJoinColumn());
		
		List<Object> joinFieldValues = data.stream().map(x->joinColumnField.invokeGet(x)).distinct().collect(Collectors.toList());
		
		
		Class<?> joinTable = info.getJoinTable();
		
		
		BaseRepository<Object, ?> repository = dbContext.createRepository(joinTable);
		
		TableInfo<?> joinTableInfo = repository.tableInfo();

		
		Rows<?> rows = repository.findByIds(joinFieldValues);
		
	
				
		EntityMapper entityMapper = joinTableInfo.getEntityMapper();
		
		EntityFieldInfo idFieldInfo = entityMapper.get( tableInfo.getPrimaryKeyName());

		Map<Object, ?> joinDataMap = rows.toMap(x->idFieldInfo.invokeGet(x),v->ObjectUtils.copy(info.getResultTypeClass(), v));
		
		
		for (Object object : data) {
			
			Object key = joinColumnField.invokeGet(object);
			
			entityFieldInfo.invokeSet(object, joinDataMap.get(key));
			
			
		}
		

		
		
		
	}

}
