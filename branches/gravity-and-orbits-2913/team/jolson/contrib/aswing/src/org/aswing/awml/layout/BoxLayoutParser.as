/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.BoxLayout;

/**
 * Parses {@link org.aswing.BoxLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.BoxLayoutParser extends EmptyLayoutParser {
	
	private static var ATTR_AXIS:String = "axis";
	private static var ATTR_GAP:String = "gap";
	
	private static var AXIS_X:String = "x";
	private static var AXIS_Y:String = "y";
	
	/**
     * Constructor.
     */
	public function BoxLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:BoxLayout) {
		
		layout = super.parse(awml, layout);
		
		// init layout
		var axis:String = getAttributeAsString(awml, ATTR_AXIS);
		switch (axis) {
			case AXIS_Y:
				layout.setAxis(BoxLayout.Y_AXIS);
				break;
			case AXIS_X:
				layout.setAxis(BoxLayout.X_AXIS);
				break;	
		}
		
		// init gap
		layout.setGap(getAttributeAsNumber(awml, ATTR_GAP, layout.getGap()));
		
		return layout;
	}	
	
    private function getClass(Void):Function {
    	return BoxLayout;	
    }
	
}