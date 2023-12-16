package com.github.nkinsp.clover.code;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.handlers.DeleteByIdHandler;
import com.github.nkinsp.clover.code.handlers.DeleteByIdsHandler;
import com.github.nkinsp.clover.code.handlers.DeleteHandler;
import com.github.nkinsp.clover.code.handlers.FindByIdHandler;
import com.github.nkinsp.clover.code.handlers.FindByIdsHandler;
import com.github.nkinsp.clover.code.handlers.FindByQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindEntityRowMapperHandler;
import com.github.nkinsp.clover.code.handlers.FindForObjectByQueryEntityParamHandler;
import com.github.nkinsp.clover.code.handlers.FindForObjectHandler;
import com.github.nkinsp.clover.code.handlers.FindPagingQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindRowsByQueryEntityParamHandler;
import com.github.nkinsp.clover.code.handlers.FindRowsByQueryPagingEntityParamHandler;
import com.github.nkinsp.clover.code.handlers.InsertBatchHandler;
import com.github.nkinsp.clover.code.handlers.InsertHandler;
import com.github.nkinsp.clover.code.handlers.UpdateEntityHandler;
import com.github.nkinsp.clover.code.handlers.UpdateHandler;
import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.query.PagingEntityQuery;
import com.github.nkinsp.clover.query.PagingQueryWrapper;
import com.github.nkinsp.clover.query.EntityQuery;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.result.Page;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;

/**
 * 
 * @author yue
 *
 * @param <E>
 */
public interface BaseRepository<Id, En> {

	/**
	 * 获取dbContext
	 * 
	 * @return
	 */
	DbContext dbContext();

	/**
	 * 获取table info
	 * 
	 * @return
	 */
	TableInfo<En> tableInfo();

	/**
	 * 保存一条数据
	 * 
	 * @param entity
	 * @return
	 */
	default Id save(En entity) {
		return dbContext().executeHandler(new InsertHandler<Id, En>(tableInfo(),entity));
	}

	/**
	 * 批量保存数据
	 * 
	 * @param entitys
	 */
	default void saveBatch(Collection<En> entitys) {
		dbContext().executeHandler(new InsertBatchHandler<En>(tableInfo(),entitys));
	}

	/**
	 * 删除一条记录
	 * 
	 * @param id
	 * @return
	 */
	default int delete(Id id) {
		return dbContext().executeHandler(new DeleteByIdHandler<>(tableInfo(), id));
	}

	/**
	 * 通过id 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	default int deleteByIds(Collection<Id> ids) {

		if (CollectionUtils.isEmpty(ids)) {
			return 0;
		}
		return dbContext().executeHandler(new DeleteByIdsHandler<>(tableInfo(), ids.toArray()));
	}

	/**
	 * 条件删除
	 * 
	 * @param consumer
	 * @return
	 */
	default int deleteBy(Consumer<DeleteWrapper<En>> consumer) {

		DeleteWrapper<En> deleteWrapper = new DeleteWrapper<>(tableInfo());
		consumer.accept(deleteWrapper);
		return dbContext().executeHandler(new DeleteHandler<>(deleteWrapper));
	}

	/**
	 * 根据id 修改
	 * 
	 * @param entity
	 * @return
	 */
	default int update(En entity) {
		return dbContext().executeHandler(new UpdateEntityHandler<En>(tableInfo(), entity));
	}

	/**
	 * 条件更新
	 * 
	 * @param consumer
	 * @return
	 */
	default int updateBy(Consumer<UpdateWrapper<En>> consumer) {

		UpdateWrapper<En> wrapper = new UpdateWrapper<>(tableInfo());
		consumer.accept(wrapper);

		return dbContext().executeHandler(new UpdateHandler<En>(wrapper));
	}
	
	
	/**
	 * 主键id 查询
	 * 
	 * @param id
	 * @return
	 */
	default En find(Id id) {
		return dbContext().executeHandler(new FindByIdHandler<En>(tableInfo(), id));
	}

	/**
	 * 条件查询
	 * 
	 * @param consumer
	 * @return
	 */
	default En findBy(Consumer<Condition<QueryWrapper<En>>> consumer) {

		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		return dbContext().executeHandler(new FindByQueryHandler<>(queryWrapper.getTableClass(), queryWrapper));

	}

	/**
	 * 条件查询
	 * 
	 * @param consumer
	 * @return
	 */
	default <R> R findBy(Class<R> resultType, Consumer<Condition<QueryWrapper<En>>> consumer) {
		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		consumer.accept(queryWrapper);
		return dbContext().executeHandler(new FindByQueryHandler<R, En>(resultType, queryWrapper));
	}

	/**
	 * 通过id 批量查询
	 * 
	 * @param ids
	 * @return
	 */
	default Rows<En> findByIds(Collection<Id> ids) {
		return dbContext().executeHandler(new FindByIdsHandler<>(tableInfo(), ids));
	}

	/**
	 * 条件查询
	 * @param consumer
	 * @return
	 */
	default Rows<En> findRowsBy(Consumer<QueryWrapper<En>> consumer) {
		
		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		consumer.accept(queryWrapper);
		return dbContext().executeHandler(new FindEntityRowMapperHandler<>(queryWrapper.getTableClass(), queryWrapper));
		
	}
	
	/**
	 * 获取所有
	 * @return
	 */
	default Rows<En> findRowsAll(){
		return findRowsBy(s->{});
	}

	/**
	 * 条件查询
	 * @param <R>
	 * @param resultType
	 * @param consumer
	 * @return
	 */
	default<R> Rows<R> findRowsBy(Class<R> resultType,Consumer<QueryWrapper<En>> consumer) {
		
		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		consumer.accept(queryWrapper);
		return dbContext().executeHandler(new FindEntityRowMapperHandler<>(resultType, queryWrapper));
		
	}
	
	/**
	 * 分页查询
	 * @param <R>
	 * @param resultType
	 * @param pageNum
	 * @param pageSize
	 * @param consumer
	 * @return
	 */
	default<R> Rows<R> findRowsBy(Class<R> resultType,Integer pageNum,Integer pageSize,Consumer<QueryWrapper<En>> consumer) {
		
		PagingQueryWrapper<En> queryWrapper = new PagingQueryWrapper<En>(tableInfo(),dbContext().getDbType(),pageNum,pageSize);
		consumer.accept(queryWrapper);
		return dbContext().executeHandler(new FindEntityRowMapperHandler<>(resultType, queryWrapper));
		
	}
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param consumer
	 * @return
	 */
	default Rows<En> findRowsBy(Integer pageNum,Integer pageSize,Consumer<QueryWrapper<En>> consumer) {
		
		return findRowsBy(tableInfo().getEntityClass(), pageNum, pageSize, consumer);
		
	}
	
	/**
	 * Map参数查询
	 * @param map
	 * @return
	 */
	default Rows<En> findRowsOf(Map<String, Object> map) {
		return findRowsBy(wrapper->wrapper.allEq(map));
	}
	
	

	/**
	 * 通过实体参数查询
	 * @param query
	 * @return
	 */
	default Rows<En> findRowsOf(EntityQuery<En> query) {
		TableInfo<En> tableInfo = tableInfo();
		return dbContext().executeHandler(new FindRowsByQueryEntityParamHandler<>(tableInfo.getEntityClass(),tableInfo, query));
	}

	/**
	 * 通过实体参数查询
	 * @param <R>
	 * @param entityClass
	 * @param query
	 * @return
	 */
	default <R> Rows<R> findRowsOf(Class<R> entityClass, EntityQuery<En> query) {
		return dbContext().executeHandler(new FindRowsByQueryEntityParamHandler<En, R>(entityClass, tableInfo(), query));
	}

	/**
	 * 通过实体参数条件查询
	 * @param pagingQuery
	 * @return
	 */
	default Rows<En> findRowsOf(PagingEntityQuery<En> pagingQuery) {

		TableInfo<En> tableInfo = tableInfo();
		return dbContext().executeHandler(new FindRowsByQueryPagingEntityParamHandler<>(
				tableInfo.getEntityClass(),
				tableInfo, 
				dbContext().getDbType(), 
				pagingQuery)
		);
	}

	/**
	 * 通过实体参数条件查询
	 * @param <R>
	 * @param resultType
	 * @param pagingQuery
	 * @return
	 */
	default <R> Rows<R> findRowsOf(Class<R> resultType,PagingEntityQuery<En> pagingQuery) {

		TableInfo<En> tableInfo = tableInfo();
		return dbContext().executeHandler(new FindRowsByQueryPagingEntityParamHandler<>(
				resultType,
				tableInfo, 
				dbContext().getDbType(), 
				pagingQuery)
		);
	}
	
	
	
	
	/**
	 * 实体条件分页查询
	 * @param pagingEntityQuery
	 * @return
	 */
	default Page<En> findPageOf(PagingEntityQuery<En> pagingEntityQuery){
		
		Long countOf = findCountOf(pagingEntityQuery);
		Rows<En> rows = findRowsOf(pagingEntityQuery);
		
		return new Page<>(countOf, rows);
	}
	
	/**
	 * 分页查询
	 * @param <R>
	 * @param resultType
	 * @param pagingEntityQuery
	 * @return
	 */
	default <R> Page<R> findPageOf(Class<R> resultType,PagingEntityQuery<En> pagingEntityQuery){
		
		Long countOf = findCountOf(pagingEntityQuery);
		
		Rows<R> rows = findRowsOf(resultType, pagingEntityQuery);
		
		return new Page<>(countOf, rows);		
	}
	
	/**
	 * 条件 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param consumer
	 * @return
	 */
	default Page<En> findPageBy(Integer pageNum, Integer pageSize, Consumer<QueryWrapper<En>> consumer) {

		TableInfo<En> tableInfo = tableInfo();
		return findPageBy(pageNum, pageSize, tableInfo.getEntityClass(), consumer);

	}

	/**
	 * 分页查询
	 * @param <R>
	 * @param pageNum
	 * @param pageSize
	 * @param resultType
	 * @param consumer
	 * @return
	 */
	default <R> Page<R> findPageBy(Integer pageNum, Integer pageSize, Class<R> resultType,
			Consumer<QueryWrapper<En>> consumer) {
		PagingQueryWrapper<En> wrapper = new PagingQueryWrapper<>(tableInfo(), dbContext().getDbType(), pageNum,
				pageSize);
		consumer.accept(wrapper);

		Long countBy = findCountBy(consumer);

		Rows<R> rows = dbContext().executeHandler(new FindPagingQueryHandler<>(resultType, wrapper));

		return new Page<>(countBy, rows);
	}
	
	/**
	 * 根据实体参数查询
	 * 
	 * @param entityQuery
	 * @return
	 */
	default Long findCountOf(EntityQuery<En> entityQuery) {

		QueryWrapper<En> wrapper = new QueryWrapper<>(tableInfo());

		FindForObjectByQueryEntityParamHandler<Long, En> handler = new FindForObjectByQueryEntityParamHandler<>(
				Long.class, entityQuery, wrapper, SqlKeyword.COUNT.format("1"));

		return dbContext().executeHandler(handler);

	}
	
	
	
	
	/**
	 * 获取内容条数
	 * @param consumer
	 * @return
	 */
	default Long findCountBy(Consumer<QueryWrapper<En>> consumer) {
		QueryWrapper<En> wrapper = new QueryWrapper<>(tableInfo());
		wrapper.select(SqlKeyword.COUNT.format("1"));
		consumer.accept(wrapper);
		return dbContext().executeHandler(new FindForObjectHandler<>(Long.class, wrapper));
	}

}
