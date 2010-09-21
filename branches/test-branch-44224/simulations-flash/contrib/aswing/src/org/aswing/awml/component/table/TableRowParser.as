/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.table.DefaultTableModel;
import org.aswing.table.TableModel;

/**
 *  Parses table row.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.table.TableRowParser extends AbstractParser {
	
	private var rowData:Array;
	
	/**
	 * Constructor.
	 */
	public function TableRowParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, model:TableModel):Void {
		
		// create empty data row
		rowData = new Array();
		
		super.parse(awml, model);
		
		// add row to model
		DefaultTableModel(model).addRow(rowData);
	}

	private function parseChild(awml:XMLNode, nodeName:String, model:TableModel):Void {
		
		super.parseChild(awml, nodeName, model);
		
		if (nodeName == AwmlConstants.NODE_TABLE_CELL) {
			var type:String = DefaultTableModel(model).getColumnClass(rowData.length);
			rowData.push(AwmlParser.parse(awml, type));
		}
	}

}