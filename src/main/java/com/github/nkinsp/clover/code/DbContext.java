package com.github.nkinsp.clover.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.cascade.CascadeAdapter;
import com.github.nkinsp.clover.code.cascade.OneToManyCascadeAdapter;
import com.github.nkinsp.clover.code.cascade.OneToOneCascadeAdapter;
import com.github.nkinsp.clover.code.dialect.MysqlDbDialectAdapter;
import com.github.nkinsp.clover.code.handlers.ExecuteHandler;
import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.enums.PrimaryKeyType;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.EntityQuery;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.query.conditions.EqConditionAdapter;
import com.github.nkinsp.clover.query.conditions.GeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.GtConditionAdapter;
import com.github.nkinsp.clover.query.conditions.InConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LikeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.LtConditionAdapter;
import com.github.nkinsp.clover.query.conditions.NeConditionAdapter;
import com.github.nkinsp.clover.query.conditions.TermConditionAdapter;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;


/**
 * 
 */
public class DbContext extends JdbcTemplate{

	

	private static  ConcurrentHashMap<Class<?>, TableInfo<?>> tableInfoMap = new ConcurrentHashMap<Class<?>, TableInfo<?>>();
	
	
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
	private static List<ConditionAdapter<? extends Annotation>> conditionAdapters = new ArrayList<>();
	
	@Getter
	private static Map<DbType,DbDialectAdapter> dialectAdapters = new HashMap<>();
	
	@Getter
	private static Map<JoinType, CascadeAdapter> cascadeAdapters = new HashMap<>();
	
	static {
		
		initConditionAdapter();
		initDialectAdapter();
		initCascadeAdapter();
		
	}
	
	
	public DbContext(DataSource dataSource) {
		super(dataSource);
		this.dbType = this.initDbType();
		
		
	}
	
	
	public DbContext(DataSource dataSource,CacheManager cacheManager) {
		this(dataSource);
		this.cacheManager = cacheManager;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> TableInfo<T> getTableInfo(Class<T> tableClass) {

		synchronized (tableClass) {

			TableInfo<?> tableInfo = tableInfoMap.computeIfAbsent(tableClass,
					entityClass -> new TableInfo<>(tableClass));

			return (TableInfo<T>) tableInfo;
		}
	}
	

	

	public <R> R executeHandler(ExecuteHandler<R> hander) {
		
		return hander.handle(this);
		
	}
	
	
	public <Id,En> BaseRepository<Id,En> createRepository(Class<En> entityClass){
		
		TableInfo<En> tableInfo = getTableInfo(entityClass);
		
		return new BaseRepository<Id, En>() {

			@Override
			public DbContext dbContext() {				
				return DbContext.this;
			}

			@Override
			public TableInfo<En> tableInfo() {
				return tableInfo;
			}
			
		};
		
	}
	
	

	public Object primaryKeyGenerator(TableInfo<?> tableInfo) {
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.AUTO_INCREMENT) {
			return null;
		}
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.UUID) {
			return UUID.randomUUID().toString().replace("-", "");
		}
		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.INPUT) {
			
			if(keyGenerator != null) {
				
				return keyGenerator.createId(this, tableInfo);
			}
			
		}
		
		return null;
	}

	
	
	
	
	/**
	 * 添加实体参数
	 * @param <T>
	 * @param wrapper
	 * @param query
	 */
	public <T> void appendWrapperParmas(QueryWrapper<T> wrapper,EntityQuery<T> query) {
		
		List<ConditionAdapter<?>> adapters = getConditionAdapters();

		EntityMapper mapper = EntityMapperManager.getEntityMapper(query.getClass());

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
		
		//
		query.then(wrapper);
	}
	
	
	
	public static DbDialectAdapter getDialectAdapter(DbType dbType) {
		
		
		DbDialectAdapter adapter = dialectAdapters.get(dbType);
		
		return adapter;
		
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

	private static void initConditionAdapter() {
		
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
	
	private static void initDialectAdapter() {
		
		addDialectAdapter(new MysqlDbDialectAdapter());
		
	}
	
	private static void initCascadeAdapter() {
		addCascadeAdapter(new OneToOneCascadeAdapter());
		addCascadeAdapter(new OneToManyCascadeAdapter());
	}

	public static void addConditionAdapter(ConditionAdapter<? extends Annotation> adapter) {
		conditionAdapters.add(adapter);
	}
	
	public static void addDialectAdapter(DbDialectAdapter adapter) {
		dialectAdapters.put(adapter.dbType(), adapter);
	}
	
	public static void addCascadeAdapter(CascadeAdapter adapter) {
		cascadeAdapters.put(adapter.joinType(), adapter);
	}
	
}
