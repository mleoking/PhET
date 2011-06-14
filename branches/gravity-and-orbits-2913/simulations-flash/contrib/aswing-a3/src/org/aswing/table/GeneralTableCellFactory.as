/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

/**
 * @author iiley
 */
public class GeneralTableCellFactory implements TableCellFactory{
	
	private var cellClass:Class;
	
	/**
	 * Creates a TableCellFactory with specified cell class.
	 * @param cellClass the cell class
	 */
	public function GeneralTableCellFactory(cellClass:Class){
		this.cellClass = cellClass;
	}
	
	/**
	 * Creates and returns a new table cell.
	 * @param isHeader is it a header cell
	 * @return the table cell
	 */
	public function createNewCell(isHeader:Boolean):TableCell{
		return new cellClass();
	}
	
	public function toString():String{
		return "GeneralTableCellFactory[cellClass:" + cellClass + "]";
	}
}
}