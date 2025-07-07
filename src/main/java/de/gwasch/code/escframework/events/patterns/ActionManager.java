package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public interface ActionManager {
	Object invoke(Object service, Event event);
}
