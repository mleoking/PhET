/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

import org.aswing.JTable;
import org.aswing.UIManager;

/**
 * Default table header cell to render text
 * @author iiley
 */
public class DefaultTextHeaderCell extends DefaultTextCell{
	
	public function DefaultTextHeaderCell() {
		super();
		setHorizontalAlignment(CENTER);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setBackgroundDecorator(UIManager.getGroundDecorator("TableHeader.cellBackground"));
		setOpaque(false);
	}
	
	override public function setTableCellStatus(table:JTable, isSelected:Boolean, row:int, column:int):void{
		var header:JTableHeader = table.getTableHeader();
		if(header != null){
			setBackground(header.getBackground());
			setForeground(header.getForeground());
			setFont(header.getFont());
		}
	}
}
}