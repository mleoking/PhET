/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Cell;
import org.aswing.overflow.JTable;

/**
 * @author iiley
 */
interface org.aswing.table.TableCell extends Cell{
	/**
	 * Sets the table cell status, include the owner-JTable isSelected, row position, column position.
	 * @param the cell's owner, a JTable
	 * @param isSelected true to set the cell selected, false to set not selected.
	 * @param row the row position
	 * @param column the column position
	 */
	public function setTableCellStatus(table:JTable, isSelected:Boolean, row:Number, column:Number):Void;
}