/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.ObjectParser;
import org.aswing.Insets;

/**
 *  Parses {@link org.aswing.Insets} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.common.InsetsParser extends ObjectParser {
	
	private static var ATTR_TOP:String = "top";
	private static var ATTR_BOTTOM:String = "bottom";
	private static var ATTR_LEFT:String = "left";
	private static var ATTR_RIGHT:String = "right"; 
	
	/**
	 * Constructor.
	 */
	public function InsetsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, insets:Insets):Insets {
		if (insets == null) {
			insets = new Insets();	
		}	
	
		super.parse(awml, insets);
	
		// init insets
		insets.top = getAttributeAsNumber(awml, ATTR_TOP, insets.top);
		insets.bottom = getAttributeAsNumber(awml, ATTR_BOTTOM, insets.bottom);
		insets.left = getAttributeAsNumber(awml, ATTR_LEFT, insets.left);
		insets.right = getAttributeAsNumber(awml, ATTR_RIGHT, insets.right);
	
		return insets;
	}

}