/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * @author iiley
 */
class org.aswing.table.sorter.Directive {
	public var column:Number;
	public var direction:Number;
	
	public function Directive(column:Number, direction:Number) {
	    this.column = column;
	    this.direction = direction;
	}
}