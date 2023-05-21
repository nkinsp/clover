package com.github.nkinsp.clover.code.handlers;

import java.util.List;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class DeleteHandler implements ExecuteHandler<Integer>{

	
	private DeleteWrapper deleteWrapper;
	
	
	public DeleteHandler(DeleteWrapper deleteWrapper) {
		this.deleteWrapper= deleteWrapper;
	}
	
	public DeleteHandler(TableInfo tableInfo) {
		
		this.deleteWrapper = new DeleteWrapper(tableInfo);

	}

	
	
	@Override
	public Integer handle(DbContext context) {
		String sql = deleteWrapper.buildSql();
		List<Object> params = deleteWrapper.getParams();
		if (context.isSqlLog()) {
			log.info("===> execute sql=[{}] params=[{}]   ", sql, params);
		}
		return context.update(sql, params.toArray());
	}



}
