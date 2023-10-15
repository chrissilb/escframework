package de.gwasch.code.escframework.events.patterns;

/**
 * A {@code RangeCondition} defines when and how a {@link Range} of events shall be evaluated.
 * It is assumed that events are collected for certain time interval.
 */
public enum RangeCondition {
	
	/**
	 * Evaluate after an interval and check for equal number of events.
	 */
	EQ_AFTER, 
	
	/**
	 * Evaluate immediately once number of events is equal. 
	 */
	ONCE_EQ,
	
	/**
	 * Evaluate after an interval and check for unequal number of events.
	 */
	NE_AFTER,
		
	/**
	 * Evaluate immediately once number of events is unequal. 
	 */
	ONCE_NE, 

	/**
	 * Evaluate after an interval and check if number of events is lower equal.
	 */
	LE_AFTER,

	/**
	 * Evaluate after an interval and check if number of events is more than.
	 */
	MT_AFTER,

	/**
	 * Evaluate immediately once number of events is more than. 
	 */
	ONCE_MT
}
