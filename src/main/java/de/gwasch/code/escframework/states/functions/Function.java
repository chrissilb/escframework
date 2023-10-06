package de.gwasch.code.escframework.states.functions;

/**
 * A {@code Function} can be injected to a {@link FunctionState}. It provides a {@code State} value according to its
 * function algorithm. Those implementations typically use {@code State} parameters.
 *   
 * @param <T> the type of the {@code State} value
 */
public interface Function<T> {

	T getValue();

}
