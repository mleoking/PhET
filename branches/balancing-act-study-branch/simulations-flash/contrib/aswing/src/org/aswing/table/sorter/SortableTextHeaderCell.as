/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.overflow.JTable;
import org.aswing.table.DefaultTextHeaderCell;
import org.aswing.table.sorter.TableSorter;

/**
 * @author iiley
 */
class org.aswing.table.sorter.SortableTextHeaderCell extends DefaultTextHeaderCell {
	
	private var tableSorter:TableSorter;
	
	public function SortableTextHeaderCell(tableSorter:TableSorter) {
		super();
		this.tableSorter = tableSorter;
		setHorizontalTextPosition(LEFT);
		setIconTextGap(6);
	}
	
	public function setTableCellStatus(table:JTable, isSelected:Boolean, row:Number, column:Number):Void{
		super.setTableCellStatus(table, isSelected, row, column);
		var modelColumn:Number = table.convertColumnIndexToModel(column);
		setIcon(tableSorter.getHeaderRendererIcon(modelColumn, getFont().getSize()-2));
	}
}