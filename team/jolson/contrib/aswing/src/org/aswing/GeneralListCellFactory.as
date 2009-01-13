/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ListCell;
import org.aswing.ListCellFactory;

/**
 * GeneralListCellFactory let you can just specified a ListCell implemented class 
 * and other params to create a ListCellFactory
 * @author iiley
 */
class org.aswing.GeneralListCellFactory implements ListCellFactory {
	
	private var listCellClass:Function;
	private var shareCelles:Boolean;
	private var cellHeight:Number;
	private var sameHeight:Boolean;
	
	/**
	 * @param listCellClass the ListCell implementation, for example com.xlands.ui.list.UserListCell
	 * @param shareCelles (optional)is share cells for list items, default is true.
	 * @param sameHeight (optional)is all cells with same height, default is true.
	 * @param height (optional)the height for all cells if sameHeight, if not <code>sameHeight</code>, 
	 * this param can be miss, default is 22.
	 * @see #isShareCells()
	 */
	public function GeneralListCellFactory(listCellClass:Function, shareCelles:Boolean, sameHeight:Boolean, height:Number){
		this.listCellClass = listCellClass;
		
		if(shareCelles == undefined){
			shareCelles = true;
		}
		this.shareCelles = shareCelles;
		if(sameHeight == undefined){
			sameHeight = true;
		}
		this.sameHeight = sameHeight;
		
		if(height == undefined){
			height = 22;
		}
		cellHeight = height;
	}
	
	public function createNewCell() : ListCell {
		return new listCellClass();
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
	 * Sets the height for all cells
	 */
	public function setCellHeight(h:Number):Void{
		cellHeight = h;
	}
	
	/**
	 * Returns the height for all cells
	 * @see ListCellFactory#getCellHeight()
	 */
	public function getCellHeight() : Number {
		return cellHeight;
	}
}