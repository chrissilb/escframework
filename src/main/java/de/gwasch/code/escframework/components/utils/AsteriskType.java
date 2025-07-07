package de.gwasch.code.escframework.components.utils;

import de.gwasch.code.escframework.components.annotations.Asterisk;

/**
 * {@code AsteriskType} defines in which way a asterisk method is applied.
 * <p>
 * They can be applied for {@code ALL} methods or for methods which are not
 * covered by the extension, i.e. which are not overridden by the extension
 * ({@code ELSE}). The invocation of an asterisk method can occur
 * {@code BEFORE}, {@code AFTER} or {@code INSTEAD} of the actual invocation.
 * This leads to the defined asterisk types.
 * 
 * @see Asterisk
 * @see Skeleton
 */
public enum AsteriskType {

	/**
	 * The asterisk method is called before all methods.
	 */
	BEFORE_ALL,

	/**
	 * The asterisk method is called before all methods which are not overridden by
	 * the extension.
	 */
	BEFORE_ELSE,

	/**
	 * The asterisk method is called instead of all method invocations.
	 */
	INSTEAD_ALL,

	/**
	 * The asterisk method is called instead of all which are not overridden by the
	 * extension.
	 */
	INSTEAD_ELSE,

	/**
	 * The asterisk method is called after all methods.
	 */
	AFTER_ALL,

	/**
	 * The asterisk method is called after all methods which are not overridden by
	 * the extension.
	 */
	AFTER_ELSE
}
