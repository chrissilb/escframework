package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class as a {@code Service}.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface Service {
	Class<?> type();
	Class<?> inherits() default void.class;
//	boolean incomplete() default false;
	boolean instantiable() default true;
}
