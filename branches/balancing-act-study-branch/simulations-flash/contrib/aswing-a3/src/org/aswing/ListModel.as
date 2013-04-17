/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.event.ListDataListener;

/**
 * ListMode is a MVC pattern's mode for List UI, different List UI can connected to 
 * a same mode to view the mode's data. When the mode's data changed the mode should
 * fire a event to all its ListDataListeners.
 * @author iiley
 */
public interface ListModel{
	/**
	 * Adds a listener to the list that's notified each time a change to the data model occurs. 
	 */
 	function addListDataListener(l:ListDataListener):void;
    /**
     * Returns the value at the specified index. 
     */
 	function getElementAt(index:int):*;
 	
 	/**
 	 * Returns the length of the list.
 	 */
 	function getSize():int;
    
    /**
     * Removes a listener from the list that's notified each time a change to the data model occurs. 
     */
 	function removeListDataListener(l:ListDataListener):void;
}
}