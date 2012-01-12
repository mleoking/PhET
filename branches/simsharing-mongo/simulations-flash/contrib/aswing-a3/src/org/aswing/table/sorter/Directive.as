/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table.sorter{

/**
 * @author iiley
 */
public class Directive {
	public var column:int;
	public var direction:Number;
	
	public function Directive(column:int, direction:Number) {
	    this.column = column;
	    this.direction = direction;
	}
}
}