package de.gwasch.code.escframework.components.exceptions;


public class GenerationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GenerationException(String message) {
		super(message);
	}
	
	public GenerationException(Throwable cause) {
		super(cause);
	}

	public GenerationException(Throwable cause, String referencingClassName) {
		super(cause.getMessage() + " referenced by " + referencingClassName, cause);
	}
}
