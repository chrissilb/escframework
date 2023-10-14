package de.gwasch.code.escframework.events.streams;

import java.util.LinkedList;
import java.util.Queue;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.events.listeners.EventListener;

//todo, Semaphore entfernen
public class EventInputStream<E extends Event> {

	private final Object sema = new Object();

	private final EventListener<E> eventHandler;
	private final Queue<E> pendingEvents;
	
	public EventInputStream() {
		eventHandler = new MyEventHandler();
		pendingEvents = new LinkedList<E>();
	}
	
	public EventListener<E> getEventHandler() {
		return eventHandler;
	}
	
	class MyEventHandler extends EventAdapter<E> {

		public boolean onProcess(E event) {
			
			synchronized (sema) {
				pendingEvents.offer(event);
				sema.notify();
			}
			
			return true;
		}
	}
	
	public E readEvent() {

		E event;
		synchronized (sema) {
			if (pendingEvents.size() == 0) {
				try {
					sema.wait();
				} 
				catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			
			event = pendingEvents.poll();
		}		
		
		return event;
	}
}
