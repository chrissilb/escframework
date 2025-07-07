package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.components.exceptions.InvocationException;
import de.gwasch.code.escframework.components.exceptions.NullExpansionException;
import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.events.listeners.EventListener;
import de.gwasch.code.escframework.events.patterns.PostEventListener;
import de.gwasch.code.escframework.events.streams.EventOutputStream;

public class Skeleton {
	
	private abstract class Invocation {
				
		InvocationEvent event;
		Object returnValue;
		Throwable throwable;
		
		Invocation(InvocationEvent event) {
			this.event = event;
			returnValue = null;
			throwable = null;
		}
		
		abstract void doInvoke() throws Exception; 
		
		boolean doReturn() {
//			ReturnEvent re = new ReturnEvent(Skeleton.this, event, returnValue, throwable);
//			out.writeEvent(re);
			return postEventListener.onProcess(Skeleton.this, event, true, returnValue, throwable);
		}
		
	}
	
	private class GetImpl extends Invocation {
		
		GetImpl(InvocationEvent event) {
			super(event);
		}
		
		void doInvoke() throws Exception {
			returnValue = service;
		}
	}

	//todo, mit dem aktuellen Ansatz darf eine asterisk-methode kein expansion-objekt ändern
	private class GetExpansionEnum extends Invocation {
		
		GetExpansionEnum(InvocationEvent event) {
			super(event);
		}
		
		void doInvoke() throws Exception {			
						
			String enumTypeName = metaType.getBase().getInterfaceType().getName() + "Enum";
			Class<?> enumType = Class.forName(enumTypeName);
			Method m = enumType.getMethod("valueOf", String.class);
			returnValue = m.invoke(null, metaType.getInterfaceType().getSimpleName());
		}
	}
	
	private class MethodInvocation extends Invocation {

		private Method method;
		private Object service;
		private Object[] args;
		
		MethodInvocation(InvocationEvent event, Method method, Object service, Object[] args) {
			super(event);
			this.method = method;
			this.service = service;
			this.args = args;
		}
		
		MethodInvocation(InvocationEvent event, Method method, Object service) {
			this(event, method, service, event.getArgs());
		}
		
		MethodInvocation(InvocationEvent event, Object service) {
			this(event, event.getMethod().getMethod(), service, event.getArgs());
		}
				
		void doInvoke() throws Exception {
			
//			assert(event.isOneway() && method.getReturnType() == Void.TYPE || !event.isOneway() && method.getReturnType() != Void.TYPE);
			
			returnValue = method.invoke(service, args);
		}
	}

	private class EventHandler extends EventAdapter<InvocationEvent> {
		
		private boolean inAsteriskMethod;	// NOTE: prevents recursive invocations of asterisk methods

		EventHandler() {
			inAsteriskMethod = false;
		}
		
		public boolean onProcess(InvocationEvent event) {
			
			assert event.getMethod().getMethod() != null;
			
//			System.out.println("Skeleton: " + event);
			
			Invocation invocation = null;
						
			try {
				boolean delegateNeeded = false;
				
				invocation = retrieveInvocation(event);

				if (invocation == null) {
					delegateNeeded = true;
					
					invocation = retrieveExpansion(event);
					
					if (invocation == null) {
						if (metaType.isExtension()) {
							invocation = new MethodInvocation(event, core);		
						}
						else {
//							System.out.println("delegate to PatternMatcher");
							throw new InvocationException("Invalid method: " + event.getMethod() + ".\n"
									+ "Possible reason: this might be an exclusive rule method but the corresponding rule is not installed.");
						}
					}
				}

				
				invokeAsterisks(invocation, AsteriskType.BEFORE_ALL);
				
				if (delegateNeeded) {
					invokeAsterisks(invocation, AsteriskType.BEFORE_ELSE);
				}

				invocation = invokeAsterisks(invocation, AsteriskType.INSTEAD_ALL);
				
				if (delegateNeeded) {
					invocation = invokeAsterisks(invocation, AsteriskType.INSTEAD_ELSE);
				}
					
				invocation.doInvoke();
				
				if (delegateNeeded) {
					invokeAsterisks(invocation, AsteriskType.AFTER_ELSE);
				}
				
				invokeAsterisks(invocation, AsteriskType.AFTER_ALL);
			}
			catch (Exception e) {
				System.out.println("Skeleton: Exception");
				e.printStackTrace();
				if (invocation == null) {
					invocation = new MethodInvocation(event, event.getMethod().getMethod(), service); 
				}
				
				try {
					throw e;
				}
				catch(InvocationTargetException ex) {
					invocation.throwable = ex.getTargetException();
				}
				catch(Exception ex) {
					invocation.throwable = ex;
				}
			}
						
			return invocation.doReturn();
		}
		
		private Invocation invokeAsterisks(Invocation invocation, AsteriskType asteriskType) throws Exception {
			
			if (! (invocation instanceof MethodInvocation)) {
				return invocation;
			}
			
			if (inAsteriskMethod) {
				return invocation;
			}
			
			inAsteriskMethod = true;
			
			MethodInvocation methodInvocation = (MethodInvocation)invocation;
			
			for (AsteriskMethod asteriskMethod : metaType.getAsteriskMethods()) {
				
				if (asteriskMethod.getAsteriskType() != asteriskType) {
					continue;
				}
							
				if (asteriskMethod.getParameterCount() == 0) {
					asteriskMethod.getMethod().invoke(service);
				}
				else {
					Object[] args = new Object[] { methodInvocation.service, methodInvocation.method,  methodInvocation.args };

					if (asteriskType == AsteriskType.INSTEAD_ALL || asteriskType == AsteriskType.INSTEAD_ELSE) {
						invocation = new MethodInvocation(invocation.event, asteriskMethod.getMethod(), service, args);
					}
					else {
						asteriskMethod.getMethod().invoke(service, args);	
					}
				}
			}			
			
			inAsteriskMethod = false;
			
			return invocation;
		}
		
		private Invocation retrieveInvocation(InvocationEvent event) throws Exception {
					
			MetaMethod metaMethod = event.getMethod();
			Invocation invocation = null;
					
			if (metaMethod.getName().equals("get" + metaType.getImplementationType().getSimpleName())
				&& (event.getArgs() == null || event.getArgs().length == 0)) {

				invocation = new GetImpl(event);
			}
			else if (metaType.getBase() != null && metaMethod.getName().equals("get" + metaType.getBase().getInterfaceType().getSimpleName() + "Enum")
					&& (event.getArgs() == null || event.getArgs().length == 0)) {
					
				invocation = new GetExpansionEnum(event);
			}
			else {
				Method method = null;
				Boolean inheritsMethod = null;  
					
				method = metaMethod.getDeclaredMethod(metaType.getImplementationType(), event.getArgs());
				
				if (method == null && !metaType.isExtension() && !(inheritsMethod = metaType.isInheriting(metaMethod, event.getArgs())) 
					&& metaMethod.getMethod().getDeclaringClass() == Object.class) {
					method = metaMethod.getMethod();
				}

							
				if (method != null) {
					
					if (metaType.hasClient()) {

						Object caller = ServiceStack.find(metaType.getClientType());
						
						//NOTE: non null clients can only be skipped if the invocation can be delegated to the core.
						if (caller != null || metaType.allowNullClient() || !ProxyWrapper.hasMethod(core, method)) {
							
							if (metaType.getClientField() != null) {
								Field f = metaType.getClientField();
								f.setAccessible(true);
								f.set(service, caller);
								f.setAccessible(false);
							}
							
							invocation = new MethodInvocation(event, method, service); 
						}
					}
					else {
						invocation = new MethodInvocation(event, method, service);
					}
				}
				else {		
					if (inheritsMethod == null) {
						inheritsMethod = metaType.isInheriting(metaMethod, event.getArgs());
					}
					
					if (inheritsMethod) {		
						invocation = new MethodInvocation(event, base); 
					}
//					else if (metaType.hasExpansions()) {
//						method = event.getMethod().getMethod();
//						
//						for (Field expField : metaType.getExpFields()) {
//							Class<?> expType = expField.getType();
//							
//							if (method.getDeclaringClass().isAssignableFrom(expType)) {
//								
//								expField.setAccessible(true);
//								Object expansion = expField.get(service);
//								expField.setAccessible(false);
//																
//								invocation = new MethodInvocation(event, expansion); 
//								
//								if (expansion == null) {
//									NullExpansionException e = new NullExpansionException("Expansion '" + expField + "' is null.");
//									throw e;
////									invocation.throwable = e;
//								}
//								
//								break;
//							}
//						}
//					}
				}
			}
			
			return invocation;
		}
		
		private Invocation retrieveExpansion(InvocationEvent event) throws Exception {
			
			Invocation invocation = null;
			
			if (metaType.hasExpansions()) {
				Method method = event.getMethod().getMethod();
				
				for (Field expField : metaType.getExpFields()) {
					Class<?> expType = expField.getType();
					
					if (method.getDeclaringClass().isAssignableFrom(expType)) {
						
						expField.setAccessible(true);
						Object expansion = expField.get(service);
						expField.setAccessible(false);

						if (expansion != null) {
							invocation = new MethodInvocation(event, expansion); 
						}
						else {
							NullExpansionException e = new NullExpansionException("Expansion '" + expField + "' is null.");
							throw e;
//							invocation.throwable = e;
						}
						
						break;
					}
				}
			}
			
			return invocation;
		}
	}
	
//	private EventOutputStream<ReturnEvent> out;
	private PostEventListener postEventListener;
	
	private final MetaType metaType;
	private final Object service;
	
	private final EventHandler eventHandler;
	
	private final Object base;
	private final Object core;
	
	
	public Skeleton(MetaType metaType, Object service, Object base, Object core) {
		this.metaType = metaType;
		this.service = service;
		this.base = base;
		this.core = core;
	
		eventHandler = new EventHandler();
	}

//	public void setOut(EventOutputStream<ReturnEvent> out) {
//		this.out = out;
//	}
	
	public void setPostEventListener(PostEventListener listener) {
		postEventListener = listener;
	}
	
	public EventListener<InvocationEvent> getEventHandler() {
		return eventHandler;
	}
	
	public MetaType getMetaType() {
		return metaType;
	}
	
	public Object getService() {
		return service;
	}
		
	public String toString() {
		return metaType.getInterfaceType().getName();
	}
}









