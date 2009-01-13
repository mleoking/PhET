/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;

/**
 *  Parses arguments AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.ArgumentsParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function ArgumentsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):Array {
	
		// create arguments array
		var args:Array = new Array();
		
		super.parse(awml, args);
		
		return args;
	}

	private function parseChild(awml:XMLNode, nodeName:String, args:Array):Void {
		
		super.parseChild(awml, nodeName);

		if (AwmlUtils.isArgumentNode(nodeName)) {
			var value = AwmlParser.parse(awml);
			if (value !== undefined) args.push(value);
		}
	}

}