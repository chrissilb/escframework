package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

public class VariationWithRepetitions extends CoBase {

	public VariationWithRepetitions(int n, int k) {
		super(n, k);
	}
	
	public VariationWithRepetitions(int n, int k, int offset) {
		super(n, k, offset);
	}
	
	public int getCount() {
		int cnt = (int)Math.pow(n, k);
		return cnt;
	}
	
	public Iterator<int[]> iterator() {
		return new VariationIterator();
	}
	
	class VariationIterator extends CoIterator {
		
		VariationIterator() {
			for (int i = 0; i < list.length; i++) {
				list[i] = 0;
			}
		}
		
		protected boolean doNext() {
			
			for (int i = list.length - 1; i >= 0; i--) {
				
				list[i] = (list[i] + 1) % n;
				
				if (list[i] != 0) {
					return true;
				}
			}
			
			return false;
		}
	}
}
