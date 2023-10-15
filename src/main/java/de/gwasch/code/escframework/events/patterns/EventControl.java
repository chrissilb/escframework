package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public interface EventControl {
	
	String getName();
	boolean onEvent(Event event);
}