/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.AlignLayout;

/**
 * Parses {@link org.aswing.AlignLayout}.
 * 
 * @author Igor Sadovskiy
 */
 class org.aswing.awml.layout.AlignLayoutParser extends EmptyLayoutParser {
	
	private static var ATTR_HORIZONTAL_ALIGN:String = "horizontal-align";
	private static var ATTR_VERTICAL_ALIGN:String = "vertical-align";
	
	private static var ALIGN_TOP:String = "top";
	private static var ALIGN_BOTTOM:String = "bottom";
	private static var ALIGN_CENTER:String = "center";
	private static var ALIGN_LEFT:String = "left";
	private static var ALIGN_RIGHT:String = "right";
	
	public function AlignLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:AlignLayout) {
		
		layout = super.parse(awml, layout);
		
		// init aligns
		var hAlign:String = getAttributeAsString(awml, ATTR_HORIZONTAL_ALIGN);
		switch (hAlign) {
			case ALIGN_TOP:
				layout.setHorizonalAlign(AlignLayout.TOP);
				break;	
			case ALIGN_CENTER:
				layout.setHorizonalAlign(AlignLayout.CENTER);
				break;	
			case ALIGN_BOTTOM:
				layout.setHorizonalAlign(AlignLayout.BOTTOM);
				break;	
		}
		
		var vAlign:String = getAttributeAsString(awml, ATTR_VERTICAL_ALIGN);
		switch (vAlign) {
			case ALIGN_LEFT:
				layout.setVerticalAlign(AlignLayout.LEFT);
				break;	
			case ALIGN_CENTER:
				layout.setVerticalAlign(AlignLayout.CENTER);
				break;	
			case ALIGN_RIGHT:
				layout.setVerticalAlign(AlignLayout.RIGHT);
				break;	
		}
		
		return layout;
	}
	
    private function getClass(Void):Function {
    	return AlignLayout;	
    }
}