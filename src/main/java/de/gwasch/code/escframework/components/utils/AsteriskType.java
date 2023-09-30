package de.gwasch.code.escframework.components.utils;

/**
 * {@code AsteriskType} defines in which way a asterisk method is applied.
 * 
 * @see AsteriskMethod
 * @see Skeleton
 */
public enum AsteriskType {
	
	/**
	 * The asterisk method is called before all method invocations.
	 */
	BEFORE_ALL,
	
	/**
	 * The asterisk method is called before all method invocations which are delegated.
	 */
	BEFORE_ELSE,
	
	/**
	 * The asterisk method is called instead of all method invocations.
	 */
	ALL_INSTEAD,
	
	/**
	 * The asterisk method is called instead of all method invocation which are delegated.
	 */
	ELSE_INSTEAD,
	
	/**
	 * The asterisk method is called after all method invocations.
	 */
	AFTER_ALL,  
	
	/**
	 * The asterisk method is called after all method invocations which are delegated.
	 */
	AFTER_ELSE
}
