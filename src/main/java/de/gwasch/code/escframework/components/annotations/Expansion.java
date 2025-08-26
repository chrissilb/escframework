package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a field as an {@code Expansion}. The field type must be a
 * {@code Service} interface. This {@code Service} interface extends the
 * interface of the enclosing class.
 * <p>
 * A service method can override a method of an expansion.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Expansion {
}

//todo, Visibility als attribut