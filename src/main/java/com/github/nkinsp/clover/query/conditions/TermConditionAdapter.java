package com.github.nkinsp.clover.query.conditions;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.github.nkinsp.clover.annotation.mapper.Term;
import com.github.nkinsp.clover.convert.Convert;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;

public class TermConditionAdapter implements ConditionAdapter<Term>{

	@Override
	public Class<Term> annotationType() {
		return Term.class;
	}

	@Override
	public  Consumer<Condition<?>> adapter(Annotation annotation, String name, Object value) {
		
		Term term = (Term) annotation;
		
		Convert convert = ConvertManager.getConvert(term.convert());
		return x->x.excerpt(term.value(), convert.to(value));
	}

	

}
