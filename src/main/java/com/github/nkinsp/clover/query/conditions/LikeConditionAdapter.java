package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Like;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class LikeConditionAdapter implements ConditionAdapter<Like>{

	@Override
	public Class<Like> annotationType() {
		return Like.class;
	}

	@Override
	public  Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Like like = (Like) annotation;
		
		String column = ObjectUtils.isEmpty(like.value())?name:like.value();

		if(like.ignoreCase()){
			return x->x.excerpt("UPPER("+column+") LIKE UPPER(?)", (like.prefix()?"%":"")+value+(like.suffix()?"%":""));
		}

		return x->x.like(column, (like.prefix()?"%":"")+value+(like.suffix()?"%":""));
	}

	

}
