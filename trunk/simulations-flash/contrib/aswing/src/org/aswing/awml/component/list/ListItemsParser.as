/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;

/**
 *  Parses list items collection.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.list.ListItemsParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function ListItemsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, collection:Array):Array {
		
		if (collection == null) {
			collection = new Array();	
		}	
	
		super.parse(awml, collection);
	
		return collection;
	}

	private function parseChild(awml:XMLNode, nodeName:String, collection:Array):Void {

		super.parseChild(awml, nodeName, collection);
		
		switch (nodeName) {
			case AwmlConstants.NODE_LIST_ITEM:
				var item:Object = AwmlParser.parse(awml);
				if (item != null) collection.push(item);
				break;
		}
	}

}