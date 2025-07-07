package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public interface PostEventListener {
	boolean onProcess(Object source, Event event, boolean consumed, Object returnValue, Throwable exception);
}
