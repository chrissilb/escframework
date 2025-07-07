package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.events.InvocationEvent;

public class Stub implements InvocationHandler {
		
//	private EventInputStream<ReturnEvent> in;
//	private EventOutputStream<InvocationEvent> out;
	
	private MetaType metaType;
	private boolean isServiceEntrance;

	public Stub(MetaType metatype) {
		metaType = metatype;
		isServiceEntrance = false;
	}
//	
//	public boolean isServiceEntrance() {
//		return isServiceEntrance;
//	}
	
	public void setServiceEntrance(boolean isServiceEntrance) {
		this.isServiceEntrance = isServiceEntrance;
	}
	
	public MetaType getMetaType() {
		return metaType;
	}
//	
//	public void setStreams(EventInputStream<ReturnEvent> in, EventOutputStream<InvocationEvent> out) {
//		this.in = in;
//		this.out = out;
//	}
//
//	public EventInputStream<ReturnEvent> getIn() {
//		return in;
//	}
//	
//	public EventOutputStream<InvocationEvent> getOut() {
//		return out;
//	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

//		System.out.println(method);
		
		MetaMethod metaMethod = new MetaMethod(method);
		InvocationEvent ie = new InvocationEvent(proxy, metaMethod, args);

		Object retval = InstanceAllocator.getInvocationManager().invoke(proxy, ie, isServiceEntrance);
		return retval;
	}
		
	public String toString() {
		return metaType.getInterfaceType().getSimpleName();
	}
}