package de.gwasch.code.escframework.utils.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@code Filters} contains a (ordered) list of type {@link Filter}.
 */
public class Filters implements Iterable<Filter>, Cloneable {

	private List<Filter> filters;

	/**
	 * Creates a container of filters.
	 */
	public Filters() {
		filters = new ArrayList<Filter>();
	}

	public Filters clone() {

		Filters clone = new Filters();
		for (Filter f : filters) {
			clone.filters.add(f.clone());
		}

		return clone;
	}

	/**
	 * Removes all filters.
	 */
	public void clear() {
		filters.clear();
	}

	/**
	 * Checks if a message is accepted based on the filters. The filters are defined
	 * in a specific order. So the first found active matching filter decides.
	 * 
	 * @param message the message
	 * @return if accepted
	 */
	public boolean accept(String message) {
		message = message.trim();

		for (Filter filter : filters) {
			if (filter.getDeactivated())
				continue;

			if (message.matches(filter.getPattern())) {
				if (filter.getAllow()) {
					return true;
				} else {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Adds a {@link Filter}.
	 * 
	 * @param pattern     a regular expression (see {@link String#matches(String)}
	 * @param allow       true if a matched filter leads to acceptance, otherwise
	 *                    false.
	 * @param deactivated if true the filter is ignored
	 */
	public void add(String pattern, boolean allow, boolean deactivated) {
		filters.add(new Filter(pattern, allow, deactivated));
	}

	/**
	 * Adds an active {@link Filter}.
	 * 
	 * @param pattern a regular expression (see {@link String#matches(String)}
	 * @param allow   true if a matched filter leads to acceptance, otherwise false.
	 */
	public void add(String pattern, boolean allow) {
		add(pattern, allow, false);
	}

	/**
	 * Inserts a {@link Filter} a at certain position.
	 * 
	 * @param index       the position of the index. First index is 0.
	 * @param pattern     a regular expression (see {@link String#matches(String)}
	 * @param allow       true if a matched filter leads to acceptance, otherwise
	 *                    false.
	 * @param deactivated if true the filter is ignored
	 */
	public void insert(int index, String pattern, boolean allow, boolean deactivated) {
		filters.add(index, new Filter(pattern, allow, deactivated));
	}

	/**
	 * Inserts an active {@link Filter} a at certain position.
	 * 
	 * @param index   the position of the index. First index is 0.
	 * @param pattern a regular expression (see {@link String#matches(String)}
	 * @param allow   true if a matched filter leads to acceptance, otherwise false.
	 */
	public void insert(int index, String pattern, boolean allow) {
		insert(index, pattern, allow, false);
	}

	/**
	 * Inserts a {@link Filter} a at certain position.
	 * 
	 * @param index  the position of the index. First index is 0.
	 * @param filter the filter
	 */
	public void insert(int index, Filter filter) {
		filters.add(index, filter);
	}

	/**
	 * Returns a filter at certain position.
	 * 
	 * @param index the position
	 * @return the filter
	 */
	public Filter get(int index) {
		return filters.get(index);
	}

	/**
	 * Removes a filter at a certain position.
	 * 
	 * @param index the position
	 */
	public void remove(int index) {
		filters.remove(index);
	}

	/**
	 * Returns the amount of filters.
	 * 
	 * @return the amount of filters
	 */
	public int size() {
		return filters.size();
	}

	public Iterator<Filter> iterator() {
		return filters.iterator();
	}

	public boolean equals(Object obj) {
		Filters cmp = (Filters) obj;
		return filters.equals(cmp.filters);
	}
}