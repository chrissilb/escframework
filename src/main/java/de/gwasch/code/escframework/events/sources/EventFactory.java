package de.gwasch.code.escframework.events.sources;

import de.gwasch.code.escframework.events.events.Event;

public interface EventFactory<E extends Event> {

	E create();
}
