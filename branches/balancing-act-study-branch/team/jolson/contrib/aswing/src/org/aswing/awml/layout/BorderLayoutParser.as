/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.BorderLayout;

/**
 * Parses {@link org.aswing.BorderLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.BorderLayoutParser extends EmptyLayoutParser {
	
	private static var ATTR_HGAP:String = "hgap";
	private static var ATTR_VGAP:String = "vgap";
	
	/**
     * Constructor.
     */
	public function BorderLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:BorderLayout) {
		
		layout = super.parse(awml, layout);
		
		// init gaps
		layout.setHgap(getAttributeAsNumber(awml, ATTR_HGAP, layout.getHgap()));
		layout.setVgap(getAttributeAsNumber(awml, ATTR_VGAP, layout.getVgap()));
		
		return layout;
	}
	
    private function getClass(Void):Function {
    	return BorderLayout;	
    }
		
}