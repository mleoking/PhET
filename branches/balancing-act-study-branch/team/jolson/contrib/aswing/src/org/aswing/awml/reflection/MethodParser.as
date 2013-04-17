/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.core.ObjectParser;

/**
 *  Parses method AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.MethodParser extends ObjectParser {
	
	private static var ATTR_NAME:String = "name";
	
	/**
	 * Constructor.
	 */
	public function MethodParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, object:Object) {
	
		// create arguments array
		var args:Array = new Array();
		
		var argumentsNode:XMLNode = AwmlUtils.getNodeChild(awml, AwmlConstants.NODE_ARGUMENTS);
		if (argumentsNode != null) {
			args = AwmlParser.parse(argumentsNode);
		}
		
		// get method name
		var methodName:String = getAttributeAsString(awml, ATTR_NAME);

		// get method reference
		var method:Function = object[methodName];
		
		// call mathod
		if (method != null) {
			var result;
			try {
				result = method.apply(object, args);
			} catch (e:Error) {
				trace("AWML Error: Unexpected exception during method execution: " + awml.toString());	
			}
			if (result != null) super.parse(awml, result);	
		} else {
			trace("AWML Warning: Can't execute method for node: " + awml.toString());	
		}
	}

}