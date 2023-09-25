package de.gwasch.code.escframework.states.exceptions;

public class LocalStateNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LocalStateNotFoundException() {
		super("LocalState has not defined, yet!");
	}
}
