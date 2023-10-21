package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Invokes {@link #methodName()} if the annotated method is on the Stack for a
 * specified time interval.
 * 
 * see de.gwasch.code.escframework.events.patterns.Rule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(WithinList.class)
@Rule
public @interface Within {

	/**
	 * Returns the considered time interval.
	 * 
	 * @return the time interval in milliseconds
	 */
	int interval();

	/**
	 * Returns the name of the method which is invoked if the annotated method is
	 * still on the Stack once the time interval elapsed. The referenced method must
	 * have to following signature: {@code public void <<methodName>>()}.
	 * 
	 * @return the method name
	 */
	String methodName();
}
