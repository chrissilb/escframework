package de.gwasch.code.escframework.components.utils;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.events.sources.EventFactory;

public class InvocationEventFactory implements EventFactory<InvocationEvent> {

	private Object eventSource;
	private MetaMethod method;
	
	public InvocationEventFactory(MetaMethod method) {
		this.method = method;
	}
	
	public Object getEventSource() {
		return eventSource;
	}
	
	public void setEventSource(Object source) {
		this.eventSource = source;
	}
	
	public MetaMethod getMethod() {
		return method;
	}
	
	public void setMethod(MetaMethod method) {
		this.method = method;
	}
		
	public InvocationEvent create() {
		return new InvocationEvent(eventSource, method, null);
	}
}
