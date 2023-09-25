package de.gwasch.code.escframework.events.sources;

import de.gwasch.code.escframework.events.events.Event;

public interface EventSource<E extends Event> {

	E pop();
}
