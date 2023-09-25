package de.gwasch.code.escframework.components.exceptions;

public class InvalidMethodSignatureException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidMethodSignatureException(String expectedSignature) {
		super("Invalid method signature. Expected: " + expectedSignature + ".");
	}
}