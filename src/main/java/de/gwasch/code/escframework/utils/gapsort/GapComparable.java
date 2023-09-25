package de.gwasch.code.escframework.utils.gapsort;

public interface GapComparable<T extends GapComparable<?>> {
	
	enum Result {
		Greater,
		Lower,
		Equal,
		Unknown
	}
	
	Result compareTo(T cmp);
}
