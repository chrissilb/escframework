package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MetaMethod {

	private Method method;
		
	public MetaMethod(Method method) {
		assert method != null;
				
		this.method = method;
	}
	
	public String getName() {
		return method.getName();
	}
	
	public boolean isOneway() {
		return method.getReturnType() == void.class;
	}
	
	public int getParameterCount() {
		return method.getParameterCount();
	}
	
	public Method getMethod() {
		return method;
	}
			
	private boolean isCompatible(Method method, Object[] args) {
		
		if (!this.method.getName().equals(method.getName())) return false;
		if (this.method.getParameterCount() != method.getParameterCount()) return false;
		int argssize = args == null ? 0 : args.length;
		if (this.method.getParameterCount() != argssize) return false;
		
		Class<?>[] cmpparams = method.getParameterTypes();
		for (int i = 0; i < this.method.getParameterCount(); i++) {
			
			if (this.method.getParameterTypes()[i].equals(cmpparams[i])) {			// Parametertypen stimmen überein
				continue;
			}
			
			if (!cmpparams[i].isInstance(args[i])) {					// Argument muss instanceof Parametertyp der Methode sein. Ablehnung, wenn Argument null. Falsch?
				return false;
			}
			
			Class<?> param = method.getParameterTypes()[i];
			
			if (!param.isInstance(args[i])) {							// Argument muss instanceof Parametertyp der MetaMethod sein. Ablehnung, wenn Argument null. Falsch?
				return false;
			}
		}
		
		return true;
	}
	
//	public Method getMethod(Class<?> cls, Object[] args) {
//		
//		Method method = null;
//		
//		for (Method m : cls.getMethods()) {
//
//			if (isCompatible(m, args)) {
//				
//				if (method == null) {
//					method = m;
//				}
//				else {
//					for (int i = 0; i < method.getParameterCount(); i++) {
//						if (method.getParameterTypes()[i].equals(m.getParameterTypes()[i])) {
//							continue;
//						}
//						if (method.getParameterTypes()[i].isAssignableFrom(m.getParameterTypes()[i])) {
//							method = m;
//							break;
//						}
//					}
//				}
//			}
//		}
//		
//		return method;
//	}	
	
	public Method getDeclaredMethod(Class<?> cls, Object[] args) {
	
		Method method = null;
		
		for (Method m : cls.getDeclaredMethods()) {
			
			if ((m.getModifiers() & Modifier.PUBLIC) == 0) {
				continue;
			}
			
			if (isCompatible(m, args)) {
				
				if (method == null) {
					method = m;
				}
				else {
					for (int i = 0; i < method.getParameterCount(); i++) {
						if (method.getParameterTypes()[i].equals(m.getParameterTypes()[i])) {
							continue;
						}
						if (method.getParameterTypes()[i].isAssignableFrom(m.getParameterTypes()[i])) {
							method = m;
							break;
						}
					}
				}
			}
		}
		
		return method;
	}
		
	public boolean equals(Object obj) {
		MetaMethod cmp = (MetaMethod)obj;
		
		boolean ret = method.equals(cmp.method);
		return ret;
	}
	
	public int hashCode() {
		return method.hashCode();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getName());
		
		sb.append('(');
		
		boolean first = true;
		
		for (int i = 0; i < method.getParameterCount(); i++) {
			
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}
			
			sb.append(method.getParameterTypes()[i].getSimpleName());
		}
		
		sb.append(')');
		
		return sb.toString();
	}
}
