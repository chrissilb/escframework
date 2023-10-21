package de.gwasch.code.escframework.utils.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@code Filters} contains a list of type {@link Filter}.
 */
public class Filters implements Iterable<Filter>, Cloneable {

	private List<Filter> filters;

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

	public void clear() {
		filters.clear();
	}

	public boolean accept(String s) {
		s = s.trim();

		for (Filter filter : filters) {
			if (filter.getDeactivated())
				continue;

			if (s.matches(filter.getPattern())) {
				if (filter.getAllow()) {
					return true;
				} else {
					return false;
				}
			}
		}

		return true;
	}

	public void add(String pattern, boolean allow, boolean deactivated) {
		filters.add(new Filter(pattern, allow, deactivated));
	}

	public void add(String pattern, boolean allow) {
		filters.add(new Filter(pattern, allow, false));
	}

	public void insert(int index, String pattern, boolean allow, boolean deactivated) {
		filters.add(index, new Filter(pattern, allow, deactivated));
	}

	public void insert(int index, String pattern, boolean allow) {
		filters.add(index, new Filter(pattern, allow, false));
	}

	public void insert(int index, Filter filter) {
		filters.add(index, filter);
	}

	public Filter get(int index) {
		return filters.get(index);
	}

	public void remove(int index) {
		filters.remove(index);
	}

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