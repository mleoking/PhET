/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.ObjectParser;
import org.aswing.geom.Rectangle;

/**
 *  Parses {@link org.aswing.geom.Rectangle} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.common.BoundsParser extends ObjectParser {
	
	private static var ATTR_X:String = "x";
	private static var ATTR_Y:String = "y";
	private static var ATTR_WIDTH:String = "width";
	private static var ATTR_HEIGHT:String = "height"; 
	
	/**
	 * Constructor.
	 */
	public function BoundsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, bounds:Rectangle):Rectangle {
		if (bounds == null) {
			bounds = new Rectangle();	
		}	
	
		super.parse(awml, bounds);
	
		// init insets
		bounds.x = getAttributeAsNumber(awml, ATTR_X, bounds.x);
		bounds.y = getAttributeAsNumber(awml, ATTR_Y, bounds.y);
		bounds.width = getAttributeAsNumber(awml, ATTR_WIDTH, bounds.width);
		bounds.height = getAttributeAsNumber(awml, ATTR_HEIGHT, bounds.height);
	
		return bounds;
	}

}