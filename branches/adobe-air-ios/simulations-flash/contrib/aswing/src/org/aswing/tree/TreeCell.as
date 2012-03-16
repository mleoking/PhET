/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Cell;
import org.aswing.overflow.JTree;

/**
 * @author iiley
 */
interface org.aswing.tree.TreeCell extends Cell {
	/**
	 * Sets the table cell status, include the owner-JTable isSelected, row position, column position.
	 * @param the cell's owner, a JTable
	 * @param seleted true indicated the cell selected, false not selected.
	 * @param expanded true the node is currently expanded, false not.
	 * @param leaf true the node represets a leaf, false not.
	 * @param row the row position
	 */
	public function setTreeCellStatus(tree:JTree, selected:Boolean, expanded:Boolean, leaf:Boolean, row:Number):Void;	
}