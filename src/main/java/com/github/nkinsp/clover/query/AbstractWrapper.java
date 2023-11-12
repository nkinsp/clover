package com.github.nkinsp.clover.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.util.ObjectUtils;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.Getter;

@SuppressWarnings("unchecked")
public abstract class AbstractWrapper<S extends AbstractWrapper<S>>  implements Condition<S>,ISqlGenerate{
	

	@Getter
	private List<Object> params = new ArrayList<Object>();
	
	@Getter
	private List<String> conditions = new ArrayList<String>();

	private S typeThis = (S) this;
	
	
	protected String colunmFormat(String column) {
		return column.contains("_")?column:StringUtils.camelToUnder(column);
	}
	
	@Override
	public S excerpt(String sql, Object... params) {
		conditions.add(sql);
		params(params);
		return typeThis;
	}
	
	@Override
	public S allEq(Map<String, Object> allEq) {
		
		if(allEq != null && !allEq.isEmpty()) {
			allEq.forEach((name,value)->{
				if(conditions.isEmpty()) {
					eq(name, value);
				}else {
					and().eq(name, value);
				}
				
			});
		}
		
		
		return typeThis;
	}
	
	@Override
	public S where() {
		return typeThis;
	}
	
	@Override
	public S where(String sql, Object... params) {
		return excerpt(sql, params);
	}
	

	@Override
	public S eq(String column, Object value) {
		return excerpt(SqlKeyword.EQ.format(colunmFormat(column)), value);
	}
	
	@Override
	public S andEq(String cloumn, Object value) {
		return and().eq(cloumn, value);
	}

	@Override
	public S ne(String column, Object value) {
		return excerpt(SqlKeyword.NE.format(colunmFormat(column)), value);
	}

	@Override
	public S gt(String column, Object value) {
		return excerpt(SqlKeyword.GT.format(colunmFormat(column)), value);
	}

	@Override
	public S ge(String column, Object value) {
		return excerpt(SqlKeyword.GE.format(colunmFormat(column)), value);
	}

	@Override
	public S lt(String column, Object value) {
		return excerpt(SqlKeyword.LT.format(colunmFormat(column)), value);
	}

	@Override
	public S le(String column, Object value) {
		return excerpt(SqlKeyword.LE.format(colunmFormat(column)), value);
	}
	
	@Override
	public S and() {
		return and(s->{});
	}

	@Override
	public S and(Consumer<S> consumer) {
		 excerpt(SqlKeyword.AND.value); 
		 consumer.accept(typeThis);
		 return typeThis;
	}

	@Override
	public S and(String sql, Object... params) {
		
		return and(s->s.excerpt(sql, params));
	}
	

	@Override
	public S in(String column, Object... params) {
		return excerpt(SqlKeyword.IN.format(colunmFormat(column),Stream.of(params).map(s -> "?").collect(Collectors.joining(","))),params);
	}
	
	@Override
	public <T> S in(String column, Class<T> tableClass, Consumer<QueryWrapper<T>> consumer) {
		QueryWrapper<T> wrapper = new QueryWrapper<T>(tableClass);
		consumer.accept(wrapper);
		return excerpt(SqlKeyword.IN.format(colunmFormat(column),wrapper.buildSql()), wrapper.getParams().toArray());
		
	}


	@Override
	public S or() {
		return excerpt(SqlKeyword.OR.value);
	}
	
	@Override
	public S or(Consumer<S> consumer) {
		return or().nesting(consumer);
	}
	
	@Override
	public S nesting(Consumer<S> consumer) {
		excerpt("(");
		consumer.accept(typeThis);
		excerpt(")");
		return typeThis;
	}
	
	
	
	
	@Override
	public S between(String column, Object v1, Object v2) {
		return excerpt(SqlKeyword.BETWEEN.format(colunmFormat(column)), v1,v2);
	}
	
	@Override
	public S like(String column, Object value) {
		
		return excerpt(SqlKeyword.LIKE.format(colunmFormat(column)), value);
	}

	@Override
	public S param(Object param) {
		params.add(param);
		return typeThis;
	}

	@Override
	public S params(Object... params) {
		for (Object param : params) {
			param(param);
		}
		return typeThis;
	}
	
	@Override
	public S andThen(boolean condition, Consumer<S> consumer) {
		return condition ? and(consumer) : typeThis;
	}
	
	
	@Override
	public <V> S andNotEmptyThen(V v, BiConsumer<V, S> consumer) {
		return andThen(!ObjectUtils.isEmpty(v),s->consumer.accept(v, typeThis));
	}
}
