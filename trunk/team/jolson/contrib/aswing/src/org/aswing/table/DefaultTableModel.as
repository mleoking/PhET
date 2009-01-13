﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.event.TableModelEvent;
import org.aswing.table.AbstractTableModel;
import org.aswing.util.ArrayUtils;

/**
 * @author iiley
 */
class org.aswing.table.DefaultTableModel extends AbstractTableModel {
	
    /**
     * The <code>Array</code> of <code>Arrays</code> of values.
     */
    private var dataArray:Array;

    /** The <code>Array</code> of column names. */
    private var columnNames:Array;
    
    private var columnsEditable:Array;

    /**
     * Constructs a default <code>DefaultTableModel</code> 
     * which is a table of zero columns and zero rows.
     * <p>
     * You must call init method after constructing.
     * @see #initWithRowcountColumncount()
     * @see #initWithNamesRowcount()
     * @see #initWithDataNames()
     */
	public function DefaultTableModel() {
		super();
		columnNames = new Array();
		columnsEditable = new Array();
	}

    /**
     *  Initializes a <code>DefaultTableModel</code> with
     *  <code>rowCount</code> and <code>columnCount</code> of
     *  <code>null</code> object values.
     *
     * @param rowCount           the number of rows the table holds
     * @param columnCount        the number of columns the table holds
     *
     * @see #setValueAt()
     */
    public function initWithRowcountColumncount(rowCount:Number, columnCount:Number):DefaultTableModel {
        return initWithNamesRowcount(new Array(columnCount), rowCount); 
    }


    /**
     *  Initializes a <code>DefaultTableModel</code> with as many
     *  columns as there are elements in <code>columnNames</code>
     *  and <code>rowCount</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> array.
     *
     * @param columnNames       <code>array</code> containing the names
     *				of the new columns; if this is
     *                          <code>null</code> then the model has no columns
     * @param rowCount           the number of rows the table holds
     * @see #setDataArray()
     * @see #setValueAt()
     */
    public function initWithNamesRowcount(columnNames:Array, rowCount:Number):DefaultTableModel {
        setDataNames(new Array(rowCount), columnNames);
        return this;
    }

    /**
     *  Initializes a <code>DefaultTableModel</code>
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataArray</code>
     *  method. The first index in the <code>[][]</code> array is
     *  the row index and the second is the column index.
     *
     * @param data              the data of the table
     * @param columnNames       the names of the columns
     * @see #setDataArray()
     */
    public function initWithDataNames(data:Array, columnNames:Array):DefaultTableModel {
        setDataNames(data, columnNames);
        return this;
    }

    /**
     *  Returns the <code>Array</code> of <code>Arrays</code>
     *  that contains the table's
     *  data values.  The arrays contained in the outer array are
     *  each a single row of values.  In other words, to get to the cell
     *  at row 1, column 5: <p>
     *
     *  <code>getDataVector()[1][5];</code><p>
     *
     * @return  the array of arrays containing the tables data values
     *
     * @see #newDataAvailable()
     * @see #newRowsAdded()
     * @see #setDataArray()
     */
    public function getData():Array {
        return dataArray;
    }

    /**
     *  Replaces the current <code>dataArray</code> instance variable 
     *  with the new <code>Vector</code> of rows, <code>dataArray</code>.
     *  Each row is represented in <code>dataArray</code> as a
     *  <code>Vector</code> of <code>Object</code> values.
     *  <p>Note that passing in a <code>null</code> value for
     *  <code>dataArray</code> results in unspecified behavior,
     *  an possibly an exception.
     *
     * @param   dataArray         the new data vector
     * @see #getData()
     */

	public function setData(dataArray:Array):Void {
		setDataNames(dataArray, columnNames);
	}

    private static function nonNullArray(v:Array):Array { 
		return (v != null) ? v : new Array(); 
    } 

    /**
     *  Replaces the current <code>dataArray</code> instance variable 
     *  with the new <code>Vector</code> of rows, <code>dataArray</code>.
     *  Each row is represented in <code>dataArray</code> as a
     *  <code>Vector</code> of <code>Object</code> values.
     *  
     *  <p><code>columnNames</code> are the names of the new 
     *  columns.  The first name in <code>columnNames</code> is
     *  mapped to column 0 in <code>dataArray</code>. Each row in
     *  <code>dataArray</code> is adjusted to match the number of 
     *  columns in <code>columnNames</code>
     *  either by truncating the <code>Vector</code> if it is too long,
     *  or adding <code>null</code> values if it is too short.
     *  
     *  <p>Note that passing in a <code>null</code> value for
     *  <code>dataArray</code> results in unspecified behavior,
     *  an possibly an exception.
     *
     * @param   dataArray         the new data vector
     * @param   columnNames     the names of the columns
     * @see #getData()
     */
    public function setDataNames(dataArray:Array, columnNames:Array):Void {
        this.dataArray = nonNullArray(dataArray);
        this.columnNames = nonNullArray(columnNames);
		justifyRows(0, getRowCount()); 
        fireTableStructureChanged();
    }

    /**
     *  Equivalent to <code>fireTableChanged</code>.
     *
     * @param event  the change event 
     *
     */
    public function newDataAvailable(event:TableModelEvent):Void {
        fireTableChanged(event);
    }

	//*******************************
	//      Manipulating rows
	//*******************************

    private function justifyRows(from:Number, to:Number):Void { 
		// Sometimes the DefaultTableModel is subclassed 
		// instead of the AbstractTableModel by mistake. 
		// Set the number of rows for the case when getRowCount 
		// is overridden. 
		ArrayUtils.setSize(dataArray, getRowCount());
        for (var i:Number = from; i < to; i++) { 
		    if (dataArray[i] == null) { 
		    	dataArray[i] = new Array(getColumnCount());
		    }
		}
    }

    /**
     *  Ensures that the new rows have the correct number of columns.
     *  This is accomplished by  using the <code>setSize</code> method in
     *  <code>Vector</code> which truncates vectors
     *  which are too long, and appends <code>null</code>s if they
     *  are too short.
     *  This method also sends out a <code>tableChanged</code>
     *  notification message to all the listeners.
     *
     * @param e         this <code>TableModelEvent</code> describes 
     *                           where the rows were added. 
     *				 If <code>null</code> it assumes
     *                           all the rows were newly added
     * @see #getData()
     */
    public function newRowsAdded(e:TableModelEvent):Void {
        justifyRows(e.getFirstRow(), e.getLastRow() + 1); 
        fireTableChanged(e);
    }

    /**
     *  Equivalent to <code>fireTableChanged</code>.
     *
     *  @param e the change event
     *
     */
    public function rowsRemoved(e:TableModelEvent):Void {
        fireTableChanged(e);
    }

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use <code>setRowCount</code> instead.
     */
    /*
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <code>rowCount</code> and greater are discarded. <p>
     *
     * @param   rowCount   the new number of rows
     * @see #setRowCount()
     */
    public function setNumRows(rowCount:Number):Void { 
        var old:Number = getRowCount();
		if (old == rowCount) { 
		    return; 
		}
		ArrayUtils.setSize(dataArray, rowCount);
        if (rowCount <= old) {
            fireTableRowsDeleted(rowCount, old-1);
        } else {
	    	justifyRows(old, rowCount); 
            fireTableRowsInserted(old, rowCount-1);
        }
    }

    /**
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <code>rowCount</code> and greater are discarded. <p>
     *
     *  @see #setColumnCount()
     */
    public function setRowCount(rowCount:Number):Void { 
		setNumRows(rowCount); 
    } 

    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public function addRow(rowData:Array):Void {
        insertRow(getRowCount(), rowData);
    }

    /**
     *  Inserts a row at <code>row</code> in the model.  The new row
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     */
    public function insertRow(row:Number, rowData:Array):Void {
    	dataArray.splice(row, 0, rowData);
		justifyRows(row, row+1); 
        fireTableRowsInserted(row, row);
    }

    private static function gcd(i:Number, j:Number):Number {
		return (j == 0) ? i : gcd(j, i%j); 
    }

    private static function rotate(v:Array, a:Number, b:Number, shift:Number):Void {
		var size:Number = b - a; 
		var r:Number = size - shift;
		var g:Number = gcd(size, r); 
		for(var i:Number = 0; i < g; i++) {
		    var to:Number = i; 
		    var tmp = v[a + to]; 
		    for(var from:Number = (to + r) % size; from != i; from = (to + r) % size) {
		    	v[a + to] = v[a + from];
				to = from; 
		    }
		    v[a + to] = tmp;
		}
    }

    /**
     *  Moves one or more rows from the inclusive range <code>start</code> to 
     *  <code>end</code> to the <code>to</code> position in the model. 
     *  After the move, the row that was at index <code>start</code> 
     *  will be at index <code>to</code>. 
     *  This method will send a <code>tableChanged</code> notification
     *  message to all the listeners. <p>
     *
     *  <pre>
     *  Examples of moves:
     *  <p>
     *  1. moveRow(1,3,5);
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|g|h|B|C|D|i|j|k   - after
     *  <p>
     *  2. moveRow(6,7,1);
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *  <p> 
     *  </pre>
     *
     * @param   start       the starting row index to be moved
     * @param   end         the ending row index to be moved
     * @param   to          the destination of the rows to be moved
     * 
     */
    public function moveRow(start:Number, end:Number, to:Number):Void { 
		var shift:Number = to - start; 
		var first:Number, last:Number; 
		if (shift < 0) { 
		    first = to; 
		    last = end; 
		}
		else { 
		    first = start; 
		    last = to + end - start;  
		}
        rotate(dataArray, first, last + 1, shift); 

        fireTableRowsUpdated(first, last);
    }

    /**
     * Removes the row at <code>row</code> from the model.  Notification
     * of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     */
    public function removeRow(row:Number):Void {
    	if(row >= 0 && row < getRowCount()){
	    	dataArray.splice(row, 1);
	        fireTableRowsDeleted(row, row);
    	}
    }
    
    /**
     * Removes the all the rows .  Notification
     * of the rows being removed will be sent to all the listeners.
     */
    public function clearRows():Void{
    	var length:Number = dataArray.length;
    	if(length > 0){
	    	dataArray.splice(0);
	    	fireTableRowsDeleted(0, length-1);
    	}
    }

	//***********************************
	//     Manipulating columns
	//***********************************

    /**
     * Replaces the column names in the model.  If the number of
     * <code>columnNames</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>columnNames</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded. <p>
     *
     * @param   columnNames  array of column names. 
     *				If <code>null</code>, set
     *                          the model to zero columns
     * @see #setNumRows()
     */
    public function setColumnNames(columnNames:Array):Void {
		setDataNames(dataArray, columnNames); 
    }

    /**
     *  Sets the number of columns in the model.  If the new size is greater
     *  than the current size, new columns are added to the end of the model 
     *  with <code>null</code> cell values.
     *  If the new size is less than the current size, all columns at index
     *  <code>columnCount</code> and greater are discarded. 
     *
     *  @param columnCount  the new number of columns in the model
     *
     *  @see #setColumnCount()
     */
    public function setColumnCount(columnCount:Number):Void { 
    	ArrayUtils.setSize(columnNames, columnCount);
		justifyRows(0, getRowCount()); 
		fireTableStructureChanged();
    } 

    /**
     * addColumn(columnName:Object, columnData:Array)<br>
     * addColumn(columnName:Object)
     * <p>
     * 
     *  Adds a column to the model.  The new column will have the
     *  name <code>columnName</code>.  <code>columnData</code> is the
     *  optional array of data for the column.  If it is <code>null</code>
     *  the column is filled with <code>null</code> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *
     * @param   columnName the name of the column being added
     * @param   columnData       optional data of the column being added
     */
    public function addColumn(columnName:Object, columnData:Array):Void {
        columnNames.push(columnName);
		if (columnData != null) { 
            var columnSize:Number = columnData.length; 
            if (columnSize > getRowCount()) { 
            	ArrayUtils.setSize(dataArray, columnSize);
            }
	    	justifyRows(0, getRowCount()); 
	    	var newColumn:Number = getColumnCount() - 1; 
	    	for(var i:Number = 0; i < columnSize; i++) { 
			  	var row:Array = dataArray[i];
			  	row[newColumn] = columnData[i];
	    	}
		}else { 
	    	justifyRows(0, getRowCount()); 
        }

        fireTableStructureChanged();
    }

	//******************************************
	// Implementing the TableModel interface
	//******************************************

    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public function getRowCount():Number {
        return dataArray.length;
    }

    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public function getColumnCount():Number {
        return columnNames.length;
    }

    /**
     * Returns the column name.
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnNames</code>.
     * If <code>columnNames</code> does not have an entry 
     * for this index, returns the default
     * name provided by the superclass
     */
    public function getColumnName(column:Number):String {
        var id:Object = null; 
		// This test is to cover the case when 
		// getColumnCount has been subclassed by mistake ... 
		if (column < columnNames.length) {  
		    id = columnNames[column]; 
		}
        return (id == null) ? super.getColumnName(column) 
                            : id.toString();
    }

    /**
     * Returns is the row column editable, default is true.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  is the row column editable, default is true.
     * @see #setValueAt()
     * @see #setCellEditable()
     * @see #setAllCellEditable()
     */
    public function isCellEditable(row:Number, column:Number):Boolean {
    	if(columnsEditable[column] == undefined){
        	return true;
    	}else{
    		return columnsEditable[column] == true;
    	}
    }

    /**
     * Returns is the column editable, default is true.
     *
     * @param   column          the column whose value is to be queried
     * @return                  is the column editable, default is true.
     * @see #setValueAt()
     * @see #setCellEditable()
     * @see #setAllCellEditable()
     */
    public function isColumnEditable(column:Number):Boolean {
    	return isCellEditable(null, column);
    }
    
    /**
     * Sets spcecifed column editable or not.
     * @param column the column whose value is to be queried
     * @param editable editable or not
     */
    public function setColumnEditable(column:Number, editable:Boolean):Void{
    	columnsEditable[column] = editable;
    }
    
    /**
     * Sets all cells editable or not.
     * @param editable editable or not
     */
    public function setAllCellEditable(editable:Boolean):Void{
    	for(var i:Number = getColumnCount()-1; i>=0; i--){
    		columnsEditable[i] = editable;
    	}
    }

    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     */
    public function getValueAt(row:Number, column:Number) {
        return dataArray[row][column];
    }

    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.  <code>aValue</code> is the new value.  This method
     * will generate a <code>tableChanged</code> notification.
     *
     * @param   aValue          the new value; this can be null
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     */
    public function setValueAt(aValue, row:Number, column:Number):Void {
        dataArray[row][column] = aValue;
        fireTableCellUpdated(row, column);
    }
	
	public function toString():String{
		return "DefaultTableModel[dataArray:" + dataArray + ",\n names:" + columnNames + "]";
	}	
}