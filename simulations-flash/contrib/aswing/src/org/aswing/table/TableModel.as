﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.event.TableModelListener;

/**
 *  The <code>TableModel</code> interface specifies the methods the
 *  <code>JTable</code> will use to interrogate a tabular data model. <p>
 *  
 * @author iiley
 */
interface org.aswing.table.TableModel {
	
    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount()
     */
    public function getRowCount():Number;

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount()
     */
    public function getColumnCount():Number;

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of the column
     * @return  the name of the column
     */
    public function getColumnName(columnIndex:Number):String;

    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the <code>JTable</code> to set up a 
     * default renderer and editor for the column.
     *
     * @param columnIndex  the index of the column
     * @return the common ancestor class(name) of the object values in the model.
     */
    public function getColumnClass(columnIndex:Number):String;

    /**
     * Returns true if the cell at <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     *
     * @param	rowIndex	the row whose value to be queried
     * @param	columnIndex	the column whose value to be queried
     * @return	true if the cell is editable
     * @see #setValueAt()
     */
    public function isCellEditable(rowIndex:Number, columnIndex:Number):Boolean;

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex 	the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
    public function getValueAt(rowIndex:Number, columnIndex:Number);
	
    /**
     * Sets the value in the cell at <code>columnIndex</code> and
     * <code>rowIndex</code> to <code>aValue</code>.
     *
     * @param	aValue		 the new value
     * @param	rowIndex	 the row whose value is to be changed
     * @param	columnIndex 	 the column whose value is to be changed
     * @see #getValueAt()
     * @see #isCellEditable()
     */
    public function setValueAt(aValue, rowIndex:Number, columnIndex:Number):Void;

    /**
     * Adds a listener to the list that is notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public function addTableModelListener(l:TableModelListener):Void;

    /**
     * Removes a listener from the list that is notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public function removeTableModelListener(l:TableModelListener):Void;
}