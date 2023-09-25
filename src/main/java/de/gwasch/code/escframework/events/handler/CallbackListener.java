package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;

public interface CallbackListener<E extends Event> {
	
	void finish(E event, boolean success);
}