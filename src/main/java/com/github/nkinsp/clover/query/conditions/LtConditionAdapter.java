package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Lt;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.util.ObjectUtils;

public class LtConditionAdapter implements ConditionAdapter<Lt>{

	@Override
	public Class<Lt> annotationType() {
		return Lt.class;
	}

	@Override
	public Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Lt lt = (Lt) annotation;
		
		String column = ObjectUtils.isEmpty(lt.value())?name:lt.value();
		Convert convert = ConvertManager.getConvert(lt.convert());
		return x->x.lt(column, convert.to(value));
	}

}
