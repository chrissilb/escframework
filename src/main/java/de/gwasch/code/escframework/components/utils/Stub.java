package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.events.streams.EventInputStream;
import de.gwasch.code.escframework.events.streams.EventOutputStream;

public class Stub implements InvocationHandler {
		
	private EventInputStream<ReturnEvent> in;
	private EventOutputStream<InvocationEvent> out;
	
	private MetaType metaType;
	private boolean isServiceEntrance;

	public Stub(MetaType metatype) {
		metaType = metatype;
		isServiceEntrance = false;
	}
	
	public boolean isServiceEntrance() {
		return isServiceEntrance;
	}
	
	public void setServiceEntrance(boolean isServiceEntrance) {
		this.isServiceEntrance = isServiceEntrance;
	}
	
	public void setStreams(EventInputStream<ReturnEvent> in, EventOutputStream<InvocationEvent> out) {
		this.in = in;
		this.out = out;
	}
	
	public MetaType getMetaType() {
		return metaType;
	}

	public EventInputStream<ReturnEvent> getIn() {
		return in;
	}
	
	public EventOutputStream<InvocationEvent> getOut() {
		return out;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) {

		Object retval = null;

//		System.out.println(method);
		
		MetaMethod metaMethod = new MetaMethod(method);
		
//		InvocationStack.push(proxy, metaMethod);
		if (isServiceEntrance) {
			ServiceStack.push(proxy);
		}
		
		InvocationEvent ie = new InvocationEvent(proxy, metaMethod, args);

		out.writeEvent(ie);
				
		if (metaMethod.isOneway()) {
			retval = null;
		}
		else {
			ReturnEvent re = in.readEvent();
			retval = re.getReturnValue();
		}
			
		if (metaMethod.isOneway() && retval != null) {
			throw new IllegalStateException();
		}
		
		if (isServiceEntrance) {
			Object obj = ServiceStack.poll();
			
			if (proxy != obj) {
				System.out.println("Stub: hier");
			}
			assert proxy == obj;
		}
		
		return retval;
	}
		
	public String toString() {
		return metaType.getInterfaceType().getSimpleName();
	}
}