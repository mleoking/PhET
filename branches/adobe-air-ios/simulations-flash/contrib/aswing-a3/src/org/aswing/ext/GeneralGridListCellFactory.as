/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.ext{

/**
 * General factory to generate instance by a class
 */
public class GeneralGridListCellFactory implements GridListCellFactory{
	
	protected var cellClass:Class;
	
	public function GeneralGridListCellFactory(cellClass:Class){
		this.cellClass = cellClass;
	}

	public function createNewGridListCell():GridListCell{
		return new cellClass() as GridListCell;
	}
	
}
}