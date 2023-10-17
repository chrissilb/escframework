package de.gwasch.code.escframework.events.patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code EventControlType} contains a list of {@link EventControl}s. A
 * {@link PatternMatcher} distributes incoming events ordered by
 * {@code EventControlType}.
 */

public class EventControlType {

	private String name;
	private List<EventControl> eventControls;

	public EventControlType(String name) {
		this.name = name;
		eventControls = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void addEventControl(EventControl eventControl) {
		eventControls.add(eventControl);
	}

	public void removeEventControl(EventControl eventControl) {
		eventControls.remove(eventControl);
	}

	public List<EventControl> getEventControls() {
		return eventControls;
	}

	public String toString() {
		return name;
	}
}
