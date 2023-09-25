package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.gwasch.code.escframework.components.utils.AsteriskType;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface Asterisk {
	AsteriskType type() default AsteriskType.AFTER_ELSE; 
}
