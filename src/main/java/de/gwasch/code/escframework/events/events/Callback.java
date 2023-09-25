package de.gwasch.code.escframework.events.events;

import de.gwasch.code.escframework.events.handler.CallbackListener;

public class Callback<E extends Event> {

	private E event;
	private CallbackListener<? super E> callbackListener;
	
	public Callback(E event, CallbackListener<? super E> callbackListener) {
		this.event = event;
		this.callbackListener = callbackListener;
	}

	public E getEvent() {
		return event;
	}

	public void setEvent(E event) {
		this.event = event;
	}

	public CallbackListener<? super E> getCallbackListener() {
		return callbackListener;
	}

	public void setCallbackListener(CallbackListener<? super E> callbackListener) {
		this.callbackListener = callbackListener;
	}
	
	public void callback(boolean success) {
		getCallbackListener().finish(getEvent(), success);
	}
}
