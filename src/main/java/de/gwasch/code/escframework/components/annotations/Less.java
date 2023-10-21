package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is called if the annotated method gets less
 * {@link #invocations()} within a certain time span ({@link #interval()}).
 * 
 * @see de.gwasch.code.escframework.events.patterns.Rule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(LessList.class)
@Rule
public @interface Less {

	/**
	 * Returns the considered time interval.
	 * 
	 * @return the time interval in milliseconds
	 */
	int interval();

	/**
	 * Returns the number of considered invocations.
	 * 
	 * @return the number of considered invocations
	 */
	int invocations();

	/**
	 * Returns the name of the method which is invoked after the time interval
	 * elapsed if less method invocations occurred. The referenced method must have
	 * to following signature: {@code public void <<methodName>>()}.
	 * 
	 * @return the method name
	 */
	String methodName();
}
