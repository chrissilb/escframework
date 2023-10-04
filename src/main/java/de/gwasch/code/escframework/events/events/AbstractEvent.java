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
	
	public AbstractEvent() {
		id = -1;
		pushTime = 0;
		popTime = 0;
		source = null;
		causes = new LinkedList<Event>();
		callbacks = new Stack<>();
		suspendTime = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public long getPushTime() {
		if (suspendTime != 0) {
			throw new IllegalStateException("Event is suspended. PushTime is unknown.");
		}
		
		return pushTime;
	}

	public void setPushTime(long time) {
		pushTime = time;
	}
	
	public long getPopTime() {
		return popTime;
	}

	public void setPopTime(long time) {
		popTime = time;
	}

	public Object getSource() {
		return source;
	}
	
	public void setSource(Object source) {
		assert source != null;
		this.source = source;
	}
	
	public Queue<Event> getCauses() {
		return causes;
	}
	
	public void putCause(Event cause) {
		causes.offer(cause);
	}
	
	public void suspend() {
		if (suspendTime == 0) {
			suspendTime = System.currentTimeMillis();
		}
	}
	
	public void resume() {
		if (suspendTime != 0) {
			pushTime += System.currentTimeMillis() - suspendTime;
			suspendTime = 0;
		}
	}
	
	public void merge(Event event) {
	}

	public int compareTo(Event event) {
		return getId() - event.getId();
	}

	public boolean hasCallback() {
		return !callbacks.empty();
	}
	
	public Callback<?> popCallback() {
		return callbacks.pop();
	}
	
	public void pushCallback(Callback<?> callback) {
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
			}
			else {
				isFirst = false;
			}
			
			sb.append("pushTime=");
			
			if (suspendTime == 0) {
				sb.append(datefmt.format(pushTime));
			}
			else {
				sb.append("suspended");
			}
			
		}

		if (popTime != 0) {
			if (!isFirst) {
				sb.append(", ");
			}
			else {
				isFirst = false;
			}
			
			sb.append("popTime=");
			sb.append(datefmt.format(popTime));
		}
		
		sb.append(")");
		
		return sb.toString();		
	}
}
