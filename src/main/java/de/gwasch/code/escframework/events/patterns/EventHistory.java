package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

/**
 * {@code EventHistory} is used to filter for new or reinserted events.
 * <p>
 * An shall be new if its {@link Event#getId()} is higher than the latest one received.
 * This typically presumes that an {@code Initializer} sets Ids in the processor network.
 * 
 * @see Rule
 * @see TriggerEventControl
 */
public enum EventHistory {
	
	/**
	 * All events shall be considered.
	 */
	EVERY,
	
	/**
	 * New events shall be considered, i.e. event id is higher than the latest one received.
	 */
	NEW,
	
	/**
	 * "Old" events shall be considered, i.e. event id is lower or equal than the latest one received.
	 */
	REINSERTED
}
