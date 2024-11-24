package com.github.nkinsp.clover.code.handlers;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;

import java.util.Collection;


/**
 * 通过id查询 *
 */
public class FindByIdsEntityRowHandler<E,T,Id>   implements ExecuteHandler<Rows<E>> {


	private final Class<E> entityClass;

	private final Collection<Id> ids;

	private final TableInfo<T> tableInfo;


	public FindByIdsEntityRowHandler(Class<E> entityClass,TableInfo<T> tableInfo, Collection<Id> ids) {

		this.entityClass = entityClass;
		this.ids = ids;
		this.tableInfo = tableInfo;
	}

	@Override
	public Rows<E> handle(DbContext context) {


		Rows<E> rows = new FindByIdsHandler<T, Id>(tableInfo, ids).handle(context)
				.mapTo(entityClass);

		context.executeCascadeAdapter(tableInfo,entityClass,rows);

		return rows;

	}
}
