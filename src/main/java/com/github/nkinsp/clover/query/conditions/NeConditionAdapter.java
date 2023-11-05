package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Ne;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class NeConditionAdapter implements ConditionAdapter<Ne>{

	@Override
	public Class<Ne> annotationType() {
		return Ne.class;
	}

	@Override
	public  Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Ne an = (Ne) annotation;
		String column = ObjectUtils.isEmpty(an.value())?name:an.value();
		Convert convert = ConvertManager.getConvert(an.convert());
		return x->x.ne(column, convert.to(value));
	}

	

}
