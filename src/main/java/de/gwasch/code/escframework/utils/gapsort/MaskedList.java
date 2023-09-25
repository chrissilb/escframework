package de.gwasch.code.escframework.utils.gapsort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MaskedList<E> implements List<E> {

	List<E> base;
	int[] mapping;
	
	public MaskedList(List<E> base) {
		this.base = base;
		mapping = null;
	}
	
	public void setMapping(int[] mapping) {
		this.mapping = mapping;
	}
	
	public void dissolveMapping() {
		
		List<E> clone = new ArrayList<E>();
		
		for (int i = 0; i < base.size(); i++) {
			clone.add(base.get(i));
		}
		
		for (int i = 0; i < mapping.length; i++) {
			base.set(i, clone.get(mapping[i]));
		}
	}
	
	public int size() {
		return base.size();
	}

	public boolean isEmpty() {
		return base.isEmpty();
	}

	public boolean contains(Object o) {
		return base.contains(o);
	}

	public Iterator<E> iterator() {
		return base.iterator();
	}

	public Object[] toArray() {
		return base.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return base.toArray(a);
	}

	public boolean add(E e) {
		return base.add(e);
	}

	public boolean remove(Object o) {
		return base.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return base.containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		return base.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return base.addAll(mapping[index], c);
	}

	public boolean removeAll(Collection<?> c) {
		return base.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return base.retainAll(c);
	}

	public void clear() {
		base.clear();
	}

	public E get(int index) {
		return base.get(mapping[index]);
	}

	public E set(int index, E element) {
		return base.set(mapping[index], element);
	}

	public void add(int index, E element) {
		base.add(mapping[index], element);
	}

	public E remove(int index) {
		return base.remove(mapping[index]);
	}

	public int indexOf(Object o) {
		return mapping[base.indexOf(o)];
	}

	public int lastIndexOf(Object o) {
		return  mapping[base.lastIndexOf(o)];
	}

	public ListIterator<E> listIterator() {
		return base.listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return base.listIterator(mapping[index]);
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return base.subList(mapping[fromIndex], mapping[toIndex]);
	}
}
