package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Le;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class LeConditionAdapter implements ConditionAdapter<Le>{

	@Override
	public Class<Le> annotationType() {
		return Le.class;
	}

	@Override
	public Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Le le = (Le) annotation;
		
		String column = ObjectUtils.isEmpty(le.value())?name:le.value();
		Convert convert = ConvertManager.getConvert(le.convert());
		return x->x.le(column, convert.to(value));
	}

}
