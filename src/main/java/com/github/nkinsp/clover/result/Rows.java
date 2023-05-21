package com.github.nkinsp.clover.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.nkinsp.clover.util.ObjectUtils;


/**
 * 
 * 返回数据
 * @param <E>
 */
public class Rows<E> extends ArrayList<E> {


	private static final long serialVersionUID = 740739498556760350L;
	
	public List<E> toList(){
		return this;
	}
	
   public static <E>  Rows<E> of(Collection<E> collections){
	   return new Rows<>(collections);
   }
   
   public Rows() {
	   
   }
   
   public Rows(Collection<E> collections){
	   
	   super(collections);
   }
   

	public <U extends Comparable<U>> Rows<E> sorted(Function<E,U> sorted){
		
		return sortedWith(Comparator.comparing(sorted));
	}
	
	public <U  extends Comparable<U>> Rows<E> sortedDesc(Function<E, U> sorted){
		
		return sortedWith(Comparator.comparing(sorted).reversed());
	}
	
	public Rows<E> sortedWith(Comparator<E> comparator){
		this.sort(comparator);
		return this;
	}
	
	public <R> Rows<R> map(Function<E, R> map){
		
		return stream(x->x.map(map));
	}
	
	/**
	 * 转换实体
	 * @param <R>
	 * @param entityClass
	 * @return
	 */
	public <R> Rows<R> mapToEntity(Class<R> entityClass){
		
		return map(x->ObjectUtils.copy(entityClass, x));
		
	}
	
	public <R> Rows<R> stream(Function<Stream<E>,Stream<R>> streamMapper){
		
		return of(streamMapper.apply(stream()).collect(Collectors.toList()));
			
		
	}
	
	
	public Rows<E> distinct(){
		return  stream(x->x.distinct());
	}
	
	
	/**
	 * 赛选
	 * @param predicate
	 * @return
	 */
	public Rows<E> filter(Predicate<E> predicate){
		return  stream(x->x.filter(predicate));
	}
	
	public <A,R>  R collect(Collector<E, A, R> collector) {
		return stream().collect(collector);
	}
	
	/**
	 * 转化Map
	 * @param <K>
	 * @param <V>
	 * @param keyMapper
	 * @param valueMappter
	 * @return
	 */
	public <K,V> Map<K, V> toMap(Function<E, K> keyMapper,Function<E, V> valueMappter){
		
		return collect(Collectors.toMap(keyMapper, valueMappter));
		
	} 
	
	/**
	 * 分组
	 * @param <K>
	 * @param keyMapper
	 * @return
	 */
	public <K> Map<K, Rows<E>> groupBy(Function<E, K> keyMapper){
		
		Map<K, Rows<E>> data = new HashMap<>();
		collect(Collectors.groupingBy(keyMapper,Collectors.toList())).forEach((k,v)->data.put(k, of(v)));
		return data;
		
	}
	
	/**
	 * 分组排序
	 * @param <K>
	 * @param <V>
	 * @param keyMapper
	 * @param valueMapper
	 * @return
	 */
	public <K,V> Map<K, Rows<V>> groupBy(Function<E, K> keyMapper,Function<E, V> valueMapper){
		
		Map<K, Rows<V>> data = new HashMap<>();
		collect(Collectors.groupingBy(keyMapper,Collectors.toList())).forEach((k,v)->data.put(k, of(v).map(valueMapper)));
		return data;
		
	}
	
	/**
	 * 转换成Map
	 * @param <K>
	 * @param keyMapper
	 * @return
	 */
	public <K> Map<K, E> toMap(Function<E, K> keyMapper){
		
		return toMap(keyMapper, v->v);	
	} 
}
