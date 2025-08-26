package de.gwasch.code.escframework.utils.logging;

/**
 * A {@code Filter} decides if a message is accepted or not. 
 */
public class Filter implements Cloneable {

	private String pattern;
	private boolean allow;
	private boolean deactivated;

	/**
	 * Creates a filter
	 * 
	 * @param pattern     a regular expression (see {@link String#matches(String)}
	 * @param allow       true if a matched filter leads to acceptance, otherwise false.
	 * @param deactivated if true the filter is ignored
	 */
	public Filter(String pattern, boolean allow, boolean deactivated) {
		this.pattern = pattern;
		this.allow = allow;
		this.deactivated = deactivated;
	}

	/**
	 * Returns the filter pattern using regular expressions, see
	 * {@link String#matches(String)}
	 * 
	 * @return the filter pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the filter pattern
	 * 
	 * @param pattern the filter pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Returns if a matching pattern leads to an acceptance.
	 * 
	 * @return if a matching pattern leads to an acceptance
	 */
	public boolean getAllow() {
		return allow;
	}

	/**
	 * Sets if a matching pattern leads to an acceptance.
	 * 
	 * @param allow if a matching pattern leads to an acceptance
	 */
	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	/**
	 * Returns if a filter is deactivated/ignored.
	 * @return if a filter is deactivated/ignored
	 */
	public boolean getDeactivated() {
		return deactivated;
	}

	/**
	 * Activates/deactivates a filter
	 * 
	 * @param deactivated {@code true} to deactivate, {@code false} to activate
	 */
	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}

	/**
	 * Clones a {@code Filter}.
	 */
	public Filter clone() {
		Filter clone = new Filter(pattern, allow, deactivated);
		return clone;
	}

	public boolean equals(Object obj) {
		Filter cmp = (Filter) obj;

		return pattern.equals(cmp.pattern) && allow == cmp.allow && deactivated == cmp.deactivated;
	}
}
