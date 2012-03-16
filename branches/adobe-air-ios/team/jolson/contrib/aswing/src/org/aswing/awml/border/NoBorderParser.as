/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.ObjectParser;

/**
 *  Parses {@link org.aswing.border.EmptyBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.NoBorderParser extends ObjectParser {
	
	/**
	 * Constructor.
	 */
	public function NoBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode) {
		return null;
	}

}