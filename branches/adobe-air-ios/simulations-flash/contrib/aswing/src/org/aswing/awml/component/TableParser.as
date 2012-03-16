/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.overflow.JTable;

/**
 * Parses {@link org.aswing.overflow.JTable} level elements.
 * 
 * @author iiley
 */
class org.aswing.awml.component.TableParser extends ComponentParser {
	
	private static var ATTR_HORIZONTAL_BLOCK_INCREMENT:String = "horizontal-block-increment";
	private static var ATTR_VERTICAL_BLOCK_INCREMENT:String = "vertical-block-increment";
	private static var ATTR_HORIZONTAL_UNIT_INCREMENT:String = "horizontal-unit-increment";
	private static var ATTR_VERTICAL_UNIT_INCREMENT:String = "vertical-unit-increment";
	
    private static var ATTR_AUTO_RESIZE_MODE:String = "auto-resize-mode";
    private static var ATTR_SELECTION_MODE:String = "selection-mode";
    private static var ATTR_ROW_HEIGHT:String = "row-height";
    private static var ATTR_ROW_MARGIN:String = "row-margin";
    private static var ATTR_COLUMN_MARGIN:String = "column-margin";
    
    private static var ATTR_SHOW_HORIZONTAL_LINES:String = "show-horizontal-lines";
    private static var ATTR_SHOW_VERTICAL_LINES:String = "show-vertical-lines";
    private static var ATTR_ROW_SELECTION_ALLOWED:String = "row-selection-allowed";
    private static var ATTR_COLUMN_SELECTION_ALLOWED:String = "column-selection-allowed";
    
    private static var ATTR_ON_SELECTION_CHANGED:String = "on-selection-changed";
    
	public static var AUTO_RESIZE_OFF:String = "off";
	public static var AUTO_RESIZE_NEXT_COLUMN:String = "next-column";
	public static var AUTO_RESIZE_SUBSEQUENT_COLUMNS:String = "subsequent-columns";
	public static var AUTO_RESIZE_LAST_COLUMN:String = "last-column";
	public static var AUTO_RESIZE_ALL_COLUMNS:String = "all-column";    
	
    private static var MODE_SINGLE:String = "single";
	private static var MODE_MULTIPLE:String = "multiple";
    
    /**
     * Constructor.
     */	
	public function TableParser(Void) {
		super();
	}

    public function parse(awml:XMLNode, table:JTable, namespace:AwmlNamespace) {
    	
        table = super.parse(awml, table, namespace);
        
		// init block increments
		table.setHorizontalBlockIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_BLOCK_INCREMENT, table.getHorizontalBlockIncrement()));
		table.setVerticalBlockIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_BLOCK_INCREMENT, table.getVerticalBlockIncrement()));
		table.setHorizontalUnitIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_UNIT_INCREMENT, table.getHorizontalUnitIncrement()));
		table.setVerticalUnitIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_UNIT_INCREMENT, table.getVerticalUnitIncrement()));
        
        // init auto resize mode
        var autoResizeMode:String = getAttributeAsString(awml, ATTR_AUTO_RESIZE_MODE, null);
        switch (autoResizeMode) {
        	case AUTO_RESIZE_OFF:
        		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        		break;	
        	case AUTO_RESIZE_NEXT_COLUMN:
        		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        		break;	
        	case AUTO_RESIZE_SUBSEQUENT_COLUMNS:
        		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        		break;	
        	case AUTO_RESIZE_LAST_COLUMN:
        		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        		break;	
        	case AUTO_RESIZE_ALL_COLUMNS:
        		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        		break;	
        }
        
        // init selection mode
        var selectionMode:String = getAttributeAsString(awml, ATTR_SELECTION_MODE, null);
        switch (selectionMode) {
        	case MODE_SINGLE:
        		table.setSelectionMode(JTable.SINGLE_SELECTION);
        		break;	
        	case MODE_MULTIPLE:
        		table.setSelectionMode(JTable.MULTIPLE_SELECTION);
        		break;	
        }   
        
        //init row height
        table.setRowHeight(getAttributeAsNumber(awml, ATTR_ROW_HEIGHT, table.getRowHeight()));
        //init row margin
        table.setRowMargin(getAttributeAsNumber(awml, ATTR_ROW_MARGIN, table.getRowMargin()));
        //init column margin
        table.getColumnModel().setColumnMargin(getAttributeAsNumber(awml, ATTR_ROW_MARGIN,
        	table.getColumnModel().getColumnMargin()));
        //init show grids
        table.setShowHorizontalLines(getAttributeAsBoolean(awml, ATTR_SHOW_HORIZONTAL_LINES, table.getShowHorizontalLines()));
        table.setShowVerticalLines(getAttributeAsBoolean(awml, ATTR_SHOW_VERTICAL_LINES, table.getShowVerticalLines()));
        //init selection allowed
        table.setRowSelectionAllowed(getAttributeAsBoolean(awml, ATTR_ROW_SELECTION_ALLOWED, table.getRowSelectionAllowed()));
        table.setColumnSelectionAllowed(getAttributeAsBoolean(awml, ATTR_COLUMN_SELECTION_ALLOWED, table.getColumnSelectionAllowed()));
        
        // init events
        attachEventListeners(table, JTable.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        attachEventListeners(table, JTable.ON_SELECTION_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_SELECTION_CHANGED));
        
        return table;
    }
    
	private function parseChild(awml:XMLNode, nodeName:String, table:JTable, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, table, namespace);
		
		switch (nodeName) {
			case AwmlConstants.NODE_SELECTION_BACKGROUND:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) table.setSelectionBackground(color);
				break;
			case AwmlConstants.NODE_SELECTION_FOREGROUND:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) table.setSelectionForeground(color);
				break;
			case AwmlConstants.NODE_GRID_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) table.setGridColor(color);
				break;
			case AwmlConstants.NODE_TABLE_COLUMNS:
				AwmlParser.parse(awml, table);
				break;
			case AwmlConstants.NODE_TABLE_ROWS:
				var data:Array = AwmlParser.parse(awml, table);
				break;
		}
	}

    private function getClass(Void):Function {
    	return JTable;	
    }
    
}