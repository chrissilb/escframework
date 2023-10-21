package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is called after a certain time span.
 * 
 * @see de.gwasch.code.escframework.events.patterns.Rule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(AfterList.class)
@Rule
public @interface After {

	/**
	 * Returns the time interval to delay the method invocation.
	 * 
	 * @return the time interval in milliseconds
	 */
	int interval();

	/**
	 * Returns the name of the method which is invoked after the time interval
	 * elapsed. The referenced method must have to following signature:
	 * {@code public void <<methodName>>()}.
	 * 
	 * @return the method name
	 */
	String methodName();
}
