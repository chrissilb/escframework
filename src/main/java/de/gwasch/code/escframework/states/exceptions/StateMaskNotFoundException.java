package de.gwasch.code.escframework.states.exceptions;

public class StateMaskNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public StateMaskNotFoundException(Object state) {
		super("No adapted state found for '" + state + "'!");
	}
}
