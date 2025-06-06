package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Asterisk;

/**
 * A {@link MetaMethod} which is annotated by {@link Asterisk}.
 * 
 * @see InstanceAllocator
 */
public class AsteriskMethod extends MetaMethod {

	private final AsteriskType asteriskType;
	
	/**
	 * Constructs an {@code AsteriskMethod}.
	 * @param method the method instance
	 * @param asteriskType the asterisk type
	 */
	public AsteriskMethod(Method method, AsteriskType asteriskType) {
		super(method);
		
		this.asteriskType = asteriskType;
	}

	/**
	 * Returns the asterisk type.
	 * @return the asterisk type
	 */
	public AsteriskType getAsteriskType() {
		return asteriskType;
	}
}
