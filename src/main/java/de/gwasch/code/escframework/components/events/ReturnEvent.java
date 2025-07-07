package de.gwasch.code.escframework.components.events;

import de.gwasch.code.escframework.components.utils.Skeleton;
import de.gwasch.code.escframework.events.events.AbstractEvent;

/**
 * A {@code ReturnEvent} gives feedback to an {@link InvocationEvent}.
 */
public class ReturnEvent extends AbstractEvent {

	private InvocationEvent invocationEvent;
	private Object returnValue;
	private Throwable exception;
	
	public ReturnEvent(Object source, InvocationEvent invocationEvent, Object value, Throwable exception) {
		setSource(source);
		this.invocationEvent = invocationEvent;
		returnValue = value;
		this.exception = exception;
	}
	
	public ReturnEvent(Object source, InvocationEvent invocationEvent) {
		this(source, invocationEvent, null, null);
	}
		
	public InvocationEvent getInvocationEvent() {
		return invocationEvent;
	}
	
	public Object getReturnValue() {
		return returnValue;
	}
	
	public Throwable getThrowable() {
		return exception;
	}
	
	public boolean equals(Object obj) {
		if (! (obj instanceof ReturnEvent)) {
			return false;
		}
		
		ReturnEvent cmp = (ReturnEvent)obj;
				
		boolean ret = getSource().equals(cmp.getSource());
		ret &= invocationEvent == cmp.invocationEvent;
		return ret;
	}
	
	public int hashCode() {
		return getSource().hashCode() ^ invocationEvent.objectHashCode();
	}
}
