/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table.sorter{

import org.aswing.JTable;
import org.aswing.table.DefaultTextHeaderCell;
import org.aswing.UIManager;

/**
 * @author iiley
 */
public class SortableTextHeaderCell extends DefaultTextHeaderCell{
	
	private var tableSorter:TableSorter;
	
	public function SortableTextHeaderCell(tableSorter:TableSorter) {
		super();
		setBorder(UIManager.getBorder("TableHeader.sortableCellBorder"));
		setBackgroundDecorator(UIManager.getGroundDecorator("TableHeader.sortableCellBackground"));
		this.tableSorter = tableSorter;
		setHorizontalTextPosition(LEFT);
		setIconTextGap(6);
	}
	
	override public function setTableCellStatus(table:JTable, isSelected:Boolean, row:int, column:int):void{
		super.setTableCellStatus(table, isSelected, row, column);
		var modelColumn:int = table.convertColumnIndexToModel(column);
		setIcon(tableSorter.getHeaderRendererIcon(modelColumn, getFont().getSize()-2));
	}
}
}