package de.gwasch.code.escframework.events.events;


import java.util.concurrent.locks.Condition;

//NOTE: TimerAction does not directly react on cancel. This is because cancelled Actions can be reused, 
// e.g. for reenqueuing after suppressed in a Scheduler. In this case execute() will be called again.
public class TimerAction<E extends Event> extends AbstractAction {
	
	private Condition condition;
	private final E event;

	public TimerAction(E event) {
		this.event = event;
		condition = null;
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public int getId() {
		System.out.println("TimerAction.getId(): hier");
		return super.getId();
	}
	public E getEvent() {
		return event;
	}

	public boolean execute() {
				
		long timeout = (event.getPushTime() - System.currentTimeMillis()) * 1000000L;
				
		if (timeout > 0L) {
			try {
				//todo, awaitNanos ist nicht mehr nötig
				timeout = condition.awaitNanos(timeout);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
		if (timeout > 0) {
			return false;
		}
//		else if (isCancelled) {
//			return false;
//		}
					
		return true;
	}
	
	public void suspend() {
		event.suspend();
	}
		
	public void resume() {
		event.resume();
	}
	
	//todo, anpassen für suspended
	public int compareTo(Event evt) {
//		System.out.println("    compareTo:" + this + " to " + cmp);
		
//		if (isCancelled) {
//			throw new IllegalStateException("Event is cancelled. PushTime is unknown.");
//		}
		
		@SuppressWarnings("unchecked")
		TimerAction<E> cmp = (TimerAction<E>)evt;
		
		long diff = event.getPushTime() - cmp.event.getPushTime();
		if (diff > 0L) {
			return 1;
		}
		else if (diff < 0L) {
			return -1;
		}
		else {
			return 0;
		}
	}
		
	public boolean equals(Object obj) {
		TimerAction<?> cmp = (TimerAction<?>)obj;
		return event == cmp.event;
	}
	
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append(": ");
		sb.append(event.toString());
		return sb.toString();
	}
}