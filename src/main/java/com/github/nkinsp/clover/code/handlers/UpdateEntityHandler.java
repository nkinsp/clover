package com.github.nkinsp.clover.code.handlers;



import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.TableInfo;

public class UpdateEntityHandler<T> extends UpdateHandler<T>{

	private T entity;
	
	private TableInfo<T> tableInfo ;
	
	public UpdateEntityHandler(TableInfo<T> tableInfo,T entity) {
		super(tableInfo);
		this.entity = entity;
		this.tableInfo = tableInfo;
	}

	@Override
	public Integer handle(DbContext context) {
	
		
		EntityFieldInfo idField = tableInfo.getEntityMapper().getByColumnName(tableInfo.getPrimaryKeyName());
		Object id = idField.invokeGet(entity);
		try {
			UpdateWrapper<T> wrapper = getUpdateWrapper();
			tableInfo.getEntityMapper().getEntityFieldInfos().stream()
					.filter(x -> !x.getFieldName().equals(idField.getFieldName())).forEach(field -> {
						Object value = field.invokeGet(entity);
						if (value != null) {
							wrapper.set(field.getColumnName(), value);
						}
					});
			wrapper.where().eq(tableInfo.getPrimaryKeyName(), id);
			return super.handle(context);
		} finally {
			//删除缓存
			if(tableInfo.isCache()) {
				CacheManager manager = context.getCacheManager();
				if(manager != null) {
					manager.delete(tableInfo, id);
				}
			}
		}
	}
	
	
}
