package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Enables <a href="https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html">
 * Repeating Annotation</a>of {@link More}.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface MoreList {
	
	/**
	 * Returns an array of single {@code More} annotations.
	 * @return array of single {@code More} annotations
	 */
	More[] value();
}
