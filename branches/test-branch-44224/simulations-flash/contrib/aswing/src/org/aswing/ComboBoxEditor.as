/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.IEventDispatcher;

/**
 * The editor component used for JComboBox components.
 * @author iiley
 */
interface org.aswing.ComboBoxEditor extends IEventDispatcher{
	
	/**
	 * Return the component that should be added to the tree hierarchy for
     * this editor.
     * <p>
     * The editor component should can accept normal component event listeners such as 
     * Component.ON_RELEASE, Component.ON_PRESS ...
	 */
	public function getEditorComponent():Component;
	
	/**
	 * Sets whether the editor is editable now.
	 */
	public function setEditable(b:Boolean):Void;
	
	/**
	 * Returns whether the editor is editable now.
	 */
	public function isEditable():Boolean;
	
	/**
	 * Add a listener to listen the editor event when the edited item changes.
	 * <p>
	 * addChangeListener(func:Function)<br>
	 * addChangeListener(func:Function, obj:Object)
	 * <p>
	 * @return the listener added.
	 * @see org.aswing.EventDispatcher#ON_ACT
	 * @see org.aswing.IEventDispatcher#addEventListener()
	 * @see org.aswing.IEventDispatcher#removeEventListener()
	 */
	public function addActionListener(func:Function, obj:Object):Object;
	
	/**
	 * Set the item that should be edited. Cancel any editing if necessary.
	 */
	public function setValue(value:Object):Void;
	
	/**
	 * Return the edited item.
	 */
	public function getValue():Object;
	
	/**
	 * Ask the editor to start editing and to select everything in the editor.
	 */
	public function selectAll():Void;
}
