package de.gwasch.code.escframework.states.exceptions;

public class MaskAlreadyDefinedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MaskAlreadyDefinedException(Class<?> type) {
		super("A mask of type '" + type + "' is already defined! "
         + "A Redefinition is not possible. "
         + "Maybe the default mask was defined implicitly by using this type."); 
	}
}
