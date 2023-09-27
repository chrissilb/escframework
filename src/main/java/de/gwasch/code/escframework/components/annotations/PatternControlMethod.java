package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method will be generated in Interface.
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
@Repeatable(PatternControlMethodList.class)
public @interface PatternControlMethod {
	String methodName(); 
}
