/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.icon.IconWrapperParser;
import org.aswing.Icon;
import org.aswing.overflow.OffsetIcon;

/**
 *  Parses {@link org.aswing.overflow.OffsetIcon} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.icon.OffsetIconParser extends IconWrapperParser {
	
	private static var ATTR_OFFSET_X:String = "offset-x";
	private static var ATTR_OFFSET_Y:String = "offset-y";
	
	/**
	 * Constructor.
	 */
	public function OffsetIconParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):OffsetIcon {
		
		// create inner icon
		var innerIcon:Icon = super.parse(awml);
		
		// create icon
		var icon:OffsetIcon = new OffsetIcon(innerIcon);
	
		// parse offsets
		icon.setOffsetX(getAttributeAsNumber(awml, ATTR_OFFSET_X, icon.getOffsetX()));
		icon.setOffsetY(getAttributeAsNumber(awml, ATTR_OFFSET_Y, icon.getOffsetY()));
	
		return icon;
	}

}