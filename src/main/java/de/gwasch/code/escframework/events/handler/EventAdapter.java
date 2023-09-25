package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;

public abstract class EventAdapter<E extends Event> implements EventListener<E> {

	public boolean onProcess(E event) {
		return true;
	}

	public void onFinish(E event, boolean success) {
	}
}
