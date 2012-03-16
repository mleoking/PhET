/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.overflow.JTable;
import org.aswing.table.DefaultTableModel;
import org.aswing.table.TableColumn;

/**
 *  Parses table column.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.table.TableColumnParser extends AbstractParser {
	
	private static var ATTR_TITLE:String = "title";
	private static var ATTR_TYPE:String = "type";
	private static var ATTR_EDITABLE:String = "editable";
	private static var ATTR_RESIZABLE:String = "resizable";
	private static var ATTR_MIN_WIDTH:String = "min-width";
	private static var ATTR_MAX_WIDTH:String = "max-width";
	private static var ATTR_PREFERRED_WIDTH:String = "preferred-width";
	private static var ATTR_WIDTH:String = "width";
		
	private static var TYPE_STRING:String = "string";
	private static var TYPE_NUMBER:String = "number";
	private static var TYPE_BOOLEAN:String = "boolean";
		
	/**
	 * Constructor.
	 */
	public function TableColumnParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, table:JTable):Void {
		
		super.parse(awml, table);

		// get table model
		var model:DefaultTableModel = DefaultTableModel(table.getModel());
		
		var index:Number = table.getColumnCount();
		var column:TableColumn = new TableColumn(table.getColumnCount());

		// init title
		column.setHeaderValue(getAttributeAsString(awml, ATTR_TITLE, ""));
		
		// init editable
		model.setColumnEditable(index, getAttributeAsBoolean(awml, ATTR_EDITABLE, model.isColumnEditable(index)));

		// init column class
		var type:String = getAttributeAsString(awml, ATTR_TYPE);
		switch (type) {
			case TYPE_BOOLEAN:
				model.setColumnClass(index, "Boolean");
				break;	
			case TYPE_STRING:
				model.setColumnClass(index, "String");
				break;	
			case TYPE_NUMBER:
				model.setColumnClass(index, "Number");
				break;	
		}
				 
		// init resizable
		column.setResizable(getAttributeAsBoolean(awml, ATTR_RESIZABLE, column.getResizable()));
		
		// init widths
		column.setMinWidth(getAttributeAsNumber(awml, ATTR_MIN_WIDTH, column.getMinWidth()));
		column.setMaxWidth(getAttributeAsNumber(awml, ATTR_MAX_WIDTH, column.getMaxWidth()));
		column.setPreferredWidth(getAttributeAsNumber(awml, ATTR_PREFERRED_WIDTH, column.getPreferredWidth()));
		column.setWidth(getAttributeAsNumber(awml, ATTR_WIDTH, column.getWidth()));
		
		table.addColumn(column);
	}

}