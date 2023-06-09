package com.github.nkinsp.clover.table;


import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.nkinsp.clover.annotation.Table;
import com.github.nkinsp.clover.cache.CacheKeyGenerator;
import com.github.nkinsp.clover.code.KeyGenerator;
import com.github.nkinsp.clover.enums.PrimaryKeyType;
import com.github.nkinsp.clover.util.ClassUtils;
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TableInfo<T> {

	
	/**
	 * 实体类型
	 */
	private Class<T> entityClass;
	
	/**
	 * 对应的表名
	 */
	private String tableName;
	
	/**
	 * 主键名称
	 */
	private String primaryKeyName;
	
	private String keySequence;
	
	/**
	 * 主键类型
	 */
	private PrimaryKeyType primaryKeyType;
	
	/**
	 * 是否缓存
	 */
	private boolean cache;
	
	/**
	 * 缓存时间单位
	 */
	private TimeUnit cacheTimeUnit;
	
	/**
	 * 缓存时间
	 */
	private long cacheTime;
	
	/**
	 * 是否逻辑删除
	 */
	private boolean logicDelete;
	
	/**
	 * 逻辑删除字段名
	 */
	private String logicDeleteColumn;
	

	/**
	 * 实体字段映射
	 */
	private EntityMapper entityMapper;
	
	/**
	 * 字段列表
	 */
	private List<String> columns;
	
	/**
	 * 主键生成器
	 */
	private KeyGenerator keyGenerator;
	
	/**
	 * 缓存key生成
	 */
	private CacheKeyGenerator cacheKeyGenerator;


	public TableInfo(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
		this.initialize();
	}
	
	
	private void initialize() {
		Table table = this.entityClass.getAnnotation(Table.class);
		if(table == null) {
			throw new RuntimeException(String.format("Entity [%s] no @Table ", this.entityClass.getName()));
		}
		String tableName = StringUtils.isEmpty(table.name())?StringUtils.camelToUnder(this.entityClass.getSimpleName()):table.name();
		setTableName(tableName);
		setCache(table.cache());
		setCacheTime(table.cacheTime());
		setCacheTimeUnit(table.cacheTimeUnit());
		setPrimaryKeyName(table.primaryKeyName());
		setEntityMapper(EntityMapperManager.getEntityMapper(entityClass));
		setLogicDelete(table.logicDelete());
		setLogicDeleteColumn(table.logicDeleteColumn());
		setColumns(this.entityMapper.getColumns());
		setPrimaryKeyType(table.primaryKeyType());
		setKeyGenerator((KeyGenerator) ClassUtils.newInstance(table.primaryKeyGenerator()));
		setCacheKeyGenerator((CacheKeyGenerator) ClassUtils.newInstance(table.cacheKeyGenerator()));
		
		
	}
	
	
	
	
}
