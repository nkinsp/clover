package com.github.nkinsp.clover.query;

public interface EntityQuery<En> {

	
	/**
	 * 查询条件
	 * @param wrapper
	 */
	void then(QueryWrapper<En> wrapper);
	
	/**
	 * 排序
	 * @return
	 */
	default String orderBy() {
		return "";
	}
	
}
