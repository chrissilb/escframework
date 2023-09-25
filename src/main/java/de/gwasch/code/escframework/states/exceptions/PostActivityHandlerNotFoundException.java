package de.gwasch.code.escframework.states.exceptions;

public class PostActivityHandlerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PostActivityHandlerNotFoundException(Object newstate, Object oldstate) {
		super("No post activity handler found for transition '" 
            + oldstate + " -> " + newstate + "'!");
	}
}
