package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;

public interface ProcessListener<E extends Event> {
	
	void process(E event);
}