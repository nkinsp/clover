package com.github.nkinsp.clover.code.handlers;

import java.util.List;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UpdateHandler implements ExecuteHandler<Integer>{

	
	
	private UpdateWrapper updateWrapper;
	
	@Override
	public Integer handle(DbContext context) {
		
		
		String sql = updateWrapper.buildSql();
		List<Object> params = updateWrapper.getParams();
		if(context.isSqlLog()) {
			log.info("===> execute [sql={}  params={}]", sql,params);			
		}
		return context.update(sql, params.toArray());
	}



	public UpdateHandler( UpdateWrapper updateWrapper) {
		super();
		this.updateWrapper = updateWrapper;
	}



	public UpdateHandler(TableInfo tableInfo) {
		 this.updateWrapper = new UpdateWrapper(tableInfo);
	}

	
}
