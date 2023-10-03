package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.gwasch.code.escframework.components.utils.CodeGenerator;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;

/**
 * Indicates that an annotation is a {@code Rule} of the {@link PatternMatcher}. 
 * <p>
 * If this is the case the {@link CodeGenerator} generates interface methods for control events.
 * 
 * @see PatternMatcher
 * @see CodeGenerator
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.ANNOTATION_TYPE) 
public @interface Rule {
}
