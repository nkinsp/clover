package com.github.nkinsp.clover.query;

import com.github.nkinsp.clover.annotation.SelectMapperColumn;
import com.github.nkinsp.clover.table.EntityFieldInfo;

public interface SelectColumnMapperRender<En> {

	 void render(SelectMapperColumn column,EntityFieldInfo fieldInfo,QueryWrapper<En> queryWrapper);
}
