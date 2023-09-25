package de.gwasch.code.escframework.states.exceptions;

public class UnknownStateChangeEventException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnknownStateChangeEventException() {
		super("Unkown StateChangeEvent installed!");
	}
}
