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

   private TableInfo tableInfo;

	
	
	@SuppressWarnings("unchecked")
	@Override
	public T handle(DbContext context) {
		
		if (tableInfo.isCache()) {
			CacheManager manager = context.getCacheManager();
			if (manager != null) {
				return (T) manager.getAndSet(tableInfo.getEntityClass(), id,()->super.handle(context));
			}
		}
		setEntityClass((Class<T>) tableInfo.getEntityClass());
		setQueryWrapper(new QueryWrapper<T>(tableInfo).where().eq(tableInfo.getPrimaryKeyName(), id));
		
		return super.handle(context);
	}




	public FindByIdHandler(TableInfo tableInfo, Object id) {
		this.id = id;
		this.tableInfo = tableInfo;
		
	}





	
	
	

}
