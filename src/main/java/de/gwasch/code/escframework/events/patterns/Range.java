package de.gwasch.code.escframework.events.patterns;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.gwasch.code.escframework.events.events.Event;

//NOTE: a range is in descending order form latest to oldest events
public class Range {

	private int maxSize;
	private long maxAge;
	
	private List<Event> events;
	
	public Range(int maxSize, long maxAge) {
		this.maxSize = maxSize;
		this.maxAge = maxAge;
		
		events = new LinkedList<>();
	}
	
	public Range(int maxSize) {
		this(maxSize, -1);
	}
	
	public void add(Event event) {
				
		if (maxAge >= 0) {
			long currentTime = System.currentTimeMillis();
			long obsoleteTime = currentTime - maxAge;
			
			if (event.getPushTime() < obsoleteTime) {
				return;
			}
			
			cleanUp(currentTime);
		}

		boolean inserted = false;
		
		if (events.size() > 0) {
			for (ListIterator<Event> it = events.listIterator(); it.hasNext();) {
				Event e = it.next();
				if (e.getPushTime() > event.getPushTime()) {
					continue;
				}
				else {
					it.add(event);
					inserted = true;
					break;
				}
			}
		}
		
		if (!inserted) {
			events.add(event);
		}
		
		if (events.size() > maxSize) {
			events.remove(0);
		}
	}
	
	public void cleanUp() {
		if (maxAge >= 0) {
			long currentTime = System.currentTimeMillis();
			cleanUp(currentTime);
		}
	}
	
	private void cleanUp(long currentTime) {
		assert maxAge >= 0;
		
		long obsoleteTime = currentTime - maxAge;
		
		for (ListIterator<Event> it = events.listIterator(events.size()); it.hasPrevious();) {
			Event e = it.previous();
			if (e.getPushTime() < obsoleteTime) {
				it.remove();
			}
			else {
				break;
			}
		}
	}
	
	public void clear() {
		events.clear();
	}
	
	public Event removeOldest() {
		return events.remove(events.size() - 1);
	}
	
	public int getSize() {
		return events.size();
	}
}
