package com.github.nkinsp.clover.code.handlers;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.annotation.EntityCascadeMapper;
import com.github.nkinsp.clover.annotation.SelectMapperColumn;
import com.github.nkinsp.clover.annotation.EntitySelectMapper;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.code.cascade.CascadeAdapter;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.query.BeanRowMapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.query.SelectColumnMapperRender;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.CascadeInfo;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ClassUtils;
import com.github.nkinsp.clover.util.EntityMapperManager;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class FindEntityRowMapperHandler<E, T> implements ExecuteHandler<Rows<E>> {

	private QueryWrapper<T> wrapper;

	@Getter
	private Class<E> entityClass;
	
	@Getter
	private TableInfo<T> tableInfo;
	

	public FindEntityRowMapperHandler(Class<E> entityClass, QueryWrapper<T> wrapper) {
		super();
		this.wrapper = wrapper;
		this.entityClass = entityClass;
		this.tableInfo = wrapper.getTableInfo();
	}
	


	

	@SuppressWarnings("unchecked")
	@Override
	public Rows<E> handle(DbContext context) {
				
		//字段查询映射
		EntitySelectMapper entitySelectMapper = entityClass.getAnnotation(EntitySelectMapper.class);
		if(entitySelectMapper != null) {
			EntityMapper mapper = EntityMapperManager.getEntityMapper(entityClass);
			List<EntityFieldInfo> entityFieldInfos = mapper.getEntityFieldInfos();
			for (EntityFieldInfo entityFieldInfo : entityFieldInfos) {
				SelectMapperColumn column = entityFieldInfo.getField().getAnnotation(SelectMapperColumn.class);
				if(column != null) {
					SelectColumnMapperRender<Object> render = ClassUtils.getSingleton(column.render());
					render.render(column, entityFieldInfo, (QueryWrapper<Object>)wrapper);
				}
			}
		}
		
		String sql = wrapper.buildSql();
		List<Object> params = wrapper.getParams();
		if (context.isSqlLog()) {
			log.info("===> execute sql=[{}] params={}   ", sql, params);
		}
		List<E> results = context.query(sql,new BeanRowMapper<E>(entityClass),params.toArray());
		if (CollectionUtils.isEmpty(results)) {
			return Rows.of(results);
		}
	
		EntityCascadeMapper mapper = entityClass.getAnnotation(EntityCascadeMapper.class);
		if (mapper != null) {

			Map<JoinType, CascadeAdapter> cascadeAdapters = DbContext.getCascadeAdapters();
			tableInfo.getEntityMapper().getEntityFieldInfos().stream().filter(EntityFieldInfo::isCascade)
					.forEach(entityFieldInfo -> {
						CascadeInfo cascadeInfo = entityFieldInfo.getCascadeInfo();
						CascadeAdapter adapter = cascadeAdapters.get(cascadeInfo.getJoinType());
						if (adapter != null) {
							adapter.adapter(tableInfo, results, entityFieldInfo);
						}
					});

		}
		
		
		return Rows.of(results);
	}
	
	
	
	
	
	
	
	
	
	
	

}
