package com.github.nkinsp.clover.code.cascade;

import java.util.*;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.util.EntityMapperManager;
import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.CascadeInfo;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ObjectUtils;

public class ManyCascadeAdapter implements CascadeAdapter{

	
	@Override
	public JoinType joinType() {
		return JoinType.MANY;
	}
	
	private Map<Object, Rows<?>> getMiddleMapData(DbContext dbContext, CascadeInfo info, Object[] joinFieldValues){
		

		var middleRepository = dbContext.createRepository(info.getMiddleTable());
		//中间表
		var tableInfo = middleRepository.tableInfo();
		//中间表实体映射
		EntityMapper mapper = tableInfo.getEntityMapper();
		//中间表内表的列
		var joinColumn = mapper.get(info.getJoinColumn());
		var inverseColumn = mapper.get(info.getInverseColumn());

		//查询数据
		var middleRows = middleRepository.findRowsBy(s -> s.where().in(joinColumn.getColumnName(), joinFieldValues));
		
		//中间表外表的列 数据
		List<Object> inverseValues = middleRows.map(inverseColumn::invokeGet).distinct()
				.collect(Collectors.toList());
		//关联表
		var joinTableRepository = dbContext.createRepository(info.getJoinTable());
		var joinTableInfo = joinTableRepository.tableInfo();
		//主键
		var joinTableIdInfo = joinTableInfo.getEntityMapper().get(joinTableInfo.getPrimaryKeyName());
		var middleRowMap = middleRows.groupBy(joinColumn::invokeGet);

		//查询关联表数据
		Map<Object, ?> joinDataMap = joinTableRepository.findByIds(inverseValues)
				.toMap(joinTableIdInfo::invokeGet, v -> ObjectUtils.copy(info.getResultTypeClass(), v));

		Map<Object, Rows<?>> dataMap = new HashMap<>(middleRowMap.size());

		middleRowMap.forEach((key, value) -> {

			Rows<?> list = value
					.map(x -> joinDataMap.get(inverseColumn.invokeGet(x)))
					.filter(Objects::nonNull);
			dataMap.put(key, list);

		});
		
		return dataMap;
	}
	
	private Rows<?> getJoinDataRows(DbContext dbContext, CascadeInfo info, Object[] joinFieldValues){

		var joinRepository = dbContext.createRepository(info.getJoinTable());
		var tableInfo = joinRepository.tableInfo();
		var inverseColumn = tableInfo.getEntityMapper().get(info.getInverseColumn());
		//条件是主键
		if(tableInfo.getPrimaryKeyName().equals(info.getInverseColumn())){
			return joinRepository.findByIds(List.of(joinFieldValues));
		}
		return joinRepository.findRowsBy(
				info.getResultTypeClass(), s -> s.
						where().
						in(inverseColumn.getColumnName(), joinFieldValues)
		);
	}

	private <R> Object[] getJoinFieldValues(List<R> data,EntityFieldInfo entityFieldInfo){
		Class<?> fieldType = entityFieldInfo.getField().getType();
		if(Collection.class.isAssignableFrom(fieldType)){

			return  data.stream()
					.map(entityFieldInfo::invokeGet)
					.filter(Objects::nonNull)
					.map(x->{
						if(x instanceof Collection<?> values){
							return values;
						}
						return List.of();
					})
					.flatMap(Collection::stream)
					.distinct()
					.toArray();

		}

		if(fieldType.isArray()){
			return data.stream().map(entityFieldInfo::invokeGet)
					.filter(Objects::nonNull)
					.map(x->(Object[]) x )
					.map(List::of)
					.flatMap(Collection::stream)
					.distinct()
					.toArray();
		}

		return data.stream().map(entityFieldInfo::invokeGet).filter(Objects::nonNull)
				.distinct()
				.toArray();

	}

	private boolean valueFilter(Object data,Set<Object> set,EntityFieldInfo inverseColumnFieldInfo){

		Object object = inverseColumnFieldInfo.invokeGet(data);
		return object != null && set.contains(object);

	}

	private Object getValue(Rows<?> dataRows,Object data,EntityFieldInfo inverseColumnFieldInfo,EntityFieldInfo joinFieldInfo){


		Class<?> fieldType = joinFieldInfo.getField().getType();
		if(fieldType.isArray()){
			Object[] values =(Object[]) joinFieldInfo.invokeGet(data);
			if(values == null){
				return new Object[]{};
			}
			Set<Object> valueSet = new HashSet<>(List.of(values));
			return dataRows.filter(x->valueFilter(x,valueSet,inverseColumnFieldInfo)).toArray();
		}
		//集合
		if(Collection.class.isAssignableFrom(fieldType)){

			List<?> rows = new ArrayList<>();
			Collection<?> values =(Collection<?>) joinFieldInfo.invokeGet(data);
			if(values != null){
				rows = dataRows.filter(x->valueFilter(x,new HashSet<>(values),inverseColumnFieldInfo));
			}
			if(Set.class.isAssignableFrom(fieldType)){
				return new HashSet<>(rows);
			}
			return rows;
		}

		Object value = joinFieldInfo.invokeGet(data);

		if(value != null){

			return dataRows.filter(x->  value.equals(inverseColumnFieldInfo.invokeGet(x)));
		}
		return null;


	}


	@Override
	public <E,R> void adapter(DbContext dbContext,TableInfo<E> tableInfo,EntityMapper entityMapper,List<R> data, EntityFieldInfo entityFieldInfo) {

		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		CascadeInfo info = entityFieldInfo.getCascadeInfo();

		boolean hasMiddleTable = info.getMiddleTable() != void.class;
				
		String joinName = hasMiddleTable?tableInfo.getPrimaryKeyName():info.getJoinColumn();
		
		EntityFieldInfo joinFieldInfo = entityMapper.get(joinName);
		
		if(joinFieldInfo == null) {
			throw new RuntimeException("column "+joinName+" not mapping ");
		}

		Object[] joinFieldValues = getJoinFieldValues(data,joinFieldInfo);

		//有中间表
		if(hasMiddleTable){

			Map<Object, Rows<?>> mapData = getMiddleMapData(dbContext, info, joinFieldValues);



			data.forEach(x->{

				Object key = joinFieldInfo.invokeGet(x);
				if(key == null){
					return;
				}
				Object value = mapData.get(key);
				if(value != null){
					entityFieldInfo.invokeSet(x,value);
				}

			});



			return;

		}



		//没有中间表直接关联
        Rows<?> dataRows=  getJoinDataRows(dbContext,info,joinFieldValues);
		var inverseColumnFieldInfo = EntityMapperManager.getEntityMapper(info.getResultTypeClass()).get(info.getInverseColumn());
		data.forEach(x->{
			Object value = getValue(dataRows, x, inverseColumnFieldInfo,joinFieldInfo);
			if(value != null) {
				entityFieldInfo.invokeSet(x, value);
			}
		});

		
		
	}

	
	
	

}
