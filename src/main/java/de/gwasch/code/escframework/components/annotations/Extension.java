package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface Extension {
	Class<?> type();
	Class<?> inherits() default void.class;
	Class<?> client() default void.class;
	Class<?> extendz();
	Class<?> before() default void.class;
	Class<?> after() default void.class;
//	boolean autoDelegate() default false;
	boolean allowNullClient() default false;
}
