package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Gt;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class GtConditionAdapter implements ConditionAdapter<Gt>{

	@Override
	public Class<Gt> annotationType() {
		return Gt.class;
	}

	@Override
	public Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Gt gt = (Gt) annotation;
		String column = ObjectUtils.isEmpty(gt.value())?name:gt.value();
		Convert convert = ConvertManager.getConvert(gt.convert());
		return x->x.gt(column, convert.to(value));
	}

}
