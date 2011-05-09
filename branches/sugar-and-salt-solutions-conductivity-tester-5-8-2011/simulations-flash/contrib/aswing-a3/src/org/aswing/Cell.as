/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * Complex component cell base class, like JList, JTable's cell.
 * @author iiley
 */
public interface Cell{
	/**
	 * Sets the value of this cell.
	 * @param value which should represent on the component of this cell.
	 */
	function setCellValue(value:*):void;
	
	/**
	 * Returns the value of the cell.
	 * @return the value of the cell.
	 */
	function getCellValue():*;
				
	/**
	 * Return the represent component of this cell.
	 * @return the cell component.
	 */
	function getCellComponent():Component;
}
}