package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

/**
 * {@code RuleMode} defines the mode of a {@link Rule}. Those modes reflect
 * states of a state-machine a {@code Rule} implements. The modes are a
 * consequence of the interface of {@link Event} and the procedural environment
 * of a pattern matcher. The first one justifies {@link #ACTIVE},
 * {@link #SUSPENDED} and {@link #INACTIVE}. The latter one means that actions
 * which are the output of a pattern matcher can lead into method invocations
 * which take time for there execution. During this time a {@code Rule} might
 * want to have a different behavior. This use case justifies
 * {@link #PROCESSING_ACTION}.
 */
public enum RuleMode {

	/**
	 * Rule processing is active. Incoming trigger events are processed. Outgoing
	 * action events are created.
	 */
	ACTIVE,

	/**
	 * Rule processing is active and processing of a sent action is ongoing. 
	 * In this mode incoming trigger events might be ignored.
	 */
	PROCESSING_ACTION,

	/**
	 * Rule processing is suspended.
	 */
	SUSPENDED,

	/**
	 * Rule processing inactive.
	 */
	INACTIVE
}
