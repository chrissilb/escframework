package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

/**
 * A {@code EventControl} 
 */
public interface EventControl {
	
	String getTypeName();
	boolean onEvent(Event event);
}