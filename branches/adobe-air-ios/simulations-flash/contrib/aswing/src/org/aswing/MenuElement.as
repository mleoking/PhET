/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;

/**
 * Any component that can be placed into a menu should implement this interface.
 * This interface is used by <code>MenuSelectionManager</code>
 * to handle selection and navigation in menu hierarchies.
 * 
 * @author iiley
 */
interface org.aswing.MenuElement {
	
	/**
     * Call by the <code>MenuSelectionManager</code> when the
     * <code>MenuElement</code> is added or remove from 
     * the menu selection.
     */
    public function menuSelectionChanged(isIncluded:Boolean):Void;

    /**
     * This method should return an array containing the sub-elements for the receiving menu element
     *
     * @return an array of MenuElements
     */
    public function getSubElements():Array; //MenuElement[]
    
    /**
     * Precess the selection when key typed. 
     */
    public function processKeyEvent(code:Number):Void;
    
    /**
     * Sets whether the menu element is in use.
     */
    public function setInUse(b:Boolean):Void;
    
    /**
     * Returns whether the menu element is in use or not.
     */
    public function isInUse():Boolean;
    
    /**
     * This method should return the Component used to paint the receiving element.
     * The returned component will be used to convert events and detect if an event is inside
     * a MenuElement's component.
     *
     * @return the Component value
     */
    public function getMenuComponent():Component;
}