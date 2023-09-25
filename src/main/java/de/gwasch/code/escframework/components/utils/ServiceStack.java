package de.gwasch.code.escframework.components.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class ServiceStack {
	private static Deque<Object> stack = new ArrayDeque<>();
	
	public static boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public static void push(Object object) {
		stack.push(object);
	}
	
	public static Object poll() {
		return stack.poll();
	}
	
	public static Object peek() {
		return stack.peek();
	}
	

	public static Object find(Class<?> cls) {
		
		Iterator<Object> it = stack.iterator();
		if (!it.hasNext()) {
			return null;
		}
		it.next();
		
		while(it.hasNext()) {
			Object entry = it.next();
			
			if (cls.isInstance(entry)) {
				return entry;
			}
		}
		
		return null;
	}
	
	public static Iterator<Object> iterator() {
		return stack.iterator();
	}
}
