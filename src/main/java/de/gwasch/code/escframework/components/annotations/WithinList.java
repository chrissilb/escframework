package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables <a href="https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html">
 * Repeating Annotations</a> of {@link Within}.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface WithinList {
	
	/**
	 * Returns an array of single {@code Within} annotations.
	 * @return array of single {@code Within} annotations
	 */
	Within[] value();
}
