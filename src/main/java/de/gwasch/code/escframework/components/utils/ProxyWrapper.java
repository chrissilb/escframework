package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.events.processors.Dispatcher;

/**
 * The class {@code ProxyWrapper} is mainly needed by framework classes to manage components by a hash table.
 * This is needed because components are realized as dynamic proxies which can have application-specific implementations
 * of {@link Object#equals(Object)} and {@link Object#hashCode()}. {@code ProxyWrapper} implements those methods
 * comparing instances and providing hash codes based on the proxy instance identity. 
 * <p>
 * {@code ProxyWrapper}s are used as sources for {@link InvocationEvent}s because {@link Dispatcher} uses a {@code HashMap}
 * to filter by event sources.
 * <p>
 * Furthermore, {@code ProxyWrapper} provides information about the methods implemented by a dynamic proxy class.
 */
public class ProxyWrapper {
	
	private Proxy proxy;
	
	public ProxyWrapper(Proxy proxy) {
		if (proxy == null) {
			System.out.println("ProxyWrapper: hier");
		}
		
		this.proxy = proxy;
	}
	
	public Proxy getProxy() {
		return proxy;
	}
	
	public static Method getMethod(Object proxy, String methodName, Class<?>[] paramTypes) {
		
		for (Class<?> cls : proxy.getClass().getInterfaces()) {
			
			for (Method method : cls.getMethods()) {
				
				if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), paramTypes)) {
					return method;
				}
			}
		}
		
		return null;
	}
	
	public static boolean hasMethod(Object proxy, Method method) {
		
		String methodName = method.getName();
		Class<?>[] paramTypes = method.getParameterTypes();
		
		for (Class<?> cls : proxy.getClass().getInterfaces()) {
			
			for (Method m : cls.getMethods()) {
				
				if (m.getName().equals(methodName) && Arrays.equals(m.getParameterTypes(), paramTypes)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean equals(Object obj) {
		ProxyWrapper cmp = (ProxyWrapper)obj;
		return proxy == cmp.proxy;
	}
	
	public int hashCode() {
		int hashCode = Proxy.getInvocationHandler(proxy).hashCode();
		return hashCode;
	}
	
	public String toString() {
		return proxy.getClass().getSimpleName() + " " + Proxy.getInvocationHandler(proxy).toString();
	}
}