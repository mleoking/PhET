/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.event.TableModelEvent;
import org.aswing.event.TableModelListener;
import org.aswing.table.TableModel;
import org.aswing.util.ArrayUtils;

/**
 *  This abstract class provides default implementations for most of
 *  the methods in the <code>TableModel</code> interface. It takes care of
 *  the management of listeners and provides some conveniences for generating
 *  <code>TableModelEvents</code> and dispatching them to the listeners.
 *  To create a concrete <code>TableModel</code> as a subclass of
 *  <code>AbstractTableModel</code> you need only provide implementations 
 *  for the following three methods:
 *
 *  <pre>
 *  public function getRowCount():Number;
 *  public function getColumnCount():Number;
 *  public function getValueAt(row:Number, column:Number);
 *  </pre>
 *  
 * @author iiley
 */
class org.aswing.table.AbstractTableModel implements TableModel {

    /** List of listeners */
    private var listenerList:Array;
    
    private var columnClasses:Array;
    
    private function AbstractTableModel(){
    	listenerList = new Array();
    	columnClasses = new Array();
    }
    
    /**
     * Subclass must override this method.
     */
    public function getRowCount():Number{
    	trace("AbstractTableModel not implemented method!");
    	throw new Error("AbstractTableModel not implemented method!");
    	return -1;
    }

    /**
     * Subclass must override this method.
     */
    public function getColumnCount():Number{
    	trace("AbstractTableModel not implemented method!");
    	throw new Error("AbstractTableModel not implemented method!");
    	return -1;
    }
    
    /**
     * Subclass must override this method.
     */
    public function getValueAt(rowIndex:Number, columnIndex:Number){
    	trace("AbstractTableModel not implemented method!");
    	throw new Error("AbstractTableModel not implemented method!");
    	return null;
    }
    
    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     *
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public function getColumnName(column:Number):String {
        return String.fromCharCode(32+column%26);
    }

    /**
     * Returns a column given its name.
     * Implementation is naive so this should be overridden if
     * this method is to be called often. This method is not
     * in the <code>TableModel</code> interface and is not used by the
     * <code>JTable</code>.
     *
     * @param columnName string containing name of column to be located
     * @return the column with <code>columnName</code>, or -1 if not found
     */
    public function findColumn(columnName:String):Number {
        for (var i:Number = 0; i < getColumnCount(); i++) {
            if (columnName == getColumnName(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns class name regardless of <code>columnIndex</code>.
     *
     * @param columnIndex  the column being queried
     * @return the class name, default is "Object"
     */
    public function getColumnClass(columnIndex:Number):String {
    	if(columnClasses[columnIndex] == undefined){
			return "Object";
    	}else{
    		return columnClasses[columnIndex];
    	}
    }
    
    /**
     * Sets class name regardless of <code>columnIndex</code>.
     *
     * @param columnIndex  the column being queried
     * @param className the class name
     */
    public function setColumnClass(columnIndex:Number, className:String):Void{
    	columnClasses[columnIndex] = className;
    }

    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
    public function isCellEditable(rowIndex:Number, columnIndex:Number):Boolean {
		return false;
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     */
    public function setValueAt(aValue, rowIndex:Number, columnIndex:Number):Void {
    }


//
//  Managing Listeners
//

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public function addTableModelListener(l:TableModelListener):Void {
		listenerList.push(l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public function removeTableModelListener(l:TableModelListener):Void {
		ArrayUtils.removeFromArray(listenerList, l);
    }

    /**
     * Returns an array of all the table model listeners 
     * registered on this model.
     *
     * @return all of this model's <code>TableModelListener</code>s 
     *         or an empty
     *         array if no table model listeners are currently registered
     *
     * @see #addTableModelListener
     * @see #removeTableModelListener
     */
    public function getTableModelListeners():Array {
        return listenerList.concat();
    }

	//**********************************************************************
	//                          Fire methods
	//**********************************************************************

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    private function fireTableDataChanged():Void {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    private function fireTableStructureChanged():Void {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param  firstRow  the first row
     * @param  lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     *
     */
    private function fireTableRowsInserted(firstRow:Number, lastRow:Number):Void {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    private function fireTableRowsUpdated(firstRow:Number, lastRow:Number):Void {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    private function fireTableRowsDeleted(firstRow:Number, lastRow:Number):Void {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    /**
     * Notifies all listeners that the value of the cell at 
     * <code>[row, column]</code> has been updated.
     *
     * @param row  row of cell which has been updated
     * @param column  column of cell which has been updated
     * @see TableModelEvent
     * @see EventListenerList
     */
    private function fireTableCellUpdated(row:Number, column:Number):Void {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    private function fireTableChanged(e:TableModelEvent):Void {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:Number = listenerList.length-1; i>=0; i--) {
			var lis:TableModelListener = TableModelListener(listenerList[i]);
		    lis.tableChanged(e);
		}
    }
}