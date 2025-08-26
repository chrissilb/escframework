package de.gwasch.code.escframework.utils.combinatorics;


/**
 * Provides helper functions for statistics.
 */
public class Statistics {

	/**
	 * Calculates the binomial coefficient.
	 * @param n Number of values
	 * @param k Number of selected values
	 * @return The binomial coefficient
	 */
	public static int binomial(int n, int k) {		
		int val = 1;

		if (n - k < k) k = n - k;
		
		boolean[] divs = new boolean[k];
		
		for (int i = 1; i < k; i++) {			
			divs[i] = true;	
		}	
		
		for (int i = n - k + 1; i <= n; i++) {
			val *= i;
			
			for (int j = 1; j < k; j++) {
				if (divs[j] && val % (j + 1) == 0) {
					val /= (j + 1);
					divs[j] = false;
				}
			}	
		}
		
		return val;	
	}

	/**
	 * Calculates the faculty of {@code n}.
	 * @param n The base number to calculate its faculty
	 * @return The faculty
	 */
	public static int fac(int n) {
		
		int val = 1;
		
		for (int i = 2; i <= n; i++) {
			val *= i;
		}
		
		return val;
	}
}
