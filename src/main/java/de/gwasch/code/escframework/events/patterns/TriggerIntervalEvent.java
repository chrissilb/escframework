package de.gwasch.code.escframework.events.patterns;

import java.util.Random;

import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.utils.TimeUtil;

/**
 * A {@code TriggerIntervalEvent} is used by a {@link Rule} to control events of a {@link Range}.
 * The {@code Rule} of the {@code TriggerIntervalEvent} gets feedback once it is processed.
 * This happens via {@link Rule#onTriggerIntervalEvent()}.
 * <p>
 * In a {@code Rule} class an {@code TriggerIntervalEvent} can be sent like this, for example:
 * <pre>
 *     setTriggerIntervalEvent(new TriggerIntervalEvent(this, oldestEvent));
 *     getPatternMatcher().getProcessor().process(getTriggerIntervalEvent());
 * </pre>
 * It is good practice to store the event via {@link Rule#setTriggerIntervalEvent(TriggerIntervalEvent)}
 * to be able to cancel or suspend it if needed. 
 */
public class TriggerIntervalEvent extends AbstractEvent {
	
	private Rule rule;
	
	/**
	 * Constructs the {@code TriggerIntervalEvent} based a {@link Rule}. It sets its {@code pushTime}.
	 * The {@code pushTime} is set to {@code offset} + {@code interval}. The {@code offset} is the
	 * {@code pushTime} of the {@code offsetEvent} if not {@code null} or the current time.
	 * The {@code interval} is {@link Rule#getTriggerInterval()} adjust by a random number and
	 * {@link Rule#getMaxTriggerDeviationFactor()}.
	 *  
	 * @param rule the {@code Rule}
	 * @param offsetEvent is used as base push time. If this is {@code null} the current time is the offset time. 
	 */
	public TriggerIntervalEvent(Rule rule, Event offsetEvent) {
		this.rule = rule;

		int interval = rule.getTriggerInterval();
		
		if (rule.getMaxTriggerDeviationFactor() != 0) {
			Random random = new Random();		
			double r = random.nextDouble();
			interval = (int)(interval - rule.getMaxTriggerDeviationFactor() * interval
					+ 2 * r * rule.getMaxTriggerDeviationFactor() * interval);
		}
		
		if (offsetEvent != null) {
			setPushTime(offsetEvent.getPushTime() + interval);
		}
		else {
			TimeUtil.setPushTime(this, interval);
		}
	}
	
	public TriggerIntervalEvent(Rule rule) {
		this(rule, null);
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public String toString() {
		return super.toString() + ": " + rule.toString();
	}
}
