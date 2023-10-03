package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Callback;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

/**
 * Collects {@code Callbacks} of multiple successor {@code Processors} and OR-links {@code success}.
 * This class can be used by event processors with multiple successor processors.
 * 
 * @param <E> the event type considered by the {@code MergeCallbackHandler}
 * 
 * @see Processor
 * @see Callback
 */
public class MergeCallbackHandler<E extends Event> implements CallbackListener<E> {

	private int nrRemainingCallbacks;
	private boolean success;
	private E callbackEvent;

	public MergeCallbackHandler(int nrCallbacks, E callbackEvent) {
		assert nrCallbacks > 0;
		nrRemainingCallbacks = nrCallbacks;
		success = true;
		this.callbackEvent = callbackEvent;
	}
	
	public int getNrRemainingCallbacks() {
		return nrRemainingCallbacks;
	}
	
	public void finish(E event, boolean success) {

		nrRemainingCallbacks--;
		assert nrRemainingCallbacks >= 0;
		
		this.success |= success;
		
		if (nrRemainingCallbacks == 0) {
			Processor.callbackGeneric(callbackEvent, this.success, null);
		}
	}

}
