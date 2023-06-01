package com.github.nkinsp.clover.code.handlers;


import java.util.Arrays;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.TableInfo;


public class DeleteByIdsHandler<T> extends DeleteHandler<T>{

	private Object[] ids;
	
	private TableInfo<T> tableInfo;
	
	public DeleteByIdsHandler(TableInfo<T> tableInfo,Object[] ids) {
		super(tableInfo);
		this.ids = ids;
		this.tableInfo = tableInfo;
	}
	
	@Override
	public Integer handle(DbContext context) {
				
		try {
			//逻辑删除
			if (tableInfo.isLogicDelete()) {
				UpdateWrapper<T> updateWrapper = new UpdateWrapper<>(tableInfo)
						.set(tableInfo.getLogicDeleteColumn(), 1)
						.where()
						.in(tableInfo.getPrimaryKeyName(), ids)
				;
				return new UpdateHandler<T>(updateWrapper).handle(context);
			}
			getDeleteWrapper().where().in(tableInfo.getPrimaryKeyName(), ids);
			return super.handle(context);
		} finally {
			if(tableInfo.isCache()) {
				//删除缓存
				CacheManager manager = context.getCacheManager();
				if(manager != null) {
					manager.delete(tableInfo.getEntityClass(), Arrays.asList(ids));
				}
			}
		}
	
	}

}
