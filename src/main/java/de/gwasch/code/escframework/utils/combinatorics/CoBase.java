package de.gwasch.code.escframework.utils.combinatorics;

import java.util.Iterator;

/**
 * Common super class of classes that provide combinatoric functionality.
 */
public abstract class CoBase implements Iterable<int[]> {

	private int offset;
	protected int n;
	protected int k;
	protected boolean withReplacement;

	/**
	 * Constructs a {@code CoBase} instance.
	 * 
	 * @param n               Number of values
	 * @param k               Number of selected values
	 * @param offset          Defines the first value. All other values are
	 *                        successively incremented by one.
	 * @param withReplacement Defines if the drawing is with replacement.
	 */
	protected CoBase(int n, int k, int offset, boolean withReplacement) {

		if (n <= 0)
			throw new RuntimeException("Bad Call: n must be > 0");
		if (k <= 0)
			throw new RuntimeException("Bad Call: k must be > 0");
		if (k > n)
			throw new RuntimeException("Bad Call: n must be >= k");

		this.n = n;
		this.k = k;
		this.offset = offset;
		this.withReplacement = withReplacement;
	}

	/**
	 * Constructs a {@code CoBase} instance. The offset (first value) is 0.
	 * 
	 * @param n Number of values
	 * @param k Number of selected values
	 */
	protected CoBase(int n, int k, boolean withReplacement) {
		this(n, k, 0, withReplacement);
	}

	/**
	 * Returns if the drawing is with replacement.
	 * 
	 * @return {@code true} if with replacement.
	 */
	public boolean isWithReplacement() {
		return withReplacement;
	}

	/**
	 * Returns the offset value.
	 * 
	 * @return The offset value
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the number of given values.
	 * 
	 * @return The number of given values
	 */
	public int getN() {
		return n;
	}

	/**
	 * Returns the number of selected values.
	 * 
	 * @return The number of selected values
	 */
	public int getK() {
		return k;
	}

	/**
	 * Returns the count of possible combinations.
	 * 
	 * @return the count of possible combinations
	 */
	public abstract int getCount();

	/**
	 * Returns an {@code Iterator} for all possible combinations.
	 * 
	 * @return An {@code Iterator} for all possible combinations
	 */
	public abstract Iterator<int[]> iterator();

	protected abstract class CoIterator implements Iterator<int[]> {

		protected int[] list;
		private int[] outList;
		private boolean hasLookup;

		protected CoIterator() {
			hasLookup = true;

			list = new int[k];
			outList = new int[k];

			if (!withReplacement) {
				for (int i = 0; i < list.length; i++) {
					list[i] = i;
				}
			}
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
			} else {
				doNext();
			}

			for (int i = 0; i < k; i++) {
				outList[i] = list[i] + offset;
			}

			return outList;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		protected abstract boolean doNext();
	}
}
