/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.overflow.JTable;

/**
 *  Parses table row list.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.table.TableRowsParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function TableRowsParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, table:JTable):Void {
		super.parse(awml, table);
	}

	private function parseChild(awml:XMLNode, nodeName:String, table:JTable):Void {

		super.parseChild(awml, nodeName, table);
		
		if (nodeName == AwmlConstants.NODE_TABLE_ROW) {
			AwmlParser.parse(awml, table.getModel());
		}
	}

}