package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables cyclic invocations of the annotated method.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
@Repeatable(TickList.class)
@Rule
public @interface Tick {
	int interval() default 1000; 
	double maxDeviationFactor() default 0.0;
	int invocations() default -1;
	@Generate String activateMethod() default "";
	@Generate String deactivateMethod() default "";
	@Generate String suspendMethod() default "";
	@Generate String resumeMethod() default "";
}
