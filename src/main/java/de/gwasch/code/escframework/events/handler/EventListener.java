package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;

public interface EventListener<E extends Event> {
	boolean onProcess(E event);
	void onFinish(E event, boolean success);
}