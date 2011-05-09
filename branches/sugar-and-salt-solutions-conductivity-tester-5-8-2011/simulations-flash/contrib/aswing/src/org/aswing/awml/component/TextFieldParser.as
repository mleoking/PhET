/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.TextComponentParser;
import org.aswing.JTextField;

/**
 * Parses {@link org.aswing.JTextField} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.TextFieldParser extends TextComponentParser {
	
	private static var ATTR_COLUMNS:String = "columns";
	
	/**
	 * Constructor.
	 */
	public function TextFieldParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, text:JTextField, namespace:AwmlNamespace) {
		
		text = super.parse(awml, text, namespace);
		
		// init columns
		text.setColumns(getAttributeAsNumber(awml, ATTR_COLUMNS, text.getColumns()));
		
        // init events
        attachEventListeners(text, JTextField.ON_ACT, getAttributeAsEventListenerInfos(awml, ATTR_ON_ACTION));
		
		return text;
	}
	
    private function getClass(Void):Function {
    	return JTextField;	
    }
	
}
