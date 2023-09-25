package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

public abstract class CoBase implements Iterable<int[]> {

	protected int offset;
	protected int n;
	protected int k;
	protected int[] list;
	protected boolean withRepetitions;

	public CoBase(int n, int k, int offset) {
		
		if (n <= 0) throw new RuntimeException("Bad Call: n must be > 0"); 
		if (k <= 0) throw new RuntimeException("Bad Call: k must be > 0"); 
		if (k > n) throw new RuntimeException("Bad Call: n must be >= k");
		
		this.n = n;
		this.k = k;
		this.offset = offset;
		withRepetitions = true;
	}
	
	public CoBase(int n, int k) {
		this(n, k, 1);
	}
	
	public boolean withRepetitions() {
		return withRepetitions;
	}
	
	public void setWithRepetitions(boolean repetitions) {
		withRepetitions = repetitions;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getN() {
		return n;
	}
	
	public int getK() {
		return k;
	}
	
	public abstract int getCount();
	public abstract Iterator<int[]> iterator();
	
	protected abstract class CoIterator implements Iterator<int[]> {

		private boolean hasLookup;
		
		protected CoIterator() {
			hasLookup = true;
			
			list = new int[k];

		}
		
		public boolean hasNext() {
			if (!hasLookup) {
				hasLookup = doNext();
			}
			
			return hasLookup;
		}

		public int[] next() {
			if (hasLookup) {
				hasLookup = false;
			}
			else {
				doNext();
			}
			
			return list;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		protected abstract boolean doNext();
	}
}

