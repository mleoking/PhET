/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.table.TableCell;

/**
 * TableCellFactory for create cells for table
 * @author iiley
 */
interface org.aswing.table.TableCellFactory {
	/**
	 * Creates a new table cell.
	 * @param isHeader is it a header cell
	 * @return the table cell
	 */
	public function createNewCell(isHeader:Boolean):TableCell;
}