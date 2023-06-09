package com.github.nkinsp.clover.code.handlers;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;

/**
 * 通过id查询 *
 * @param <R>
 */
public class FindByIdsHandler<T,Id>  implements ExecuteHandler<Rows<T>> {

	   
   private Collection<Id> ids;


   private TableInfo<T> tableInfo;
	
   
	public List<T> findRowsByIds(DbContext context, Object[] ids) {

		FindEntityRowMapperHandler<T, T> handler = new FindEntityRowMapperHandler<>(tableInfo.getEntityClass(),
				new QueryWrapper<T>(tableInfo).where().in(tableInfo.getPrimaryKeyName(), ids));

		return handler.handle(context);


	}
	



	public FindByIdsHandler(TableInfo<T> tableInfo, Collection<Id> ids) {
		
		
		this.tableInfo = tableInfo;
		this.ids = ids;
		
		
	}



	@Override
	public Rows<T> handle(DbContext context) {
		
		
		if(CollectionUtils.isEmpty(ids)) {
			return Rows.of(new ArrayList<>());
		}
		
		
		if(tableInfo.isCache()) {
			
			CacheManager manager = context.getCacheManager();
			if(manager != null) {
				Class<T> entityClass = tableInfo.getEntityClass();
				List<T> list = manager.multiGetAndSet(
						entityClass,
						ids, 
						tableInfo.getPrimaryKeyName(),
						nowCacheKeys->findRowsByIds(context, nowCacheKeys.toArray())
				);
				return Rows.of(list);
				
			}
			
			
		}
		
		return Rows.of( findRowsByIds(context, this.ids.toArray()));
	}





	
	
	

}
