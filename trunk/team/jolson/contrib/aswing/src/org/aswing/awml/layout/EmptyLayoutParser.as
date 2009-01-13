/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.CreatableObjectParser;
import org.aswing.EmptyLayout;

/**
 *  Parses {@link org.aswing.EmptyLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.EmptyLayoutParser extends CreatableObjectParser {
	
	/**
	 * Constructor.
	 */
	public function EmptyLayoutParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, layout:EmptyLayout) {
		
		if (layout == null) {
			layout = create(awml);	
		}	
	
		super.parse(awml, layout);
	
		return layout;
	}

    private function getClass(Void):Function {
    	return EmptyLayout;	
    }

}