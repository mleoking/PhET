/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.tree.TreePath;

/**
 *  Parses list tree item.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.listtree.ListTreeItemParser extends AbstractParser {
	
	private static var ATTR_VALUE:String = "value";
	private static var ATTR_TYPE:String = "type";
	
	private static var TYPE_STRING:String = "string";
	private static var TYPE_NUMBER:String = "number";
	private static var TYPE_BOOLEAN:String = "boolean";
	
	private static var DEFAULT_TYPE:String = TYPE_STRING;
	
	/**
	 * Constructor.
	 */
	public function ListTreeItemParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):Object {
		
		// get type
		var type:String = getAttributeAsString(awml, ATTR_TYPE, DEFAULT_TYPE);
		
		// init item
		var item:Object = null;
		
		switch (type) {
			case TYPE_STRING:
				item = getAttributeAsString(awml, ATTR_VALUE);
				break;
			case TYPE_NUMBER:
				item = getAttributeAsNumber(awml, ATTR_VALUE);
				break;
			case TYPE_BOOLEAN:
				item = getAttributeAsBoolean(awml, ATTR_VALUE);
				break;
		}

		// create list tree path item		
		var path:TreePath = new TreePath([item]);
		
		super.parse(awml, path);
	
		return path;
	}

}