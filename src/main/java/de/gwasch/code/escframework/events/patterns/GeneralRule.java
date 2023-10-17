package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;

/**
 * Standard implementation of the {@code Rule} interface
 */

public class GeneralRule extends Rule {

	private TriggerEventControl tec;
	
	/**
	 * Constructs a {@code GeneralRule} and registers {@link EventControl}s. They become registered like this:
	 * <pre>
	 * addEventControl(new ControlEventControl(this, "deactivate", true, new RuleEventListener() {
	 *     public void onEvent() { onDeactivateEvent(); }
	 *     public boolean accept(RuleMode mode) { return mode != RuleMode.INACTIVE; }}));
	 * addEventControl(new ControlEventControl(this, "suspend", true, new RuleEventListener() {
	 *     public void onEvent() { onSuspendEvent(); }
	 *     public boolean accept(RuleMode mode) { return mode != RuleMode.INACTIVE &amp;&amp; mode != RuleMode.SUSPENDED; }}));
	 * addEventControl(tec = new TriggerEventControl(this, "trigger", false, new RuleEventListener() {
	 *     public void onEvent() { onTriggerEvent(); }
	 *     public boolean accept(RuleMode mode) { return mode == RuleMode.ACTIVE; }}));
	 * addEventControl(new ControlEventControl(this, "activate", true, new RuleEventListener() {
	 *     public void onEvent() { onActivateEvent(); }
	 *     public boolean accept(RuleMode mode) { return mode == RuleMode.INACTIVE; }}));
	 * addEventControl(new ControlEventControl(this, "resume", true, new RuleEventListener() {
	 *     public void onEvent() { onResumeEvent(); }
	 *     public boolean accept(RuleMode mode) { return mode == RuleMode.SUSPENDED; }}));
	 * </pre>
	 */
	public GeneralRule() {
		
		addPatternEventControl(new ControlEventControl(this, "deactivate", true, new RuleEventListener() { 
			public void onEvent() { onDeactivateEvent(); } 
			public boolean accept(RuleMode mode) { return mode != RuleMode.INACTIVE; }}));
		addPatternEventControl(new ControlEventControl(this, "suspend", true, new RuleEventListener() { 
			public void onEvent() { onSuspendEvent(); }
			public boolean accept(RuleMode mode) { return mode != RuleMode.INACTIVE && mode != RuleMode.SUSPENDED; }}));
		addPatternEventControl(tec = new TriggerEventControl(this, "trigger", false, new RuleEventListener() { 
			public void onEvent() { onTriggerEvent(); }
			public boolean accept(RuleMode mode) { return mode == RuleMode.ACTIVE; }}));		
		addPatternEventControl(new ControlEventControl(this, "activate", true, new RuleEventListener() { 
			public void onEvent() { onActivateEvent(); }
			public boolean accept(RuleMode mode) { return mode == RuleMode.INACTIVE; }}));
		addPatternEventControl(new ControlEventControl(this, "resume", true, new RuleEventListener() { 
			public void onEvent() { onResumeEvent(); }
			public boolean accept(RuleMode mode) { return mode == RuleMode.SUSPENDED; }}));
	}
	
	public void onActivateEvent() {
		
		assert getRuleMode() == RuleMode.INACTIVE;
		assert getActionCount() == 0;				
		
		assert getActionEventState() != null;

		setRuleMode(RuleMode.ACTIVE);
		
		boolean registerTriggerEvent = false;
		
		Range range = null;
		
		if (getTriggerInterval() > 0 || getMaxTriggerCount() >= 0) {
			range = new Range(getMaxTriggerCount() + 1, getTriggerInterval());
			
			switch(getRangeCondition()) {
				case EQ_AFTER:
				case NE_AFTER:
				case LE_AFTER:
				case MT_AFTER: {
					setTriggerIntervalEvent(new TriggerIntervalEvent(this));
					getPatternMatcher().getProcessor().process(getTriggerIntervalEvent());
					break;
				}
				case ONCE_EQ:
				case ONCE_MT: 
					registerTriggerEvent = true;
					break;
				case ONCE_NE: {					
					setTriggerIntervalEvent(new TriggerIntervalEvent(this));
					getPatternMatcher().getProcessor().process(getTriggerIntervalEvent());
					registerTriggerEvent = true;
					break;
				}
				default:
					//todo, individuelle condition prüfen
					break;
			}
		}
		else {
			registerTriggerEvent = true;
		}
		
		if (getPatternEventControl("trigger").getPatternEvent() != null) {
			tec.setRange(range);
			tec.setRegisterTriggerEvent(registerTriggerEvent);
		}
		
		if (implicitFirstTrigger()) {
			onTriggerEvent();
		}
	}

	
	public void onTriggerEvent() {
		
//		if (getRuleMode() != RuleMode.ACTIVE) {
//			System.out.println("GeneralRule.onTriggerEvent()");
//		}
				
		assert getRuleMode() == RuleMode.ACTIVE;

		if (tec.getRange() != null) {
			switch(getRangeCondition()) {
				case EQ_AFTER:
				case NE_AFTER:
				case LE_AFTER:
				case MT_AFTER:
					break;
				case ONCE_EQ:
					if (tec.getRange().getSize() == getMaxTriggerCount()) {
						doAction();
					}
					break;
				case ONCE_NE:
				case ONCE_MT:
					if (tec.getRange().getSize() > getMaxTriggerCount()) {
						doAction();
					}
					break;
				default:
					break;
			}
		}
		else {
			doAction();
		}
	}
		
	public void onTriggerIntervalEvent() {
				
		if (getRuleMode() != RuleMode.ACTIVE) {
			System.out.println("GeneralRule: hier");
		}
		assert getRuleMode() == RuleMode.ACTIVE;
		
		setTriggerIntervalEvent(null);

		if (tec.getRange() != null) {
			switch(getRangeCondition()) {
			case EQ_AFTER:
				if (tec.getRange().getSize() == getMaxTriggerCount()) {
					doAction();
				}
				break;
				
			case NE_AFTER:
			case ONCE_NE:
				if (tec.getRange().getSize() != getMaxTriggerCount()) {
					doAction();
				}
				break;				
			case LE_AFTER:
				Event oldestEvent = null;
				boolean actionNeeded = false;
				
				if (tec.getRange().getSize() <= getMaxTriggerCount()) {
					tec.getRange().clear();
					actionNeeded = true;
				}
				else {
					oldestEvent = tec.getRange().removeOldest();
				}
				
				setTriggerIntervalEvent(new TriggerIntervalEvent(this, oldestEvent));
				getPatternMatcher().getProcessor().process(getTriggerIntervalEvent());

				//NOTE: doAction() must be called after sending new triggerIntervalEvent because it might cancel it again.
				if (actionNeeded) {
					doAction();
				}
				break;	
				
			case MT_AFTER:
				if (tec.getRange().getSize() > getMaxTriggerCount()) {
					doAction();
				}
				break;	
				
			case ONCE_EQ:
			case ONCE_MT:
				break;
				
			default:
				break;
			}
		}
		else {
			doAction();
		}
	}

	private void doAction() {
		if (getActionInterval() > 0) {
			setActionIntervalEvent(new ActionIntervalEvent(this));
			getPatternMatcher().getProcessor().process(getActionIntervalEvent());
		}
		else {
			onActionIntervalEvent();
		}
	}
	
	public void onActionIntervalEvent() {
				
		assert getRuleMode() == RuleMode.ACTIVE;
		
		setActionIntervalEvent(null);
		
		incrementActionCount();
				
		if (getMaxActionCount() > -1 && getActionCount() == getMaxActionCount()) {
			onDeactivateEvent();
		}
		else if (getActionFinishEvent() != null) {
			setRuleMode(RuleMode.PROCESSING_ACTION);
			getPatternMatcher().registerActionFinishEvent(this);
		}

		Event actionEvent = getActionEvent();		
		processAction(actionEvent);
	}
	
	//NOTE: method can be overridden to decide if or if no actionEvent shall be sent.
	protected void processAction(Event actionEvent) {
		assert actionEvent != null;
		getPatternMatcher().getProcessor().process(actionEvent);
	}
	
	public void onActionFinishEvent() {
		getPatternMatcher().unregisterActionFinishEvent(this);
		setRuleMode(RuleMode.ACTIVE);
		
		assert getTriggerIntervalEvent() == null;
		assert getActionIntervalEvent() == null;
		
		if (getPatternEventControl("trigger").getPatternEvent() == getActionEvent()) {
			onTriggerEvent();
		}
	}
	
	
	public void onSuspendEvent() {
		setRuleMode(RuleMode.SUSPENDED);
		
		if (getTriggerIntervalEvent() != null) {
			getPatternMatcher().getProcessor().suspend(new SuspendEvent(getTriggerIntervalEvent()));

		}
		
		if (getActionIntervalEvent() != null) {
			getPatternMatcher().getProcessor().suspend(new SuspendEvent(getActionIntervalEvent()));
		}
		
		if (getActionFinishEvent() != null) {
			getPatternMatcher().unregisterActionFinishEvent(this);
		}
		
		setLastEvent(null);
	}
	
	public void onResumeEvent() {
		setRuleMode(RuleMode.ACTIVE);
		
		if (getTriggerIntervalEvent() != null) {
			getPatternMatcher().getProcessor().resume(new ResumeEvent(getTriggerIntervalEvent()));

		}
		
		if (getActionIntervalEvent() != null) {
			getPatternMatcher().getProcessor().resume(new ResumeEvent(getActionIntervalEvent()));
		}
		else if (tec.getPatternEvent() == getActionEvent()) {
			onTriggerEvent();
		}
	}
	
	public void onDeactivateEvent() {		

		setRuleMode(RuleMode.INACTIVE);
				
		setActionCount(0);
		
		if (getTriggerIntervalEvent() != null) {
			getPatternMatcher().getProcessor().cancel(new CancelEvent(getTriggerIntervalEvent()));
			setTriggerIntervalEvent(null);
		}
		
		if (getActionIntervalEvent() != null) {
			getPatternMatcher().getProcessor().cancel(new CancelEvent(getActionIntervalEvent()));
			setActionIntervalEvent(null);
		}
		
		if (getActionFinishEvent() != null) {
			getPatternMatcher().unregisterActionFinishEvent(this);
		}
		
		setLastEvent(null);
	}
}
