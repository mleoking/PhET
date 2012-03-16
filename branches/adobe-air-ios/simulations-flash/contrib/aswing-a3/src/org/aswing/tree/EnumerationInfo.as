package org.aswing.tree { 
/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.tree.FHTreeStateNode;

/**
 * @author iiley
 */
public class EnumerationInfo {
	
	/** Parent thats children are being enumerated. */
	public var parent:FHTreeStateNode;
	/** Index of next child. An index of -1 signifies parent should be
	 * visibled next. */
	public var nextIndex:Number;
	/** Number of children in parent. */
	public var childCount:Number;
	/** The number of path left to enumerat*/
	public var enumCount:Number;
}
}