/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing{

/**
 * The default list cell factory for text cells.
 * @author iiley
 */
public class DefaultListTextCellFactory implements ListCellFactory{
	
	protected var listCellClass:Class;
	protected var shareCelles:Boolean;
	protected var cellHeight:int;
	protected var sameHeight:Boolean;
	
	/**
	 * Create a list cell(with text renderer) factory with a list cell class and other properties.
	 * @param listCellClass the ListCell implementation, for example com.xlands.ui.list.UserListCell
	 * @param shareCelles (optional)is share cells for list items, default is true.
	 * @param sameHeight (optional)is all cells with same height, default is true.
	 * @param height (optional)the height for all cells if sameHeight, if not <code>sameHeight</code>, 
	 * this param can be miss, default is -1, it means will use a sample to count at the first time.
	 * @see #isShareCells()
	 */	
	public function DefaultListTextCellFactory(listCellClass:Class, shareCelles:Boolean=true, sameHeight:Boolean=true, height:int=-1){
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
		if(cellHeight < 0){
			var cell:ListCell = createNewCell();
			cell.setCellValue("JjHhWpqQ1@|");
			cellHeight = cell.getCellComponent().getPreferredSize().height;
		}
		return cellHeight;
	}
	
}
}