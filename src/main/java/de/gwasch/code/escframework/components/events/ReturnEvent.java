package de.gwasch.code.escframework.components.events;

import de.gwasch.code.escframework.components.utils.Skeleton;
import de.gwasch.code.escframework.events.events.AbstractEvent;

//todo, methode fehlt zum vergleich?
public class ReturnEvent extends AbstractEvent {

	private InvocationEvent invocationEvent;
	private Object returnValue;
	
	public ReturnEvent(Skeleton source, InvocationEvent invocationEvent, Object value) {
		setSource(source);
		this.invocationEvent = invocationEvent;
		returnValue = value;
	}
	
	public ReturnEvent(Skeleton source, InvocationEvent invocationEvent) {
		this(source, invocationEvent, null);
	}
	
	
//	public ReturnEvent clone() {
//		ReturnEvent clone = new ReturnEvent(returnValue);
//		return clone;
//	}
	
	public InvocationEvent getInvocationEvent() {
		return invocationEvent;
	}
	
	public Object getReturnValue() {
		return returnValue;
	}
	
//	public void setReturnValue(Object value) {
//		returnValue = value;
//	}
	
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
