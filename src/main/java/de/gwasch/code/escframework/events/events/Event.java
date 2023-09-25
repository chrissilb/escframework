package de.gwasch.code.escframework.events.events;
import java.util.Queue;

public interface Event extends Comparable<Event>, Cloneable {
	
	int getId();
	void setId(int id);
	
	long getPushTime();
	void setPushTime(long time);
	
	long getPopTime();
	void setPopTime(long time);
	
	Object getSource();
	void setSource(Object source);
	
	Queue<Event> getCauses();
	void putCause(Event event);
	
	void suspend();
	void resume();
	
	void merge(Event event);
	
	boolean hasCallback();
	Callback<?> popCallback();	
	void pushCallback(Callback<?> callback);
}
