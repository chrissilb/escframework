package de.gwasch.code.escframework.events.events;

public abstract class AbstractPushAction extends AbstractAction implements PushAction {

	private Action action;
	
	public AbstractPushAction(Action action) {
		this.action = action;
	}
	
	public abstract AbstractPushAction clone();
	
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append(": ");
		sb.append(action);
		
		return sb.toString();
	}
}
