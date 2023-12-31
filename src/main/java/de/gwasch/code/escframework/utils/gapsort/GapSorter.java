package de.gwasch.code.escframework.utils.gapsort;

import java.util.List;

import de.gwasch.code.escframework.utils.combinatorics.Combination;
import de.gwasch.code.escframework.utils.combinatorics.Permutation;
import de.gwasch.code.escframework.utils.gapsort.GapComparable.Result;

public class GapSorter {

	public static <T extends GapComparable<? super T>> void sort(List<T> list) {
		
		if (list.size() < 2) return;
		
		MaskedList<T> mlist = new MaskedList<T>(list);
		Permutation p = new Permutation(list.size(), 0);
		
		for (int[] combi : p) {
			mlist.setMapping(combi);
			
			if (isSorted(mlist)) {
				mlist.dissolveMapping();
				break;
			}
		}
	}

	
	private static <T extends GapComparable<? super T>> boolean isSorted(List<T> list) { 
		
		Combination c = new Combination(list.size(), 2, 0);
		boolean sorted = true;
		
		for (int[] combi : c) {
			Result r = list.get(combi[0]).compareTo(list.get(combi[1]));
			if (r == Result.Greater) {
				sorted = false;
				break;
			}
		}
		
		return sorted;
	}
}
