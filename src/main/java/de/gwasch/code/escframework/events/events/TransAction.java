package de.gwasch.code.escframework.events.events;

import java.util.ArrayList;
import java.util.List;


public class TransAction extends AbstractAction {
		
	private List<Action> actions;
	
	public TransAction() {
		actions = new ArrayList<>();
	}
	
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public boolean execute() {

		for (int i = 0; i < actions.size(); i++) {
			
			if (!actions.get(i).execute()) {
				
				for (; i >= 0; i--) {
					actions.get(i).getUndoAction().execute();
				}
				
				return false;
			}
		}
		
		return true;
	}
}