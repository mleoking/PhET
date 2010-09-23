/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
/**
 * GeneralListCellFactory let you can just specified a ListCell implemented class 
 * and other params to create a ListCellFactory
 * @author iiley
 */	
public class GeneralListCellFactory implements ListCellFactory{

	private var listCellClass:Class;
	private var shareCelles:Boolean;
	private var cellHeight:int;
	private var sameHeight:Boolean;
	
	/**
	 * Create a list cell factory with a list cell class and other properties.
	 * @param listCellClass the ListCell implementation, for example com.xlands.ui.list.UserListCell
	 * @param shareCelles (optional)is share cells for list items, default is true.
	 * @param sameHeight (optional)is all cells with same height, default is true.
	 * @param height (optional)the height for all cells if sameHeight, if not <code>sameHeight</code>, 
	 * this param can be miss, default is 22.
	 * @see #isShareCells()
	 */
	public function GeneralListCellFactory(listCellClass:Class, shareCelles:Boolean=true, sameHeight:Boolean=true, height:int=22){
		this.listCellClass = listCellClass;
		this.shareCelles = shareCelles;
		this.sameHeight = sameHeight;
		
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
	public function setCellHeight(h:int):void{
		cellHeight = h;
	}
	
	/**
	 * Returns the height for all cells
	 * @see ListCellFactory#getCellHeight()
	 */
	public function getCellHeight() : int {
		return cellHeight;
	}
	
}
}