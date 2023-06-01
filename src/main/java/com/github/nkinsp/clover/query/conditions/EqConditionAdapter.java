package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Eq;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class EqConditionAdapter implements ConditionAdapter<Eq>{

	@Override
	public Class<Eq> annotationType() {
		return Eq.class;
	}

	@Override
	public  Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Eq eq = (Eq) annotation;
		String column = ObjectUtils.isEmpty(eq.value())?name:eq.value();
		Convert convert = ConvertManager.getConvert(eq.convert());
		return x->x.eq(column, convert.to(value));
	}

	

}
