package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class as an {@code Extension}. An extension extends the interface
 * of a service or another extension. It can override methods of the extended component.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

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
	 * Defines the interface type of the component which shall be extended.
	 * 
	 * @return the interface type of the component which shall be extended
	 */
	Class<?> extendz();

	/**
	 * Defines the interface type of an extension which extends the same component
	 * but shall be considered before this extension.
	 * 
	 * @return the preceding extension interface type
	 */
	Class<?> before() default void.class;

	/**
	 * Defines the interface type of an extension which extends the same component
	 * but shall be considered after this extension.
	 * 
	 * @return the subsequent extension interface type
	 */
	Class<?> after() default void.class;

	/**
	 * If a client is defined the extension can reference it via a field annotated
	 * by {@link Client} if it is the caller.
	 * 
	 * @return the client interface type
	 */
	Class<?> client() default void.class;

	/**
	 * If {@code client} is defined and set to false the extension is only
	 * considered if the client is the caller. If it is set to true the extension is
	 * considered in any case but a {@link Client} field is null if the client is
	 * not the caller.
	 * 
	 * @return if null clients are allowed, i.e. other clients then defined by
	 *         {@code client}.
	 */
	boolean allowNullClient() default false;

//	boolean autoDelegate() default false;
}
