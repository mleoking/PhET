/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Cell;
import org.aswing.overflow.JList;

/**
 * @author iiley
 */
interface org.aswing.ListCell extends Cell{
	/**
	 * Sets the table cell status, include the owner-JList, isSelected, the cell index.
	 * @param the cell's owner, a JList
	 * @param isSelected true to set the cell selected, false to set not selected.
	 * @param index the index of the list item
	 */
	public function setListCellStatus(list:JList, isSelected:Boolean, index:Number):Void;
}
