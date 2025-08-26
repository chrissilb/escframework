package de.gwasch.code.escframework.events.events;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * {@code AbstractEvent} is typically used as super class for concrete events.
 * It provides the basic functionality of an {@code Event}.
 */
public abstract class AbstractEvent implements Event {

	private int id;
	private long pushTime;
	private long popTime;
	private Object source;
	private Queue<Event> causes;
	private Stack<Callback<?>> callbacks;
	private long suspendTime;

	protected AbstractEvent() {
		id = -1;
		pushTime = 0;
		popTime = 0;
		source = null;
		causes = new LinkedList<Event>();
		callbacks = new Stack<>();
		suspendTime = 0;
	}

	/**
	 * Returns the unique id of an event.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of an event.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the push time of an event, i.e. the timestamp where an event get into
	 * a processor network.
	 */
	public long getPushTime() {
		if (suspendTime != 0) {
			throw new IllegalStateException("Event is suspended. PushTime is unknown.");
		}

		return pushTime;
	}

	/**
	 * Sets the push time of an event.
	 */
	public void setPushTime(long time) {
		pushTime = time;
	}

	/**
	 * Returns the pop time of an event, i.e. the timestamp where an event leaves a
	 * processor network.
	 */
	public long getPopTime() {
		return popTime;
	}

	/**
	 * Sets the pop time of an event.
	 */
	public void setPopTime(long time) {
		popTime = time;
	}

	/**
	 * Returns the source of the event.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Sets the source of the event.
	 */
	public void setSource(Object source) {
		assert source != null;
		this.source = source;
	}

	/**
	 * Returns the queue of event causes.
	 */
	public Queue<Event> getCauses() {
		return causes;
	}

	/**
	 * Adds an event cause.
	 */
	public void putCause(Event cause) {
		causes.offer(cause);
	}

	/**
	 * Suspends the event in the processor network. Hence the push time is postponed
	 * until it is resumed.
	 */
	public void suspend() {
		if (suspendTime == 0) {
			suspendTime = System.currentTimeMillis();
		}
	}

	/**
	 * Resumes the event in the processor network. The push time is reset to the
	 * original push time plus the time interval while it was suspended.
	 */
	public void resume() {
		if (suspendTime != 0) {
			pushTime += System.currentTimeMillis() - suspendTime;
			suspendTime = 0;
		}
	}

	/**
	 * Provides an empty method so that sub classes do not need to implement a merge
	 * method.
	 */
	public void merge(Event event) {
	}

	public int compareTo(Event event) {
		return getId() - event.getId();
	}

	
	public final boolean hasCallback() {
		return !callbacks.empty();
	}

	public final Callback<?> popCallback() {
		return callbacks.pop();
	}

	public final void pushCallback(Callback<?> callback) {
		callbacks.push(callback);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

//		SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//		SimpleDateFormat datefmt = new SimpleDateFormat("ss:SSS");
		SimpleDateFormat datefmt = new SimpleDateFormat("HH:mm:ss:SSS");

		sb.append(getClass().getSimpleName());
		sb.append("(");

		boolean isFirst = true;

		if (id != -1) {
			sb.append("id=");
			sb.append(id);
			isFirst = false;
		}

		if (pushTime != 0) {
			if (!isFirst) {
				sb.append(", ");
			} else {
				isFirst = false;
			}

			sb.append("pushTime=");

			if (suspendTime == 0) {
				sb.append(datefmt.format(pushTime));
			} else {
				sb.append("suspended");
			}

		}

		if (popTime != 0) {
			if (!isFirst) {
				sb.append(", ");
			} else {
				isFirst = false;
			}

			sb.append("popTime=");
			sb.append(datefmt.format(popTime));
		}

		sb.append(")");

		return sb.toString();
	}
}
