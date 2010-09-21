/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;

/**
 *  Parses text for {@link org.aswing.JTextComponent}.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.text.TextParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function TextParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):String {
		
		super.parse(awml);
		
		// init text
		var text:String = "";
		
		for (var i = 0; i < awml.childNodes.length; i++) {
			text += getValueAsString(awml.childNodes[i], "");	
		}
			
		return text;
	}

}