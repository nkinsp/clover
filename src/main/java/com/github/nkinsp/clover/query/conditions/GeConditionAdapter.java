package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Ge;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class GeConditionAdapter implements ConditionAdapter<Ge>{

	@Override
	public Class<Ge> annotationType() {
		return Ge.class;
	}

	@Override
	public Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Ge ge = (Ge) annotation;
		String column = ObjectUtils.isEmpty(ge.value())?name:ge.value();
		Convert convert = ConvertManager.getConvert(ge.convert());
		return x->x.ge(column, convert.to(value));
	}

}
