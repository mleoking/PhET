/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;

/**
 *  Parses table cell.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.table.TableCellParser extends AbstractParser {
	
	private static var ATTR_VALUE:String = "value";
		
	private static var TYPE_STRING:String = "string";
	private static var TYPE_NUMBER:String = "number";
	private static var TYPE_BOOLEAN:String = "boolean";
		
	/**
	 * Constructor.
	 */
	public function TableCellParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, type:String):Object {
		
		super.parse(awml);

		var value:Object;

		// init value
		switch (type.toLowerCase()) {
			case TYPE_BOOLEAN:
				value = getAttributeAsBoolean(awml, ATTR_VALUE);
				break;	
			case TYPE_NUMBER:
				value = getAttributeAsNumber(awml, ATTR_VALUE);
				break;	
			case TYPE_STRING:
			default:
				value = getAttributeAsString(awml, ATTR_VALUE);
				break;	
		}
	
		return value;
	}

}