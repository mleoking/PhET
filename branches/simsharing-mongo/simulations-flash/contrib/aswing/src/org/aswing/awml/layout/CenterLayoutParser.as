/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.CenterLayout;

/**
 * Parses {@link org.aswing.CenterLayout}.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.CenterLayoutParser extends EmptyLayoutParser {
	
	/**
     * Constructor.
     */
	public function CenterLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:CenterLayout) {
		
		layout = super.parse(awml, layout);
		
		return layout;
	}
	
    private function getClass(Void):Function {
    	return CenterLayout;	
    }
		
}