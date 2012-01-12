/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.overflow.JTable;
import org.aswing.table.DefaultTextCell;
import org.aswing.table.JTableHeader;
import org.aswing.UIManager;

/**
 * Default table header cell to render text
 * @author iiley
 */
class org.aswing.table.DefaultTextHeaderCell extends DefaultTextCell {
	
	public function DefaultTextHeaderCell() {
		super();
		setHorizontalAlignment(CENTER);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setOpaque(false);
	}
	
	public function setTableCellStatus(table:JTable, isSelected:Boolean, row:Number, column:Number):Void{
		var header:JTableHeader = table.getTableHeader();
		if(header != null){
			setBackground(header.getBackground());
			setForeground(header.getForeground());
			setFont(table.getFont());
		}
	}
}