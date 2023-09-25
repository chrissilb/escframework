package de.gwasch.code.escframework.components.exceptions;

import de.gwasch.code.escframework.components.utils.AsteriskType;

public class MultipleAsteriskInsteadException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MultipleAsteriskInsteadException(String typeName) {
		this(typeName, null);
	}
	
	public MultipleAsteriskInsteadException(String typeName, Throwable cause) {
		super("Multiple asterisk instead methods ('type' equals " + AsteriskType.ALL_INSTEAD + " or " 
				+ AsteriskType.ELSE_INSTEAD + ") not allowed in class '" + typeName + "'.", cause);
	}
	
	public MultipleAsteriskInsteadException(Class<?> type) {
		this(type.getName(), null);
	}
	
	public MultipleAsteriskInsteadException(Class<?> type, Throwable cause) {
		this(type.getName(), cause);
	}
}