package com.github.nkinsp.clover.code;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.handlers.ExecuteHandler;
import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.enums.PrimaryKeyType;
import com.github.nkinsp.clover.query.BeanRowMapper;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.conditions.EqConditionAdapter;
import com.github.nkinsp.clover.query.conditions.GeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.GtConditionAdapter;
import com.github.nkinsp.clover.query.conditions.InConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LikeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LtConditionAdapter;
import com.github.nkinsp.clover.query.conditions.NeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.TermConditionAdapter;
import com.github.nkinsp.clover.table.CascadeInfo;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.EntityMapperUtils;


import lombok.Getter;
import lombok.Setter;


/**
 * 
 * @author yue
 *
 */
public class DbContext extends JdbcTemplate{

	

	private static  ConcurrentHashMap<Class<?>, TableInfo> tableInfoMap = new ConcurrentHashMap<Class<?>, TableInfo>();
	
	
	@Setter
	@Getter
	private boolean sqlLog = true;
	
	@Getter
	private CacheManager cacheManager;
	
	@Setter
	private KeyGenerator keyGenerator;
	
	@Getter
	private DbType dbType;
	
	@Getter
	private List<ConditionAdapter<? extends Annotation>> conditionAdapters = new ArrayList<>();
	
	
	public DbContext(DataSource dataSource) {
		super(dataSource);
		this.dbType = this.initDbType();
		this.initConditionAdapter();
		
	}
	
	
	public DbContext(DataSource dataSource,CacheManager cacheManager) {
		this(dataSource);
		this.cacheManager = cacheManager;
	}
	
	public  static TableInfo getTableInfo(Class<?> tableClass) {
		
		return  tableInfoMap.computeIfAbsent(tableClass, entityClass->new TableInfo(tableClass));
	}

	

	public <R> R executeHandler(ExecuteHandler<R> hander) {
		
		return hander.handle(this);
		
	}
	

	protected Object primaryKeyGenerator(TableInfo tableInfo) {
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.AUTO_INCREMENT) {
			return null;
		}
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.UUID) {
			return UUID.randomUUID().toString().replace("-", "");
		}
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.INPUT) {
			
			if(keyGenerator == null) {
				
				
			}
			
			
		}
		
		
		
		
		
		return null;
	}

	
	
	/***
	 * 及联操作查询
	 * @param <R>
	 * @param <E>
	 * @param sql
	 * @param params
	 * @param entityClass
	 * @return
	 */
	public <R> List<R> opsCascadeQuery(String sql, Object[] params, Class<R> entityClass) {
		
		
		EntityMapper entityMapper = EntityMapperUtils.getEntityMapper(entityClass);
		List<EntityFieldInfo> cascadeFields = entityMapper.getEntityFieldInfos().stream().filter(x->x.isCascade()).collect(Collectors.toList());
		List<R> result = query(sql, params, new BeanRowMapper<R>(entityClass));
		for (EntityFieldInfo entityFieldInfo : cascadeFields) {
			CascadeInfo cascadeInfo = entityFieldInfo.getCascadeInfo();
			
//			if(cascadeFields)
			
			
			
			
		}
		
	
		
		for (R r : result) {
			
		}
//		
//		result.stream().map(r->{
//			
//			Map<String, Object> map = new HashMap<>(2);
////			map.put(sql, result)
//			
//		});
		
		
		
		
		
		
		return null;
	}

	
	private DbType initDbType() {
		
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			DatabaseMetaData metaData =connection.getMetaData();
			String url = metaData.getURL().toLowerCase();
			Map<String, DbType> dbTypeMap = new HashMap<>();
			dbTypeMap.put("jdbc:mysql:",DbType.MYSQL );
			dbTypeMap.put("jdbc:sqlite:", DbType.SQLITE);
			dbTypeMap.put("jdbc:oracle:",DbType.ORACLE);
			dbTypeMap.put("jdbc:postgresql:",DbType.POSTGRESQL);
			dbTypeMap.put("jdbc:db2:",DbType.DB2);
			dbTypeMap.put("jdbc:sqlserver:",DbType.SQLSERVER);
			dbTypeMap.put("jdbc:h2:", DbType.H2);
			for (Entry<String,DbType> en : dbTypeMap.entrySet()) {
				if(url.startsWith(en.getKey())) {
					return en.getValue();
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void initConditionAdapter() {
		
		addConditionAdapter(new EqConditionAdapter());
		addConditionAdapter(new GeConditionAdapter());
		addConditionAdapter(new GtConditionAdapter());
		addConditionAdapter(new InConditionAdapter());
		addConditionAdapter(new LeConditionAdapter());
		addConditionAdapter(new LikeConditionAdapter());
		addConditionAdapter(new LtConditionAdapter());
		addConditionAdapter(new NeConditionAdapter());
		addConditionAdapter(new TermConditionAdapter());
		
	}

	public void addConditionAdapter(ConditionAdapter<? extends Annotation> adapter) {
		this.conditionAdapters.add(adapter);
	}
	
	
	
	
}
