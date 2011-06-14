/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.event.ListDataListener;

/**
 * ListMode is a MVC pattern's mode for List UI, different List UI can connected to 
 * a same mode to view the mode's data. When the mode's data changed the mode should
 * fire a event to all its ListDataListeners.
 */
interface org.aswing.ListModel{
	/**
	 * Adds a listener to the list that's notified each time a change to the data model occurs. 
	 */
 	public function addListDataListener(l:ListDataListener):Void;
    /**
     * Returns the value at the specified index. 
     */
 	public function getElementAt(index:Number):Object;
 	
 	/**
 	 * Returns the length of the list.
 	 */
 	public function getSize():Number;
    
    /**
     * Removes a listener from the list that's notified each time a change to the data model occurs. 
     */
 	public function removeListDataListener(l:ListDataListener):Void;
}
