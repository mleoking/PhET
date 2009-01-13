/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.ObjectParser;
import org.aswing.geom.Point;

/**
 *  Parses {@link org.aswing.geom.Point} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.common.PointParser extends ObjectParser {
	
	private static var ATTR_X:String = "x";
	private static var ATTR_Y:String = "y";
	
	/**
	 * Constructor.
	 */
	public function PointParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, point:Point):Point {
		if (point == null) {
			point = new Point();	
		}	
	
		super.parse(awml, point);
	
		// init insets
		point.x = getAttributeAsNumber(awml, ATTR_X, point.x);
		point.y = getAttributeAsNumber(awml, ATTR_Y, point.y);
	
		return point;
	}

}