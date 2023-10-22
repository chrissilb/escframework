package de.gwasch.code.escframework.utils.logging;

/**
 * A {@code Filter} is used to filter log messages. It contains a pattern which
 * indicates a regular expression (see {@link String#matches(String)}. If
 * {@link #getAllow()} returns true a match deactivates filtering; otherwise a
 * match activates filtering. All that is ignored if {@link #getDeactivated()}
 * returns true. Then the filter is ignored.
 */
public class Filter implements Cloneable {

	private String pattern;
	private boolean allow;
	private boolean deactivated;

	public Filter(String pattern, boolean allow, boolean deactivated) {
		this.pattern = pattern;
		this.allow = allow;
		this.deactivated = deactivated;
	}

	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean getAllow() {
		return allow;
	}
	
	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	public boolean getDeactivated() {
		return deactivated;
	}
	
	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}

	public Filter clone() {
		Filter clone = new Filter(pattern, allow, deactivated);
		return clone;
	}

	public boolean equals(Object obj) {
		Filter cmp = (Filter) obj;

		return pattern.equals(cmp.pattern) && allow == cmp.allow && deactivated == cmp.deactivated;
	}
}
