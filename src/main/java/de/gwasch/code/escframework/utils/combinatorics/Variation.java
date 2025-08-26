package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

/**
 * Provides a variation of integers. For a variation the order of values is
 * relevant.
 * 
 * NOTE: A 'Permutation' is Variation without repetition where n equals k.
 */
public class Variation extends CoBase {

	/**
	 * Constructs a {@code Variation} instance.
	 * 
	 * @param n               Number of values
	 * @param k               Number of selected values
	 * @param offset          Defines the first value. All other values are
	 *                        successively incremented by one.
	 * @param withReplacement Defines if the drawing is with replacement.
	 */
	public Variation(int n, int k, int offset, boolean withReplacement) {
		super(n, k, offset, withReplacement);
	}

	/**
	 * Constructs a {@code Variation} instance. The offset (first value) is 0.
	 * 
	 * @param n Number of values
	 * @param k Number of selected values
	 * @param withReplacement Defines if the drawing is with replacement.
	 */
	public Variation(int n, int k, boolean withReplacement) {
		super(n, k, withReplacement);
	}

	/**
	 * Returns the count of possible selected value variations.
	 * 
	 * @return the count of possible value variations
	 */
	public int getCount() {
		
		
		if (withReplacement) {
			return (int)Math.pow(n, k);
		}
		else {	// NOTE: implementation does not use Statistics.fac() to prevent overflow
			int cnt = 1;

			for (int i = n - k + 1; i <= n; i++) {
				cnt *= i;
			}

			return cnt;
		}
	}

	/**
	 * Returns an {@code Iterator} for all possible variations.
	 * 
	 * @return an {@code Iterator} for all possible variations
	 */
	public Iterator<int[]> iterator() {

		if (withReplacement) {
			return new VariationRepIterator();
		} else {
			return new VariationIterator();
		}
	}

	private class VariationIterator extends CoIterator {

		protected boolean doNext() {

			for (int i = list.length - 1; i >= 0; i--) {

				boolean exists = false;

				for (list[i]++; list[i] < n; list[i]++) {

					for (int k = 0; k < i; k++) {

						if (list[i] == list[k]) {
							exists = true;
							break;
						}
					}

					if (!exists) {
						if (i < list.length - 1) {

							list[i + 1] = -1;
							i = i + 2;
							break;
						} else {
							return true;
						}
					} else {
						exists = false;
					}
				}
			}

			list = null;
			return false;
		}
	}

	private class VariationRepIterator extends CoIterator {

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
