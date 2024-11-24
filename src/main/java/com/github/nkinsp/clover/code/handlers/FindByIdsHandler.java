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
 */
public class FindByIdsHandler<T,Id>  implements ExecuteHandler<Rows<T>> {

	   
   private final Collection<Object> ids;


   private final TableInfo<T> tableInfo;
	
   
	public List<T> findRowsByIds(DbContext context, Object[] ids) {

		FindEntityRowMapperHandler<T, T> handler = new FindEntityRowMapperHandler<>(tableInfo.getEntityClass(),
				new QueryWrapper<T>(tableInfo).where().in(tableInfo.getPrimaryKeyName(), ids));

		return handler.handle(context);


	}
	



	@SuppressWarnings("unchecked")
	public FindByIdsHandler(TableInfo<T> tableInfo, Collection<Id> ids) {
		
		
		this.tableInfo = tableInfo;
		this.ids = (Collection<Object>) ids;
		
		
	}



	@Override
	public Rows<T> handle(DbContext context) {
		
		
		if(CollectionUtils.isEmpty(ids)) {
			return Rows.of(new ArrayList<>());
		}
		
		
		if(tableInfo.isCache()) {
			
			CacheManager manager = context.getCacheManager();
			if(manager != null) {
				List<T> list = manager.multiGetAndSet(tableInfo, ids, keys->findRowsByIds(context, keys.toArray()));
				return Rows.of(list);
			}
			
			
		}
		
		return Rows.of(findRowsByIds(context, this.ids.toArray()));
	}





	
	
	

}
