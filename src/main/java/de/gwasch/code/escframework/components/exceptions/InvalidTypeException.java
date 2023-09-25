package de.gwasch.code.escframework.components.exceptions;


public class InvalidTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidTypeException(String typeName) {
		this(typeName, null);
	}
	
	public InvalidTypeException(String typeName, Throwable cause) {
		super("Type '" + typeName + "' invalid.", cause);
	}
	
	public InvalidTypeException(Class<?> type) {
		this(type.getName(), null);
	}
	
	public InvalidTypeException(Class<?> type, Throwable cause) {
		this(type.getName(), cause);
	}
}