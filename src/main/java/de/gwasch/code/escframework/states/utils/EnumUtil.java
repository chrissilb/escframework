package de.gwasch.code.escframework.states.utils;

/**
 * Utility class for enum types.
 */
public class EnumUtil {
	
	/**
	 * Increments an enum value by 1 according to its ordinal number.
	 * 
	 * @param <T> the enum type
	 * @param value the current value
	 * @return the incremented value
	 */
    @SuppressWarnings("unchecked")
	public static<T extends Enum<T>> T increment(T value) {    	
    	Class<?> cls = value.getClass();
    	return (T)cls.getEnumConstants()[value.ordinal() + 1];
    }

	/**
	 * Decrements an enum value by 1 according to its ordinal number.
	 * 
	 * @param <T> the enum type
	 * @param value the current value
	 * @return the incremented value
	 */
    @SuppressWarnings("unchecked")
	public static<T extends Enum<T>> T decrement(T value) {   	
    	Class<?> cls = value.getClass();
    	return (T)cls.getEnumConstants()[value.ordinal() - 1];
    }
}
