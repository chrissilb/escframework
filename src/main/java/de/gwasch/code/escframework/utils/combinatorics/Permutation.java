package de.gwasch.code.escframework.utils.combinatorics;

/**
 * Provides a permutation of integers.
 */
public class Permutation extends Variation {
	
	public Permutation(int n) {
		super(n, n);
	}
	
	public Permutation(int n, int offset) {
		super(n, n, offset);
	}
}