/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.table.TableCell;
import org.aswing.table.TableCellFactory;

/**
 * @author iiley
 */
class org.aswing.table.GeneralTableCellFactory implements TableCellFactory{
	
	private var cellClass:Function;
	
	/**
	 * GeneralTableCellFactory(cellClass:Function)<br>
	 * <p>
	 * Creates a TableCellFactory with specified cell class.
	 * @param cellClass the cell class
	 */
	public function GeneralTableCellFactory(cellClass:Function){
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