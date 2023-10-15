package de.gwasch.code.escframework.events.patterns;

import java.util.Random;

import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.events.utils.TimeUtil;

/**
 * An {@code ActionIntervalEvent} is used by a {@link Rule} to delay processing an {@code Rule.actionEvent}.
 * The {@code Rule} of the {@code ActionIntervalEvent} gets feedback once it is processed.
 * This happens via {@link Rule#onActionIntervalEvent()}.
 * <p>
 * In a {@code Rule} class an {@code ActionIntervalEvent} can be sent like this, for example:
 * <pre>
 *     setActionIntervalEvent(new ActionIntervalEvent(this));
 *     getPatternMatcher().getProcessor().process(getActionIntervalEvent());
 * </pre>
 * It is good practice to store the event via {@link Rule#setActionIntervalEvent(ActionIntervalEvent)}
 * to be able to cancel or suspend it if needed. 
 */
public class ActionIntervalEvent extends AbstractEvent {
	
	private Rule rule;
	
	/**
	 * Constructs the {@code ActionIntervalEvent} based a {@link Rule}. It sets its {@code pushTime}.
	 * The {@code pushTime} is set to {@code offset} + {@code interval}. The {@code offset} is the
	 * {@code pushTime} of the last event ({@link Rule#getLastEvent()} if existent or the current time.
	 * The {@code interval} is {@link Rule#getActionInterval()} adjust by a random number and
	 * {@link Rule#getMaxActionDeviationFactor()}.
	 * <p>
	 * If {@link Rule#firstActionImmediately()} is {@code true} and {link Rule#getActionCount()} is {@code 0}
	 * this value is adjusted by the {@code interval} so that {@code pushTime} is in "future" just by
	 * the effect of the deviation factor. 
	 *  
	 * @param rule the {@code Rule}
	 */
	public ActionIntervalEvent(Rule rule) {
		this.rule = rule;
		
		int interval = rule.getActionInterval();
		
		if (rule.getMaxActionDeviationFactor() != 0) {
			Random random = new Random();		
			double r = random.nextDouble();
			interval = (int)(interval - rule.getMaxActionDeviationFactor() * interval
					+ 2 * r * rule.getMaxActionDeviationFactor() * interval);
		}		

		if (rule.getActionCount() == 0 && rule.firstActionImmediately()) {
			interval = interval - rule.getActionInterval();
			if (interval < 0) {
				interval = interval * -1;
			}
		}

		if (rule.getLastEvent() != null) {
			setPushTime(rule.getLastEvent().getPushTime() + interval);
		}
		else {
			System.out.println("ActionIntervalEvent: hier");
			TimeUtil.setPushTime(this, interval);
		}
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public String toString() {
		return super.toString() + ": " + rule.toString();
	}
}
