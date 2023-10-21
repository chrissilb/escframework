package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.gwasch.code.escframework.components.utils.AsteriskType;

/**
 * Indicates that a method is called after as a placeholder method according to
 * its {@link AsteriskType}.
 * <p>
 * An asterisk method have one of the following signature templates:
 * <ul>
 * <li>Simple: {@code public void <<<methodName>>()}</li>
 * <li>Parameterized:
 * {@code public void <<methodName>>(Object, Method, Object[])}</li>
 * <li>Qualified:
 * {@code public Object <<methodName>>(Object, Method, Object[])}</li>
 * </ul>
 * INSTEAD methods ({@code AsteriskType.ALL_INSTEAD},
 * {@code AsteriskType.ELSE_INSTEAD} must have a qualified signature. Methods
 * with another {@code AsteriskType} must have a simple or parameterized
 * signature because they are call "in addition".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Asterisk {
	
	/**
	 * Returns the asterisk type.
	 * @return the asterisk type
	 */
	AsteriskType type() default AsteriskType.AFTER_ELSE;
}
