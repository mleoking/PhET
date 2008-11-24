/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.table.sorter.SortableTextHeaderCell;
import org.aswing.table.sorter.TableSorter;
import org.aswing.table.TableCell;
import org.aswing.table.TableCellFactory;

/**
 * @author iiley
 */
class org.aswing.table.sorter.SortableHeaderRenderer implements TableCellFactory {
	
	private var tableSorter:TableSorter;
	private var originalRenderer:TableCellFactory;
	
	public function SortableHeaderRenderer(originalRenderer:TableCellFactory, tableSorter:TableSorter){
		this.originalRenderer = originalRenderer;
		this.tableSorter = tableSorter;
	}
	
	public function createNewCell(isHeader : Boolean) : TableCell {
		return new SortableTextHeaderCell(tableSorter);
	}
	
	public function getTableCellFactory():TableCellFactory{
		return null;
	}
}