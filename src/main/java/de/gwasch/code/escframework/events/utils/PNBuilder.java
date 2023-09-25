package de.gwasch.code.escframework.events.utils;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

public class PNBuilder<E extends Event> {

	private String name;
	private Processor<E> top;
	private Processor<E> bottom;
			
	public PNBuilder() {
		this("");
	}

	public PNBuilder(String name) {
		this.name = name;
		
		top = null;
		bottom = null;
	}
	
	public PNBuilder<E> add(Processor<E> processor) {
				
		if (processor.getName().length() == 0) {
			processor.setName(name);
		}
		
		if (top == null) {
			top = processor;
		}
		else {
			bottom.addSuccessor(processor);
		}
		
		bottom = processor;

		return this;
	}
	
	public PNBuilder<E> add(Processor<E> predecessor, Processor<E> processor) {
		bottom = predecessor;
		add(processor);
		return this;
	}
	
	public Processor<E> top() {
		return top;
	}
}
