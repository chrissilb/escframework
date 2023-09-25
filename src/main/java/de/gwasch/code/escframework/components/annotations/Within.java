package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Invokes {@link #methodName()} if the annotated method is on the Stack for a specified time interval.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
@Repeatable(WithinList.class)
@Rule
public @interface Within {
	int interval(); 
	String methodName(); 
}
