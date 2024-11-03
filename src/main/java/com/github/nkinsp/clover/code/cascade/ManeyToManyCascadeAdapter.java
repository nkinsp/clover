package com.github.nkinsp.clover.code.cascade;

import java.util.HashMap;
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
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.ObjectUtils;

public class ManeyToManyCascadeAdapter implements CascadeAdapter{

	
	@Override
	public JoinType joinType() {
		return JoinType.MANY;
	}
	
	private Map<Object, List<?>> getMiddleMapData(DbContext dbContext,CascadeInfo info,Object[] joinFieldValues){
		
		
		
		//获取
		BaseRepository<Object, ?> middleRepository = dbContext.createRepository(info.getMiddleTable());

		//中间表
		TableInfo<?> tableInfo = middleRepository.tableInfo();

		//中间表实体映射
		EntityMapper mapper = tableInfo.getEntityMapper();

		//中间表外表的列
		EntityFieldInfo inverseColumn = mapper.get(info.getInverseColumn());

		//中间表内表的列
		EntityFieldInfo joinColumn = mapper.get(info.getJoinColumn());

		//中间表数据
		Rows<?> middleRows = middleRepository.findRowsBy(s -> s.where().in(joinColumn.getColumnName(), joinFieldValues));
		
		//中间表外表的列 数据
		List<Object> inverseValues = middleRows.map(x -> inverseColumn.invokeGet(x)).distinct()
				.collect(Collectors.toList());

		//关联表
		BaseRepository<Object, ?> joinTableRepository = dbContext.createRepository(info.getJoinTable());

		TableInfo<?> joinTableInfo = joinTableRepository.tableInfo();

		EntityFieldInfo joinTableIdInfo = joinTableInfo.getEntityMapper().get(joinTableInfo.getPrimaryKeyName());

		Map<Object, ?> middleRowMap = middleRows.collect(Collectors.groupingBy(x -> joinColumn.invokeGet(x)));

		Map<Object, ?> joinDataMap = joinTableRepository.findByIds(inverseValues)
				.toMap(x -> joinTableIdInfo.invokeGet(x), v -> ObjectUtils.copy(info.getResultTypeClass(), v));

		Map<Object, List<?>> dataMap = new HashMap<>(middleRowMap.size());

		middleRowMap.forEach((key, value) -> {

			List<?> values = (List<?>) value;

			List<?> list = values.stream().map(x -> joinDataMap.get(inverseColumn.invokeGet(x))).filter(x -> x != null)
					.collect(Collectors.toList());

			dataMap.put(key, list);

		});
		
		return dataMap;
	}
	
	private Map<Object, ?> getJoinDataMap(DbContext dbContext,CascadeInfo info,Object[] joinFieldValues){
		
		
		BaseRepository<Object, ?> joinRepository = dbContext.createRepository(info.getJoinTable());
		
		TableInfo<?> tableInfo = joinRepository.tableInfo();
		
		EntityFieldInfo inverseField = tableInfo.getEntityMapper().get(info.getInverseColumn());
		
		Map<Object, ?> inverseDataMap = joinRepository.findRowsBy(info.getResultTypeClass(),s->s.where().in(inverseField.getColumnName(), joinFieldValues))
		.collect(Collectors.groupingBy(x->inverseField.invokeGet(x)));
		

		return inverseDataMap;
	}

	@Override
	public <E,R> void adapter(DbContext dbContext,TableInfo<E> tableInfo,EntityMapper entityMapper,List<R> data, EntityFieldInfo entityFieldInfo) {
	
	
		
		
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		CascadeInfo info = entityFieldInfo.getCascadeInfo();
		
		boolean joinMiddleTable = info.getMiddleTable() != void.class;
				
		String joinName = joinMiddleTable?tableInfo.getPrimaryKeyName():info.getJoinColumn();
		
		EntityFieldInfo joinFieldInfo = entityMapper.get(joinName);
		
		if(joinFieldInfo == null) {
			throw new RuntimeException("column "+joinName+" not mapping ");
		}
		
	
		
		Object[] joinFieldValues = data.stream().map(x->joinFieldInfo.invokeGet(x)).toArray();
		
		Map<Object, ?> dataMap = joinMiddleTable?getMiddleMapData(dbContext,info, joinFieldValues):getJoinDataMap(dbContext,info, joinFieldValues);
		
		
		data.forEach(x->{
			
			
			Object key = joinFieldInfo.invokeGet(x);
			
			Object vaue = dataMap.get(key);
			
			if(vaue != null) {
				entityFieldInfo.invokeSet(x, vaue);
			}
			
		});
		
		
		
		
		
	}

	
	
	

}
