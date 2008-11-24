/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.DefaultListCell;
import org.aswing.ListCell;
import org.aswing.ListCellFactory;

/**
 * @author iiley
 */
class org.aswing.DefaultListCellFactory implements ListCellFactory {
	
	private var shareCelles:Boolean;
	private var sameHeight:Boolean;
	private var cellHeight:Number;
	
	/**
	 * @param shareCelles is share cells for list items.
	 * @see #isShareCells()
	 */
	public function DefaultListCellFactory(shareCelles:Boolean, sameHeight:Boolean){
		if(shareCelles == undefined){
			shareCelles = true;
		}
		if(sameHeight == undefined){
			sameHeight = true;	
		}
		this.shareCelles = shareCelles;
		this.sameHeight = sameHeight;
		cellHeight = -1;
	}
	
	public function createNewCell() : ListCell {
		return new DefaultListCell();
	}
	
	/**
	 * @return is same height for items
	 * @see ListCellFactory#isAllCellHasSameHeight()
	 */
	public function isAllCellHasSameHeight() : Boolean {
		return sameHeight;
	}
	
	/**
	 * @return is share cells for items.
	 * @see ListCellFactory#isShareCells()
	 */
	public function isShareCells() : Boolean {
		return shareCelles;
	}
	
	/**
	 * Returns the height for all cells.
	 * @see ListCellFactory#getCellHeight()
	 */
	public function getCellHeight() : Number {
		if(cellHeight < 0){
			var cell:ListCell = createNewCell();
			cell.setCellValue("JjHhWpqQ1@|");
			cellHeight = cell.getCellComponent().getPreferredSize().height;
		}
		return cellHeight;
	}

}
