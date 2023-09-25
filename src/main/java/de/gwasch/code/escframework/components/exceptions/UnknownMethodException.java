package de.gwasch.code.escframework.components.exceptions;


public class UnknownMethodException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnknownMethodException(String method) {
		this(method, null);
	}
	
	public UnknownMethodException(String methodSignature, Throwable cause) {
		super("Method '" + methodSignature + "' unknown.", cause);
	}
}
