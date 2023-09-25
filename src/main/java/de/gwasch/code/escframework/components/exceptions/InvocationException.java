package de.gwasch.code.escframework.components.exceptions;


public class InvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvocationException(Throwable cause) {
		super(cause);
	}
	
	public InvocationException(String message) {
		super(message);
	}
}
