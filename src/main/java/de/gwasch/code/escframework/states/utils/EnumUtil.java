package de.gwasch.code.escframework.states.utils;

/**
 * Utility class for enum types.
 */
public class EnumUtil {
	
	private EnumUtil() {
	}
	
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
    
    /**
     * Return the greater enum value based on its ordinal number.
     * @param <T> the enum type
     * @param value1 the first enum value
     * @param value2 the second enum value
     * @return the greater enum value
     */
    public static <T extends Enum<T>> boolean gt(T value1, T value2) {
		return value1.compareTo(value2) > 0;
	}

    /**
     * the lower enum value based on its ordinal number.
     * @param <T> the enum type
     * @param value1 the first enum value
     * @param value2 the second enum value
     * @return the lower enum value
     */
    public static <T extends Enum<T>> boolean lt(T value1, T value2) {
		return value1.compareTo(value2) < 0;
	}

}
