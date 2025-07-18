package de.gwasch.code.escframework.states.utils;

import de.gwasch.code.escframework.states.exceptions.UnsupportedTypeException;

public class TypeUtil {

	@SuppressWarnings("unchecked")
	public static <T> T getDefaultValue(Class<T> cls) {

		T defaultValue;

		if (cls.isEnum()) {
			defaultValue = cls.getEnumConstants()[0];
		} else if (cls.equals(Boolean.class)) {
			defaultValue = (T) Boolean.FALSE;
		} else if (cls.equals(Integer.class)) {
			defaultValue = (T) Integer.valueOf(0);
		} else if (cls.equals(Double.class)) {
			defaultValue = (T) Double.valueOf(0.0);
		} else if (cls.equals(Byte.class)) {
			defaultValue = (T) Byte.valueOf((byte) 0);
		} else if (cls.equals(Short.class)) {
			defaultValue = (T) Short.valueOf((short) 0);
		} else if (cls.equals(Long.class)) {
			defaultValue = (T) Long.valueOf(0L);
		} else if (cls.equals(Float.class)) {
			defaultValue = (T) Float.valueOf(0.0f);
		} else if (cls.equals(Character.class)) {
			defaultValue = (T) Character.valueOf('\0');
		} else {
			defaultValue = null;
		}

		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Number, U extends Number> T cast(Class<T> cls, U value) {

		T result;
		
		if (cls.equals(Integer.class)) {
			result = (T) Integer.valueOf(value.intValue());
		} else if (cls.equals(Double.class)) {
			result = (T) Double.valueOf(value.doubleValue());
		} else if (cls.equals(Byte.class)) {
			result = (T) Byte.valueOf(value.byteValue());
		} else if (cls.equals(Short.class)) {
			result = (T) Short.valueOf(value.shortValue());
		} else if (cls.equals(Long.class)) {
			result = (T) Long.valueOf(value.longValue());
		} else if (cls.equals(Float.class)) {
			result = (T) Float.valueOf(value.floatValue());
		} else {
			throw new UnsupportedTypeException(cls);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T add(Class<T> cls, T value1, T value2) {

		T result;

		if (cls.equals(Integer.class)) {
			result = (T) (Integer.valueOf(value1.intValue() + value2.intValue()));
		} else if (cls.equals(Double.class)) {
			result = (T) (Double.valueOf(value1.doubleValue() + value2.doubleValue()));
		} else if (cls.equals(Long.class)) {
			result = (T) (Long.valueOf(value1.longValue() + value2.longValue()));
		} else if (cls.equals(Float.class)) {
			result = (T) (Float.valueOf(value1.floatValue() + value2.floatValue()));
		} else {
			throw new UnsupportedTypeException(cls);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T substract(Class<T> cls, T value1, T value2) {

		T result;

		if (cls.equals(Integer.class)) {
			result = (T) (Integer.valueOf(value1.intValue() - value2.intValue()));
		} else if (cls.equals(Double.class)) {
			result = (T) (Double.valueOf(value1.doubleValue() - value2.doubleValue()));
		} else if (cls.equals(Long.class)) {
			result = (T) (Long.valueOf(value1.longValue() - value2.longValue()));
		} else if (cls.equals(Float.class)) {
			result = (T) (Float.valueOf(value1.floatValue() - value2.floatValue()));
		} else {
			throw new UnsupportedTypeException(cls);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T multiply(Class<T> cls, T value1, T value2) {

		T result;

		if (cls.equals(Integer.class)) {
			result = (T) (Integer.valueOf(value1.intValue() * value2.intValue()));
		} else if (cls.equals(Double.class)) {
			result = (T) (Double.valueOf(value1.doubleValue() * value2.doubleValue()));
		} else if (cls.equals(Long.class)) {
			result = (T) (Long.valueOf(value1.longValue() * value2.longValue()));
		} else if (cls.equals(Float.class)) {
			result = (T) (Float.valueOf(value1.floatValue() * value2.floatValue()));
		} else {
			throw new UnsupportedTypeException(cls);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T divide(Class<T> cls, T value1, T value2) {

		T result;

		if (cls.equals(Integer.class)) {
			result = (T) (Integer.valueOf(value1.intValue() / value2.intValue()));
		} else if (cls.equals(Double.class)) {
			result = (T) (Double.valueOf(value1.doubleValue() / value2.doubleValue()));
		} else if (cls.equals(Long.class)) {
			result = (T) (Long.valueOf(value1.longValue() / value2.longValue()));
		} else if (cls.equals(Float.class)) {
			result = (T) (Float.valueOf(value1.floatValue() / value2.floatValue()));
		} else {
			throw new UnsupportedTypeException(cls);
		}
		
		return result;
	}
}
