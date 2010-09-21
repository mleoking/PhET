/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.overflow.JTable;

/**
 *  Parses table column list.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.table.TableColumnsParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function TableColumnsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, table:JTable) {
		super.parse(awml, table);
	}

	private function parseChild(awml:XMLNode, nodeName:String, table:JTable):Void {

		super.parseChild(awml, nodeName, table);
		
		if (nodeName == AwmlConstants.NODE_TABLE_COLUMN) {
			AwmlParser.parse(awml, table);
		}
	}

}