package com.github.nkinsp.clover.query;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

public interface ConditionAdapter<A extends Annotation> {

	
	Class<A> annotationType();
	
	
    Consumer<Condition<?>> adapter(Annotation annotation,String name,Object value);
	
	
}
