package de.gwasch.code.escframework.components.utils;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.patterns.PostEventListener;
import de.gwasch.code.escframework.events.processors.Processor;

public class InvocationPostEventHandler implements PostEventListener {
	
	private Processor<Event> processor;
	
	public InvocationPostEventHandler() {
		processor = null;
	}
	
	public void setProcessor(Processor<Event> processor) {
		this.processor = processor;
	}
	
	public boolean onProcess(Object source, Event event, boolean consumed, Object returnValue, Throwable throwable) {
				
		if (event instanceof InvocationEvent) {
			
			if (consumed || throwable != null) {
				InvocationEvent ie = (InvocationEvent)event;
				
				ReturnEvent re = new ReturnEvent(source, ie, returnValue, throwable);
				processor.process(re);
			}
		}
		else if (throwable != null) {
			throw new RuntimeException(throwable);
		}
		
		return !consumed && throwable == null;
	}
}