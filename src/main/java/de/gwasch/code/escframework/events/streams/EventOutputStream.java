package de.gwasch.code.escframework.events.streams;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

// todo, EventOutputStream sollte selbst ein Processor sein, damit er als Einsprungspunkt dienen kann
public class EventOutputStream<E extends Event> {

	private Processor<Event> processor;
	
	public EventOutputStream(Processor<Event> processor) {
		this.processor = processor;
	}
	
//	public Processor<Event> getProcessor() {
//		return processor;
//	}
//	
//	public void setProcessor(Processor<Event> processor) {
//		this.processor = processor;
//	}
	
	//todo, Exception werfen, wenn fehlgeschlagen
	public void writeEvent(E event) {
		processor.process(event);
	}
}
