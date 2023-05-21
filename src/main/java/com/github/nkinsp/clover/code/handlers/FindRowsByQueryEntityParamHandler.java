package com.github.nkinsp.clover.code.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.Query;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.EntityMapperUtils;
import com.github.nkinsp.clover.util.ObjectUtils;

public class FindRowsByQueryEntityParamHandler<E,R> implements ExecuteHandler<Rows<R>> {

	private Class<R> entityClass;
	
	private TableInfo tableInfo;
	
	private Query<E> query;
	
	@Override
	public Rows<R> handle(DbContext context) {

		QueryWrapper<?> wrapper = new QueryWrapper<>(tableInfo);

		List<ConditionAdapter<?>> adapters = context.getConditionAdapters();

		EntityMapper mapper = EntityMapperUtils.getEntityMapper(query.getClass());

		List<EntityFieldInfo> fieldInfos = mapper.getEntityFieldInfos();

		for (EntityFieldInfo fieldInfo : fieldInfos) {

			Field field = fieldInfo.getField();
			String fieldName = fieldInfo.getFieldName();
			Object value = fieldInfo.invokeGet(query);

			if (!ObjectUtils.isEmpty(value)) {

				for (ConditionAdapter<?> adapter : adapters) {

					Annotation annotation = field.getAnnotation(adapter.annotationType());
					if (annotation != null) {

						Consumer<Condition<?>> consumer = adapter.adapter(annotation, fieldName, value);
						if (!wrapper.getConditions().isEmpty()) {
							wrapper.and();
						}
						consumer.accept(wrapper);
					}
				}
			}
		}

		List<R> results = new FindEntityRowMapperHandler<>(entityClass, wrapper).handle(context);

		return Rows.of(results);
	}

	public FindRowsByQueryEntityParamHandler(Class<R> entityClass, TableInfo tableInfo, Query<E> query) {
		super();
		this.entityClass = entityClass;
		this.tableInfo = tableInfo;
		this.query = query;
	}

	@SuppressWarnings("unchecked")
	public FindRowsByQueryEntityParamHandler(TableInfo tableInfo, Query<E> query) {
		super();
		this.tableInfo = tableInfo;
		this.query = query;
		this.entityClass = (Class<R>) tableInfo.getEntityClass();
	}

	
	
	
}
