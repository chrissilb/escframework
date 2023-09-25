package de.gwasch.code.escframework.components.exceptions;


public class UnknownTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnknownTypeException(Class<?> type) {
		this(type, null);
	}
	
	public UnknownTypeException(Class<?> type, Throwable cause) {
		super("Type '" + type + "' unknown.", cause);
	}
}
