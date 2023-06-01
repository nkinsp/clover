package com.github.nkinsp.clover.code.handlers;



import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.table.TableInfo;

/**
 * 通过id查询 *
 * @param <R>
 */
public class FindByIdHandler<T> extends FindByQueryHandler<T, T>{

	   
   private Object id;

   private TableInfo<T> tableInfo;

	
	
	@Override
	public T handle(DbContext context) {
		
		//缓存
		if (tableInfo.isCache()) {
			CacheManager manager = context.getCacheManager();
			if (manager != null) {
				 Class<T> entityClass = tableInfo.getEntityClass();
				return  manager.getAndSet(entityClass, id,()->super.handle(context));
			}
		}
		
		return super.handle(context);
	}




	public FindByIdHandler(TableInfo<T> tableInfo, Object id) {
	    super(tableInfo.getEntityClass(),new QueryWrapper<>(tableInfo).where().eq(tableInfo.getPrimaryKeyName(), id) );
		this.id = id;
		this.tableInfo = tableInfo;
		
	}





	
	
	

}
