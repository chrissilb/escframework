package de.gwasch.code.escframework.states.exceptions;

public class UnsupportedTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnsupportedTypeException(Class<?> cls) {
		super("Type '" + cls.getName() + "' is not supported.");
	}
}
