/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

import org.aswing.tree.TreeCell;
import org.aswing.tree.TreeCellFactory;

/**
 * @author iiley
 */
public class GeneralTreeCellFactory implements TreeCellFactory {
		
	private var cellClass:Class;
	
	/**
	 * Creates a GeneralTreeCellFactory with specified cell class.
	 * @param cellClass the cell class
	 */
	public function GeneralTreeCellFactory(cellClass:Class){
		this.cellClass = cellClass;
	}
	
	/**
	 * Creates and returns a new tree cell.
	 * @return the tree cell
	 */
	public function createNewCell():TreeCell{
		return new cellClass();
	}
	
	public function toString():String{
		return "GeneralTreeCellFactory[cellClass:" + cellClass + "]";
	}
}
}