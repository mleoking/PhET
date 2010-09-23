/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * The editor component used for JComboBox components.
 * @author iiley
 */
public interface ComboBoxEditor{
	
	/**
	 * Return the component that performance the editing asset.
	 * @return the editor component
	 */
	function getEditorComponent():Component;
	
	/**
	 * Sets whether the editor is editable now.
	 */
	function setEditable(b:Boolean):void;
	
	/**
	 * Returns whether the editor is editable now.
	 */
	function isEditable():Boolean;
		
	/**
	 * Adds a listener to listen the editor event when the edited item changes.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	function addActionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void;
	
	/**
	 * Removes a action listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	function removeActionListener(listener:Function):void;		
	
	/**
	 * Set the item that should be edited. Cancel any editing if necessary.
	 */
	function setValue(value:*):void;
	
	/**
	 * Return the edited item.
	 */
	function getValue():*;
	
	/**
	 * Ask the editor to start editing and to select everything in the editor.
	 */
	function selectAll():void;
}
}