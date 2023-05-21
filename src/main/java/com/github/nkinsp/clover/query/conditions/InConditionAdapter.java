package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.In;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class InConditionAdapter implements ConditionAdapter<In>{

	@Override
	public Class<In> annotationType() {
		return In.class;
	}

	@Override
	public Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {

		
		In an = (In) annotation;
		
		String column = ObjectUtils.isEmpty(an.value())?name:an.value();
		
		if(value.getClass().isArray()) {
			return x->x.in(column, (Object[])value);
		}
		if(Collection.class.isAssignableFrom(value.getClass())) {
			Collection<?> collection = (Collection<?>) value;
			return x->x.in(column, collection.toArray());
		}
		
		return x->{};
	}

}
