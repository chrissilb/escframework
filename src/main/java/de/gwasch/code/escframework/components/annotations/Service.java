package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.gwasch.code.escframework.components.utils.InstanceAllocator;

/**
 * Indicates a class as a {@code Service}. Service can be instantiated via
 * {@code InstanceAllocator.create()}.
 * 
 * @see InstanceAllocator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

	/**
	 * Defines the interface type.
	 * 
	 * @return the interface type
	 */
	Class<?> type();

	/**
	 * Defines the base interface type. It must be the interface type of a
	 * {@code Service}. The base component can be addressed via a field annotated by
	 * {@link Base}.
	 * 
	 * @return the base interface type.
	 */
	Class<?> inherits() default void.class;

	/**
	 * Defines if the {@code Service} is instantiable. If yes it can only be use as
	 * a base class. The class itself must not be defined as {@code abstract}
	 * because it still must be instantiated by the framework independently.
	 * 
	 * @return {@code true} if the service is instantiable
	 */
	boolean instantiable() default true;

//	boolean incomplete() default false;
}
