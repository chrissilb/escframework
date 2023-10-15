package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

/**
 * {@code RuleMode} defines the mode of a {@link Rule}. Those modes reflect states of a 
 * state-machine a {@code Rule} implements. The modes are a consequence of the interface of 
 * {@link Event} and the procedural environment of a pattern matcher. The latter one 
 * means that actions which are the output of a pattern matcher can lead into method invocations
 * which take time for there execution. During this time a {@code Rule} might want to have
 * a different behavior. This use case justifies {@link #PROCESSING_ACTION}. 
 */
public enum RuleMode {
	
	/**
	 * Event processing is active. Incoming trigger events are processed.
	 */
	ACTIVE,

	/**
	 * Event processing is active and processing of a sent action is ongoing.
	 */
	PROCESSING_ACTION,
	
	/**
	 * Event processing is suspended.
	 */
	SUSPENDED,
	
	/**
	 * Event processing inactive.
	 */
	INACTIVE
}
