package com.github.nkinsp.clover.convert.impl;

import java.util.Date;

import com.github.nkinsp.clover.convert.Convert;

public class LongToDateConvert implements Convert{

	@Override
	public Object to(Object value) {

		if(value == null) {
			return null;
		}
		
		return new Date((Long)value);
	}

	

}
