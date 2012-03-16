/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ButtonGroup;
import org.aswing.IEventDispatcher;

/**
 * State Model for buttons.
 * This model is used for check boxes and radio buttons, which are
 * special kinds of buttons, as well as for normal buttons.
 * For check boxes and radio buttons, pressing the mouse selects
 * the button. 
 * <p>
 * In use, a UI will invoke {@link #setSelected} when a mouse
 * click occurs over a check box or radio button. 
 * 
 * @author iiley
 */
interface org.aswing.ButtonModel extends IEventDispatcher{
	
	/**
	 * Add a listener to listen the Model's change event.
	 * <p>
	 * When the button's state changed, the state is all about:
	 * <ul>
	 * <li>enabled
	 * <li>rollOver
	 * <li>pressed
	 * <li>released
	 * <li>selected
	 * </ul>
	 *<br>
	 * onStateChanged(source:ButtonModel)
	 * @see org.aswing.Component#ON_STATE_CHANGED
	 * @return the listener added.
	 * @see EventDispatcher#addEventListener()
	 */
	public function addChangeListener(func:Function, obj:Object):Object;
	
	/**
	 * Add a listener to listen the Model's selection change event.
	 * When the button's selection changed, fired when diselected or selected.
	 *<br>
	 * onSelectionChanged(source:ButtonModel)
	 * @see org.aswing.Component#ON_SELECTION_CHANGED
	 * @return the listener added.
	 * @see EventDispatcher#addEventListener()
	 */	
	public function addSelectionListener(func:Function, obj:Object):Object;
	
	/**
	 * Indicates if the button can be selected or pressed by an input device (such as a mouse pointer).
	 */
	public function isEnabled():Boolean;
	
	/**
	 * Indicates that the mouse is over the button.
	 */
	public function isRollOver():Boolean;
	
	/**
	 * Indicates that the button has been pressed the and not be released yet.
	 * <p><b>This is not same as Java Swing's, this is some like Java Swing's Armed</b>
	 */
	public function isPressed():Boolean;
		
	/**
	 * Indicates if button has been released. This is some like java Swing's pressed when released inside.
	 */
	public function isReleased():Boolean;
		
	/**
	 * Indicates if the button has been selected.
	 */
	public function isSelected():Boolean;
	
	
	/**
	 * Enables or disables the button.
	 */
	public function setEnabled(b:Boolean):Void;
	
	/**
	 * Sets or clears the button's rollover state.
	 */
	public function setRollOver(b:Boolean):Void;
	
	/**
	 * Sets the button to being pressed or unpressed.
	 */
	public function setPressed(b:Boolean):Void;
	
	/**
	 * Sets the button to released or unreleased.
	 */
	public function setReleased(b:Boolean):Void;
	
	/**
	 * Selects or deselects the button.
	 */
	public function setSelected(b:Boolean):Void;
	
    /**
     * Identifies the group this button belongs to --
     * needed for radio buttons, which are mutually
     * exclusive within their group.
     *
     * @param group the ButtonGroup this button belongs to
     */
	public function setGroup(group:ButtonGroup):Void;

    /**
     * Sets if selected element can change its status to unselected
     * then in group. In other words, if no slected elements in group is
     * allowed.
     * 
     * @param allow true if no selected elements in group is allowed and false if not
     * @see #isAllowUnselectAllInGroup
     */
    public function setAllowUnselectAllInGroup(allow:Boolean):Void;
    
    /**
     * Checks if selected element can change its status to unselected
     * then in group. In other words, if no slected elements in group is
     * allowed.
     * 
     * @return true if no selected elements in group is allowed
     * @see #setAllowUnselectAllInGroup
     */
    public function isAllowUnselectAllInGroup():Boolean;
	
}
