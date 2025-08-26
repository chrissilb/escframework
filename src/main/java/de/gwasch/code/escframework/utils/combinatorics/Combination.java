package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

/**
 * Provides a combination of integers. For a combination the order of values is
 * not relevant.
 */
public class Combination extends CoBase {

	/**
	 * Constructs a {@code Combination} instance.
	 * 
	 * @param n      Number of values
	 * @param k      Number of selected values
	 * @param offset Defines the first value. All other values are successively
	 *               incremented by one.
	 * @param withReplacement Defines if the drawing is with replacement.
	 */
	public Combination(int n, int k, int offset, boolean withReplacement) {
		super(n, k, offset, withReplacement);
	}

	/**
	 * Constructs a {@code Combination} instance. The offset (first value) is 0.
	 * 
	 * @param n Number of values
	 * @param k Number of selected values
	 * @param withReplacement Defines if the drawing is with replacement.
	 */
	public Combination(int n, int k, boolean withReplacement) {
		super(n, k, withReplacement);
	}

	/**
	 * Returns the count of possible selected value combinations.
	 * 
	 * @return the count of possible value combinations
	 */
	public int getCount() {
		
		if (withReplacement) {
			return Statistics.binomial(n + k - 1, k);
		}
		else {
			return Statistics.binomial(n, k);
		}
	}

	/**
	 * Returns an {@code Iterator} for all possible combinations.
	 * 
	 * @return an {@code Iterator} for all possible combinations
	 */
	public Iterator<int[]> iterator() {

		if (withReplacement) {
			return new CombinationRepIterator();
		}
		else {
			return new CombinationIterator();
		}
	}

	private class CombinationIterator extends CoIterator {

		protected boolean doNext() {

			for (int i = list.length - 1; i >= 0; i--) {

				if (list[i] < n - (list.length - i)) {

					list[i]++;

					for (int j = i + 1; j < list.length; j++) {
						list[j] = list[j - 1] + 1;
					}

					break;
				} else if (i == 0) {
					list = null;
					return false;
				}
			}

			return true;
		}
	}
	
	private class CombinationRepIterator extends CoIterator {

		protected boolean doNext() {

			for (int i = list.length - 1; i >= 0; i--) {

				if (list[i] < n - 1) {

					list[i]++;

					for (int j = i + 1; j < list.length; j++) {
						list[j] = list[j - 1];
					}

					break;
				} else if (i == 0) {
					list = null;
					return false;
				}
			}

			return true;
		}
	}
}
