package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is called if the annotated method gets less {@link #invocations()}
 * within the time span {@link #interval()}.
 * 
 * @see Rule
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
@Repeatable(LessList.class)
@Rule
public @interface Less {
	int interval(); 
	int invocations();
	String methodName();
}
