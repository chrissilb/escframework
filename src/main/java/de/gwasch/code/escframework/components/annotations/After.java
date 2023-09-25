package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is called after a certain time span.
 * 
 * @see Rule
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
@Repeatable(AfterList.class)
@Rule
public @interface After {
	int interval(); 
	String methodName(); 
}
