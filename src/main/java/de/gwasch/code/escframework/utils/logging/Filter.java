package de.gwasch.code.escframework.utils.logging;


public class Filter implements Cloneable {
	
	public String pattern;
	public boolean allow;
	public boolean deactivated;
	
	public Filter() {
	}
	
	public Filter(String pattern, boolean allow, boolean deactivated) {
		this.pattern = pattern;
		this.allow = allow;
		this.deactivated = deactivated;
	}
	
	public Filter clone() {
		Filter clone = new Filter(pattern, allow, deactivated);
		return clone;
	}
	
	public boolean equals(Object obj) {
		Filter cmp = (Filter)obj;
		
		return pattern.equals(cmp.pattern)
			&& allow == cmp.allow
			&& deactivated == cmp.deactivated;
	}
}
