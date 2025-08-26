package de.gwasch.code.escframework.components.exceptions;

import de.gwasch.code.escframework.components.annotations.Rule;

public class InvalidRuleAnnotationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
//	public InvalidRuleAnnotationException(String typeName) {
//		this(typeName, null);
//	}
	
//	public InvalidRuleAnnotationException(String typeName, Throwable cause) {
//		super("Invalid Rule annotation '" + typeName + "'. Rule annotations must be annotated by '" + Rule.class.getName() + "'.", cause);
//	}
	
	public InvalidRuleAnnotationException(Class<?> type) {
		super("Invalid Rule annotation '" + type.getName() + "'. Rule annotations must be annotated by '" + Rule.class.getName() + "'.");
	}
	
//	public InvalidRuleAnnotationException(Class<?> type, Throwable cause) {
//		this(type.getName(), cause);
//	}
}