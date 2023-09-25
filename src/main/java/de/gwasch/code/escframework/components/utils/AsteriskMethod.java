package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Method;

public class AsteriskMethod extends MetaMethod {

	private final AsteriskType asteriskType;
	
	public AsteriskMethod(Method method, AsteriskType asteriskType) {
		super(method);
		
		this.asteriskType = asteriskType;
	}

	public AsteriskType getAsteriskType() {
		return asteriskType;
	}
}
