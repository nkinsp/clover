package com.github.nkinsp.clover.code;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.handlers.DeleteByIdHandler;
import com.github.nkinsp.clover.code.handlers.DeleteByIdsHandler;
import com.github.nkinsp.clover.code.handlers.DeleteHandler;
import com.github.nkinsp.clover.code.handlers.FindByIdHandler;
import com.github.nkinsp.clover.code.handlers.FindByQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindRowsByQueryEntityParamHandler;
import com.github.nkinsp.clover.code.handlers.InsertBatchHandler;
import com.github.nkinsp.clover.code.handlers.InsertHandler;
import com.github.nkinsp.clover.code.handlers.UpdateEntityHandler;
import com.github.nkinsp.clover.code.handlers.UpdateHandler;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.query.PagingQuery;
import com.github.nkinsp.clover.query.Query;
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
public interface BaseRepository<Id,En> {

	
	/**
	 * 获取dbContext
	 * @return
	 */
	DbContext dbContext();
	
	/**
	 * 获取table info
	 * @return
	 */
	TableInfo tableInfo();
	
	/**
	 * 保存一条数据
	 * @param entity
	 * @return
	 */
	 default Id save(En entity) {
		 return dbContext().executeHandler(new InsertHandler<Id, En>(entity));
	 }
	
	
	/**
	 * 批量保存数据
	 * @param entitys
	 */
	default void saveBatch(Collection<En> entitys) {
		dbContext().executeHandler(new InsertBatchHandler<En>(entitys));
	}
	
	
	/**
	 * 删除一条记录
	 * @param id
	 * @return
	 */
	default int delete(Id id) {
		return dbContext().executeHandler(new DeleteByIdHandler<Id>(tableInfo(), id));
	}
	
	/**
	 * 通过id 批量删除
	 * @param ids
	 * @return
	 */
	default int deleteByIds(Collection<Id> ids) {
		
		if(CollectionUtils.isEmpty(ids)) {
			return 0;
		}
		return dbContext().executeHandler(new DeleteByIdsHandler(tableInfo(), ids.toArray()));
	}
	
	/**
	 * 条件删除
	 * @param consumer
	 * @return
	 */
	default int deleteBy(Consumer<DeleteWrapper> consumer) {
		
		DeleteWrapper deleteWrapper = new DeleteWrapper(tableInfo());
		consumer.accept(deleteWrapper);
		return dbContext().executeHandler(new DeleteHandler(deleteWrapper));
	}
	
	
	/**
	 *  根据id 修改
	 * @param entity
	 * @return
	 */
	default int update(En entity) {
		return dbContext().executeHandler(new UpdateEntityHandler<En>(tableInfo(), entity));
	}
	
	
	/**
	 * 条件更新
	 * @param consumer
	 * @return
	 */
	default int updateBy(Consumer<Condition<UpdateWrapper>> consumer) {
		
		UpdateWrapper wrapper = new UpdateWrapper(tableInfo());
		consumer.accept(wrapper);

		return dbContext().executeHandler(new UpdateHandler(wrapper));
	}
	
	
	/**
	 * 主键id 查询
	 * @param id
	 * @return
	 */
	default En find(Id id) {
		return dbContext().executeHandler(new FindByIdHandler<En>(tableInfo(), id));
	}
	
	/**
	 * 条件查询
	 * @param consumer
	 * @return
	 */
	default En findBy(Consumer<Condition<QueryWrapper<En>>> consumer) {

		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		return dbContext()
				.executeHandler(new FindByQueryHandler<En, En>(queryWrapper.getTableClass(), queryWrapper));

	}
	
	/**
	 * 条件查询
	 * @param consumer
	 * @return
	 */
	default <R> R findBy(Class<R> resultType,Consumer<Condition<QueryWrapper<En>>> consumer) {
		QueryWrapper<En> queryWrapper = new QueryWrapper<En>(tableInfo());
		return dbContext()
				.executeHandler(new FindByQueryHandler<R, En>(resultType, queryWrapper));
	}
	
	/**
	 * 通过id 批量查询
	 * @param ids
	 * @return
	 */
	default Rows<En> findByIds(Collection<Id> ids){
		return null;
	}
	
	/**
	 * 
	 * @param consumer
	 * @return
	 */
	default Rows<En> findRowsBy(Consumer<Condition<QueryWrapper<En>>> consumer){
		return null;
	}
	
	default Rows<En> findRowsOf(Map<String, Object> map){
		return null;
	}
	
	default Rows<En> findRowsOf(Query<En> query){
		return dbContext().executeHandler(new FindRowsByQueryEntityParamHandler<En, En>( tableInfo(), query));
	}
	
	default <R> Rows<R> findRowsOf(Class<R> entityClass,Query<En> query){
		return dbContext().executeHandler(new FindRowsByQueryEntityParamHandler<En, R>( entityClass,tableInfo(), query));
	}
	
	/**
	 * 
	 * @param pagingQuery
	 * @return
	 */
	default Rows<En> findRowsOf(PagingQuery<En> pagingQuery){
		return null;
	}
	
	
	
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param consumer
	 * @return
	 */
	default Page<En> findPageBy(long pageNum,long pageSize,Consumer<Condition<QueryWrapper<En>>> consumer){
		return null;
	}
	
	
	default <R> Page<En> findPageBy(long pageNum,long pageSize,Class<R> resultType,Consumer<Condition<QueryWrapper<En>>> consumer){
		return null;
	}
	
	
}
