package de.gwasch.code.escframework.states.states;

/**
 * A {@code Function} can be injected to a {@link FunctionState}. It provides a
 * {@code State} value according to its function algorithm. Those
 * implementations typically use {@code State} parameters.
 * 
 * @param <T> the type of the {@code State} value
 */
public interface Function<T> {

	/**
	 * Returns the function result.
	 * @return the function result
	 */
	T getValue();

}
