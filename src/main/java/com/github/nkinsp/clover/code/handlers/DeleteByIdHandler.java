package com.github.nkinsp.clover.code.handlers;


import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.TableInfo;


public class DeleteByIdHandler<Id> extends DeleteHandler{

	private Id id;
	
	private TableInfo tableInfo;
	
	public DeleteByIdHandler(TableInfo tableInfo,Id id) {
		
		super(tableInfo);
		this.id = id;
		this.tableInfo = tableInfo;
		
	}

	
	@Override
	public Integer handle(DbContext context) {
		
		try {
			//逻辑删除
			if (tableInfo.isLogicDelete()) {
				UpdateWrapper updateWrapper = new UpdateWrapper(tableInfo).set(tableInfo.getLogicDeleteColumn(), 1)
						.where().eq(tableInfo.getPrimaryKeyName(), id);
				return new UpdateHandler(updateWrapper).handle(context);
			}
			getDeleteWrapper().where().eq(tableInfo.getPrimaryKeyName(), id);
			return super.handle(context);
		} finally {
			// 删除缓存
			if(tableInfo.isCache()) {
				//删除缓存
				CacheManager manager = context.getCacheManager();
				if(manager != null) {
					manager.delete(tableInfo.getEntityClass(), id);
				}
			}
		}
	
	}

}
