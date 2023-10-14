package de.gwasch.code.escframework.events.events;
import java.util.Queue;

import de.gwasch.code.escframework.events.processors.Dispatcher;

/**
 * {@code Event} is the main interface of all events.
 */
public interface Event extends Comparable<Event>, Cloneable {
	
	/**
	 * Returns the event Identifier.
	 * @return The Identifier.
	 */
	int getId();
	/**
	 * Sets the event Identifier.
	 * @param id The event Identifier.
	 */
	void setId(int id);
	
	/** 
	 * Returns the timestamp indicating when the {@code Event} shall be processed.
	 * @return The timestamp in ms.
	 */
	long getPushTime();
	/**
	 * Sets the timestamp indicating when the {@code Event} shall be processed.
	 * @param time The timestamp in ms.
	 */
	void setPushTime(long time);
	
	/**
	 * Return the timestamp indicating when the {@code Event} processing is finished.
	 * @return The timestamp in ms.
	 */
	long getPopTime();
	/**
	 * Sets the timestamp indicating when the {@code Event} processing is finished.
	 * @param time The timestamp in ms.
	 */
	void setPopTime(long time);
	
	/**
	 * Returns the {@code Event} source. {@link Dispatcher} allows to filter for it.
	 * @return The source object.
	 */
	Object getSource();
	/**
	 * Sets the {@code Event} source. {@link Dispatcher} allows to filter for it.
	 * @param source The source object.
	 */
	void setSource(Object source);
	
	Queue<Event> getCauses();
	void putCause(Event event);
	
	/**
	 * Suspends the {@code Event}.
	 */
	void suspend();
	/**
	 * Resumes the {@code Event}.
	 */
	void resume();
	
	void merge(Event event);
	
	boolean hasCallback();
	Callback<?> popCallback();	
	void pushCallback(Callback<?> callback);
}
