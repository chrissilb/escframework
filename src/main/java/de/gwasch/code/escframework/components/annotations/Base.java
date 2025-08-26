package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a field as the instance of the base class. It refers to its first
 * extension if available or to the service instance. The base class is defined
 * via {@code inherits} in {@link Service} or {@link Extension}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Base {
}
