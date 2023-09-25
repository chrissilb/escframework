package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;


public class Combination extends CoBase {
	
	public Combination(int n, int k) {
		super(n, k);
	}
	
	public Combination(int n, int k, int offset) {
		super(n, k, offset);
	}
	
	public int getCount() {
		return Statistics.binomial(n, k);
	}
	
	public Iterator<int[]> iterator() {
		return new CombinationIterator();
	}
	
	class CombinationIterator extends CoIterator {
		
		CombinationIterator() {
			for (int i = 0; i < list.length; i++) {
				list[i] = offset + i;
			}
		}
		
		protected boolean doNext() {
			
			for (int i = list.length - 1; i >= 0; i--) {
				
				if (list[i] < n - list.length + i + offset) {
					
					list[i]++;
					
					for (int j = i + 1; j < list.length; j++) {
						list[j] = list[j - 1] + 1;
					}
					
					break;
				}
				else if (i == 0) {
					list = null;
					return false;
				}
			}
			
			return true;
		}
	}
}
