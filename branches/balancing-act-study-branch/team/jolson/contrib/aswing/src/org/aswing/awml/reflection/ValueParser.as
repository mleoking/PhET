/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;

/**
 * Creates primitive type value from AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.ValueParser extends AbstractParser {
	
	private static var ATTR_TYPE:String = "type";
	private static var ATTR_VALUE:String = "value";
	
	private static var TYPE_STRING:String = "string";
	private static var TYPE_NUMBER:String = "number";
	private static var TYPE_BOOLEAN:String = "boolean";
	
	/**
	 * Constructor.
	 */
	public function ValueParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode) {
	
		super.parse(awml);
		
		// get property type
		var type:String = getAttributeAsString(awml, ATTR_TYPE, TYPE_STRING);
		
		// get value
		var value;
			
		switch (type) {
			case TYPE_BOOLEAN:
				value = getAttributeAsBoolean(awml, ATTR_VALUE, null);
				break;	
			case TYPE_NUMBER:
				value = getAttributeAsNumber(awml, ATTR_VALUE, null);
				break;	
			case TYPE_STRING:
			default:
				value = getAttributeAsString(awml, ATTR_VALUE, null);
				break;	
		}
		
		return value;
	}

}