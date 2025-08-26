package de.gwasch.code.escframework.components.exceptions;

import de.gwasch.code.escframework.components.events.InvocationEvent;

/**
 * Indicates that an invocation via an {@link InvocationEvent} cannot be resolved.
 */
public class InvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
//	public InvocationException(Throwable cause) {
//		super(cause);
//	}
	
	/**
	 * Constructs an invocation exception
	 * @param message the exception message
	 */
	public InvocationException(String message) {
		super(message);
	}
}
