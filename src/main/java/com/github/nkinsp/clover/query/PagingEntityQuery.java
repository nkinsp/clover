package com.github.nkinsp.clover.query;

public interface PagingEntityQuery<T> extends EntityQuery<T>{


	/**
	 * 设置分页信息
	 * @return
	 */
	default PagingInfo paging() {
		return new PagingInfo(1, 15);
	}
}
