import org.aswing.ListCell;
import org.aswing.ListCellFactory;

import test.list.IconListCell;

/**
 * @author iiley
 */
class test.list.IconListCellFactory implements ListCellFactory {
	
	
	private var shareCelles:Boolean;
	private var cellHeight:Number;
	private var sameHeight:Boolean;
	
	/**
	 * @param shareCelles is share cells for list items.
	 * @see #isShareCells()
	 */
	public function IconListCellFactory(shareCelles:Boolean, sameHeight:Boolean){
		if(shareCelles == undefined){
			shareCelles = true;
		}
		this.shareCelles = shareCelles;
		if(sameHeight == undefined){
			sameHeight = true;
		}
		this.sameHeight = sameHeight;
		cellHeight = 40;
	}
	
	public function createNewCell() : ListCell {
		return new IconListCell();
	}
	
	/**
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
		return cellHeight;
	}

}