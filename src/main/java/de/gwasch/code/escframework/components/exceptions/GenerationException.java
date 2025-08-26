package de.gwasch.code.escframework.components.exceptions;

import de.gwasch.code.escframework.components.utils.CodeGenerator;

/**
 * A {@code GenerationException} can be thrown by the {@link CodeGenerator}.
 */
public class GenerationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance based on a message.
	 * @param message the message
	 */
	public GenerationException(String message) {
		super(message);
	}
	
	/**
	 * Creates an instance based on a cause.
	 * @param cause the cause throwable
	 */
	public GenerationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an instance based on a cause and causal class name.
	 * @param cause the cause throwable
	 * @param referencingClassName the causal class name
	 */
	public GenerationException(Throwable cause, String referencingClassName) {
		super(cause.getMessage() + " referenced by " + referencingClassName, cause);
	}
}
