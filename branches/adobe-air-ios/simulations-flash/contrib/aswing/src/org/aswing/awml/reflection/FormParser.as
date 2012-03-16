/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.reflection.ComponentParser;
import org.aswing.Container;

/**
 *  Parses form component AWML reflection element (JFrame, JWindow, MCPanel).
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.FormParser extends ComponentParser {
	
	/**
	 * Constructor.
	 */
	public function FormParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, form:Container, namespace:AwmlNamespace) {
	
		form = super.parse(awml, form, namespace);
		
		return form;
	}

	private function getConstructorArgs(awml:XMLNode):Array {
		return ([getOwner(awml)]).concat(super.getConstructorArgs(awml)); 
	}
}