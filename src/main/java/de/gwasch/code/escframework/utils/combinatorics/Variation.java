package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

public class Variation extends CoBase {

	public Variation(int n, int k) {
		super(n, k);
	}
	
	public Variation(int n, int k, int offset) {
		super(n, k, offset);
	}
	
	public int getCount() {
		int cnt = 1;
		
		for (int i = n - k + 1; i <= n; i++) {
			cnt *= i;
		}
		
		return cnt;
	}
	
	public Iterator<int[]> iterator() {
		return new VariationIterator();
	}
	
	class VariationIterator extends CoIterator {
		
		VariationIterator() {
			for (int i = 0; i < list.length; i++) {
				list[i] = offset + i;
			}
		}
		
		protected boolean doNext() {
			
			for (int i = list.length - 1; i >= 0; i--) {

				boolean exists = false;

				for (list[i]++; list[i] < n + offset; list[i]++) {
					
					for (int k = 0; k < i; k++) {
						
						if (list[i] == list[k]) {
							exists = true;
							break;
						}
					}
				
					if (!exists) {
						if (i < list.length - 1) { 
							
							list[i + 1] = offset - 1;
							i = i + 2;	
							break; 
						}
						else {
							return true;
						}
					}
					else {
						exists = false;
					}
				}
			}

			list = null;	
			return false;
		}
	}
}
