/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event { 

import org.aswing.table.TableModel;

/**
 * TableModelEvent is used to notify listeners that a table model
 * has changed. The model event describes changes to a TableModel 
 * and all references to rows and columns are in the co-ordinate 
 * system of the model. 
 * Depending on the parameters used in the constructors, the TableModelevent
 * can be used to specify the following types of changes: <p>
 *
 * <pre>
 * TableModelEvent(source);              //  The data, ie. all rows changed 
 * TableModelEvent(source, HEADER_ROW);  //  Structure change, reallocate TableColumns
 * TableModelEvent(source, 1);           //  Row 1 changed
 * TableModelEvent(source, 3, 6);        //  Rows 3 to 6 inclusive changed
 * TableModelEvent(source, 2, 2, 6);     //  Cell at (2, 6) changed
 * TableModelEvent(source, 3, 6, ALL_COLUMNS, INSERT); // Rows (3, 6) were inserted
 * TableModelEvent(source, 3, 6, ALL_COLUMNS, DELETE); // Rows (3, 6) were deleted
 * </pre>
 *
 * It is possible to use other combinations of the parameters, not all of them 
 * are meaningful. By subclassing, you can add other information, for example: 
 * whether the event WILL happen or DID happen. This makes the specification 
 * of rows in DELETE events more useful but has not been included in 
 * the swing package as the JTable only needs post-event notification. 
 * <p>
 * 
 * @author iiley
 */
public class TableModelEvent extends ModelEvent {
	
    /** Identifies the addtion of new rows or columns. */
    public static const INSERT:String =  "insert";
    /** Identifies a change to existing data. */
    public static const UPDATE:String =  "update";
    /** Identifies the removal of rows or columns. */
    public static const DELETE:String = "delete";
    
    /** Identifies the header row. */
    public static const HEADER_ROW:int = -1;

    /** Specifies all columns in a row or rows. */
    public static const ALL_COLUMNS:int = -1;
    
    private var type:String;
    private var firstRow:int;
    private var lastRow:int;
    private var column:int;
    
    /**
     * <pre>
     * TableModelEvent(source:TableModel, firstRow:int, lastRow:int, column:int, type:String)
     * TableModelEvent(source:TableModel, firstRow:int, lastRow:int, column:int)
     * TableModelEvent(source:TableModel, firstRow:int, lastRow:int)
     * TableModelEvent(source:TableModel, row:int)
     * TableModelEvent(source:TableModel)
     * </pre>
     * <p>
     * <ul>
     * <li>TableModelEvent(source:TableModel, firstRow:int, lastRow:int, column:int, type:String)<br>
     *  The cells from (firstRow, column) to (lastRow, column) have been changed. 
     *  The <I>column</I> refers to the column index of the cell in the model's 
     *  co-ordinate system. When <I>column</I> is ALL_COLUMNS, all cells in the 
     *  specified range of rows are considered changed. <br>
     *  The <I>type</I> should be one of: INSERT, UPDATE and DELETE.
     *  
     *  <li>TableModelEvent(source:TableModel, firstRow:int, lastRow:int, column:int)<br>
     *  The cells in column <I>column</I> in the range 
     *  [<I>firstRow</I>, <I>lastRow</I>] have been updated. 
     *  
     *  <li>TableModelEvent(source:TableModel, firstRow:int, lastRow:int)<br>
     *  The data in rows [<I>firstRow</I>, <I>lastRow</I>] have been updated.
     *   
     *  <li>TableModelEvent(source:TableModel, row:int)<br>
     *  This row of data has been updated. 
     *  To denote the arrival of a completely new table with a different structure 
     *  use <code>HEADER_ROW</code> as the value for the <code>row</code>. 
     *  When the <code>JTable</code> receives this event and its
     *  <code>autoCreateColumnsFromModel</code> 
     *  flag is set it discards any TableColumns that it had and reallocates 
     *  default ones in the order they appear in the model. This is the 
     *  same as calling <code>setModel(TableModel)</code> on the <code>JTable</code>.
     *   
     *  <li>TableModelEvent(source:TableModel)<br>
     *  All row data in the table has changed, listeners should discard any state 
     *  that was based on the rows and requery the <code>TableModel</code>
     *  to get the new row count and all the appropriate values. 
     *  The <code>JTable</code> will repaint the entire visible region on
     *  receiving this event, querying the model for the cell values that are visible. 
     *  The structure of the table ie, the column names, types and order 
     *  have not changed.  
     * </ul>
     */
    public function TableModelEvent(source:TableModel, firstRow:int=-2, lastRow:int=-2, column:int=ALL_COLUMNS, type:String=UPDATE) {
    	super(source);
    	if(firstRow == -2){
	        // Use int.MAX_VALUE instead of getRowCount() in case rows were deleted. 
			init(0, int.MAX_VALUE, column, type);
    	}else if(lastRow == -2){
        	init(firstRow, firstRow, column, type);
    	}else{
			init(firstRow, lastRow, column, type);
    	}
    }
	
	private function init(firstRow:int, lastRow:int, column:int, type:String):void{
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.column = column;
		this.type = type;
	}
	
	//**********************
	// Querying Methods
	//**********************
	
	public function getType():String{
		return type;
	}
	
    /** Returns the first row that changed.  HEADER_ROW means the meta data, 
     * ie. names, types and order of the columns. 
     */
    public function getFirstRow():int { return firstRow; };

    /** Returns the last row that changed. */
    public function getLastRow():int { return lastRow; };
    
    /**
     *  Returns the column for the event.  If the return
     *  value is ALL_COLUMNS; it means every column in the specified
     *  rows changed.
     */
    public function getColumn():int { return column; }; 
}
}