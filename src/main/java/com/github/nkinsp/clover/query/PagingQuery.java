package com.github.nkinsp.clover.query;

public interface PagingQuery<T> extends Query<T>{


	/**
	 * 设置分页信息
	 * @return
	 */
	void paging(PagingInfo paging);
}
