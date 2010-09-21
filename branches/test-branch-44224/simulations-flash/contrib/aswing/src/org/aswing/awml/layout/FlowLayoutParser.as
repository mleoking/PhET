/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.FlowLayout;

/**
 * Parses {@link org.aswing.FlowLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.FlowLayoutParser extends EmptyLayoutParser {
	
	private static var ATTR_ALIGN:String = "align";
	private static var ATTR_HGAP:String = "hgap";
	private static var ATTR_VGAP:String = "vgap";
	
	private static var ALIGN_LEFT:String = "left";
	private static var ALIGN_CENTER:String = "center";
	private static var ALIGN_RIGHT:String = "right";
	
	/**
     * Constructor.
     */
	public function FlowLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:FlowLayout) {
		
		layout = super.parse(awml, layout);
		
		// init layout
		var align:String = getAttributeAsString(awml, ATTR_ALIGN);
		switch (align) {
			case ALIGN_CENTER:
				layout.setAlignment(FlowLayout.CENTER);
				break;	
			case ALIGN_RIGHT:
				layout.setAlignment(FlowLayout.RIGHT);
				break;
			case ALIGN_LEFT:
				layout.setAlignment(FlowLayout.LEFT);
				break; 
		}
		
		// init gaps
		layout.setHgap(getAttributeAsNumber(awml, ATTR_HGAP, layout.getHgap()));
		layout.setVgap(getAttributeAsNumber(awml, ATTR_VGAP, layout.getVgap()));
		
		return layout;
	}	
	
    private function getClass(Void):Function {
    	return FlowLayout;	
    }
	
}