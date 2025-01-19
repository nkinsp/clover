package com.github.nkinsp.clover.code.cascade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.util.EntityMapperManager;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class OneToOneCascadeAdapter implements CascadeAdapter{

	
	@Override
	public JoinType joinType() {
		// TODO Auto-generated method stub
		return JoinType.ONE;
	}

	@Override
	public <E,R> void adapter(DbContext dbContext,TableInfo<E> tableInfo,EntityMapper mapper,List<R> data, EntityFieldInfo entityFieldInfo) {
	
	
		if (CollectionUtils.isEmpty(data)) {
			return;
		}

		CascadeInfo info = entityFieldInfo.getCascadeInfo();
		EntityFieldInfo joinColumnField = mapper.get(info.getJoinColumn());
		List<Object> joinFieldValues = data.stream()
				.map(joinColumnField::invokeGet)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		if(CollectionUtils.isEmpty(joinFieldValues)){
			return;
		}

		Class<?> joinTable = info.getJoinTable();

		BaseRepository<Object, ?> repository = dbContext.createRepository(joinTable);

		TableInfo<?> joinTableInfo = repository.tableInfo();

		EntityMapper entityMapper = EntityMapperManager.getEntityMapper(info.getResultTypeClass());


		Rows<?> rows = repository.findByIds(entityMapper.getEntityClass(),joinFieldValues);

		if(CollectionUtils.isEmpty(rows)){
			return;
		}

		EntityFieldInfo idFieldInfo = entityMapper.get(joinTableInfo.getPrimaryKeyName());

		Map<Object, ?> joinDataMap = rows.toMap(idFieldInfo::invokeGet, v->ObjectUtils.copy(info.getResultTypeClass(), v));
		
		
		for (Object object : data) {
			
			Object key = joinColumnField.invokeGet(object);
			
			entityFieldInfo.invokeSet(object, joinDataMap.get(key));
			
			
		}

		
	}

}
