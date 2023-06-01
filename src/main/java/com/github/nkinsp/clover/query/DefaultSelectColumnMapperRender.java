package com.github.nkinsp.clover.query;


import org.springframework.util.StringUtils;

import com.github.nkinsp.clover.annotation.SelectMapperColumn;
import com.github.nkinsp.clover.table.EntityFieldInfo;

public class DefaultSelectColumnMapperRender implements SelectColumnMapperRender<Object>{

	@Override
	public void render(SelectMapperColumn column,EntityFieldInfo fieldInfo, QueryWrapper<Object> queryWrapper) {
				
		String value = column.value();
		
		if(StringUtils.hasText(value)) {	
			queryWrapper.select(value+" AS "+fieldInfo.getColumnName());
			return;
		}
		
		queryWrapper.select(fieldInfo.getColumnName());
		
	}

	

}
