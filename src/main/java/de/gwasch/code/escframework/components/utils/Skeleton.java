package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.components.exceptions.InvocationException;
import de.gwasch.code.escframework.events.handler.EventAdapter;
import de.gwasch.code.escframework.events.handler.EventListener;
import de.gwasch.code.escframework.events.streams.EventOutputStream;

public class Skeleton {
	
	private abstract class Invocation {
				
		InvocationEvent event;
		Object returnValue;
		
		Invocation(InvocationEvent event) {
			this.event = event;
			returnValue = null;
		}
		
		abstract void doInvoke() throws Exception; 
		
		void doReturn() {
			ReturnEvent re = new ReturnEvent(Skeleton.this, event, returnValue);
			out.writeEvent(re);
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

		Method method;
		Object service;
		Object[] args;
		
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
			
			MetaMethod metaMethod = event.getMethod();
			assert(metaMethod.isOneway() && method.getReturnType() == Void.TYPE || !metaMethod.isOneway() && method.getReturnType() != Void.TYPE);

//			if (method.getName().equals("startAutoLogout")) {
//				System.out.println("hier");
//			}
			
			try {
				returnValue = method.invoke(service, args);
			}
			catch (Exception e) {
				System.out.println("Invocation: hier: " + e);
			}
		}
	}

	private EventOutputStream<ReturnEvent> out;
	
	private final MetaType metaType;
	private final Object service;
	private final Object base;
	private final Object core;
	
	private boolean inAsteriskMethod;	// NOTE: prevents recursive invocations of asterisk methods
	
	private final EventHandler eventHandler;
	
	
	public Skeleton(MetaType metaType, Object service, Object base, Object core) {
		this.metaType = metaType;
		this.service = service;
		this.base = base;
		this.core = core;
		
		inAsteriskMethod = false;
		
		eventHandler = new EventHandler();
	}

	public void setOut(EventOutputStream<ReturnEvent> out) {
		this.out = out;
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
	
	class EventHandler extends EventAdapter<InvocationEvent> {

		public boolean onProcess(InvocationEvent event) {
			
//			if (event.getMethod().getMethod().getName() == "xstart") {
//				System.out.println("hier");
//			}
			
			assert event.getMethod().getMethod() != null;
			
			Invocation invocation = null;
						
			try {
				boolean delegateNeeded = false;
				
				invocation = retrieveInvocation(event);

				if (invocation == null) {
					delegateNeeded = true;

					if (!metaType.isExtension()) {
						throw new InvocationException("Invalid method: " + event.getMethod() + ".\n"
								+ "Possible reason: this might be an exclusive rule method but the corresponding rule is not installed.");
					}
					
					invocation = new MethodInvocation(event, core);
				}

				
				invokeAsterisks(invocation, AsteriskType.BEFORE_ALLL);
				
				if (delegateNeeded) {
					invokeAsterisks(invocation, AsteriskType.BEFORE_ELSE);
				}

				invocation = invokeAsterisks(invocation, AsteriskType.ALL_INSTEAD);
				
				if (delegateNeeded) {
					invocation = invokeAsterisks(invocation, AsteriskType.ELSE_INSTEAD);
				}

				invocation.doInvoke();
				
				if (delegateNeeded) {
					invokeAsterisks(invocation, AsteriskType.AFTER_ELSE);
				}
				
				invokeAsterisks(invocation, AsteriskType.AFTER_ALL);
				
				invocation.doReturn();				
				return true;
			}
			catch(Exception e) {
				throw new InvocationException(e);
			}
		}
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

				if (asteriskType == AsteriskType.ALL_INSTEAD || asteriskType == AsteriskType.ELSE_INSTEAD) {
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
					
					//NOTE: no null clients can only be skipped if the invocation can be delegated to the core.
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
				else if (metaType.hasExpansions()) {
					method = event.getMethod().getMethod();
					
					for (Field expField : metaType.getExpFields()) {
						Class<?> expType = expField.getType();
						
						if (method.getDeclaringClass().isAssignableFrom(expType)) {
							
							expField.setAccessible(true);
							Object expansion = expField.get(service);
							expField.setAccessible(false);
							
							invocation = new MethodInvocation(event, expansion); 
							break;
						}
					}
				}
			}
		}
		
		return invocation;
	}
	
	public String toString() {
		return metaType.getInterfaceType().getName();
	}
}









