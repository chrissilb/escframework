package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Method;

/**
 * A {@link MetaMethod} which is annotated by {@link Asterisk}
 * 
 * @see InstanceAllocator
 */
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
