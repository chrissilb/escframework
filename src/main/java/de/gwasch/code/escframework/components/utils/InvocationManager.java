package de.gwasch.code.escframework.components.utils;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.patterns.ActionManager;
import de.gwasch.code.escframework.events.streams.EventInputStream;
import de.gwasch.code.escframework.events.streams.EventOutputStream;

public class InvocationManager implements ActionManager {
	
	private EventInputStream<ReturnEvent> in;
	private EventOutputStream<InvocationEvent> out;
	
	public InvocationManager() {
		in = null;
		out = null;
	}
	
	public void setStreams(EventInputStream<ReturnEvent> in, EventOutputStream<InvocationEvent> out) {
		this.in = in;
		this.out = out;
	}

	public EventInputStream<ReturnEvent> getIn() {
		return in;
	}
	
	public EventOutputStream<InvocationEvent> getOut() {
		return out;
	}
	
	public Object invoke(Object service, Event event) {
		return invoke(service, event, false);
	}
	
	public Object invoke(Object service, Event event, boolean isServiceEntrance) {
		
		Object retval = null;
		Throwable throwable = null;
		
		if (isServiceEntrance) {
			ServiceStack.push(service);
		}
		
		InvocationEvent invocationEvent = (InvocationEvent)event;
		out.writeEvent(invocationEvent);
		
		ReturnEvent re = in.readEvent();
		retval = re.getReturnValue();
		throwable = re.getThrowable();
		
		if (isServiceEntrance) {
			Object obj = ServiceStack.poll();
			assert service == obj;
		}
		
		if (throwable == null) {
			return retval;
		}
		
		if (throwable instanceof RuntimeException) {
			RuntimeException rex = (RuntimeException)throwable;
			throw rex;
		} else if (throwable instanceof Error) {
			Error error = (Error)throwable;
			throw error;
		}
		else {
			throw new RuntimeException(throwable);
		}
	}

}
