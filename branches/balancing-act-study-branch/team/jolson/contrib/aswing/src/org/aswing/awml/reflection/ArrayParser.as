/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;

/**
 *  Parses array AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.ArrayParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function ArrayParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):Array {
	
		// create result array
		var arr:Array = new Array();
		
		super.parse(awml, arr);
		
		return arr;
	}

	private function parseChild(awml:XMLNode, nodeName:String, arr:Array):Void {
		
		super.parseChild(awml, nodeName);

		if (AwmlUtils.isPropertyNode(nodeName)) {
			var value = AwmlParser.parse(awml);
			if (value !== undefined) arr.push(value);
		}
	}

}