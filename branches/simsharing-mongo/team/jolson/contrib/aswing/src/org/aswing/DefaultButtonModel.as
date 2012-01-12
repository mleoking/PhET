/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractButton;
import org.aswing.ButtonGroup;
import org.aswing.ButtonModel;
import org.aswing.EventDispatcher;
 
 
/**
 * The default implementation of a Button component's data model.
 * @author iiley
 */
class org.aswing.DefaultButtonModel extends EventDispatcher implements ButtonModel{

	private var group:ButtonGroup;
	private var allowUnselectAllInGroup:Boolean;
	
	private var enabled:Boolean;
	private var rollOver:Boolean;
	private var pressed:Boolean;
	private var released:Boolean;
	private var selected:Boolean;
	
	public function DefaultButtonModel(){
		super();
		enabled = true;
		rollOver = false;
		pressed = false;
		released = false;
		selected = false;
		allowUnselectAllInGroup = true;
	}
	
	/**
	 * Indicates that the button has been pressed the and not be released yet.
	 * <p><b>This is not same as Java Swing's, this is some like Java Swing's Armed</b>
	 */
	public function isPressed():Boolean{
        return pressed;
	}
		
	/**
	 * Indicates if button has been released. This is some like java Swing's pressed when released inside.
	 */
	public function isReleased():Boolean{
        return released;
	}
	        
    /**
     * Indicates if the button has been selected. Only needed for
     * certain types of buttons - such as RadioButton or Checkbox.
     *
     * @return true if the button is selected
     */
    public function isSelected():Boolean {
        return selected;
    }
        
    /**
     * Indicates whether the button can be selected or pressed by
     * an input device (such as a mouse pointer). (Checkbox-buttons
     * are selected, regular buttons are "pressed".)
     *
     * @return true if the button is enabled, and therefore
     *         selectable (or pressable)
     */
    public function isEnabled():Boolean {
        return enabled;
    }
                
    /**
     * Indicates that the mouse is over the button.
     *
     * @return true if the mouse is over the button
     */
    public function isRollOver():Boolean {
        return rollOver;
    }
        
    /**
     * Sets the button to released or unreleased.
     */
	public function setReleased(b:Boolean):Void{
        if((released == b) || !isEnabled()) {
            return;
        }
            
        released = b;
        if(released){
        	pressed = false;
        }
            
        fireStateChanged();		
	}
		     

    /**
     * Enables or disables the button.
     * 
     * @param b true to enable the button
     * @see #isEnabled()
     */
    public function setEnabled(b:Boolean):Void {
        if(enabled == b) {
            return;
        }
            
        enabled = b;
        if (!b) {
            pressed = false;
        }

            
        fireStateChanged();
    }
        
    /**
     * Selects or deselects the button.
     *
     * @param b true selects the button,
     *          false deselects the button
     */
    public function setSelected(b:Boolean):Void {
        if (selected == b) {
            return;
        }

        selected = b;
		
        fireStateChanged();
        fireSelectionChanged();
    }
        
        
    /**
     * Sets the button to pressed or unpressed.
     * 
     * @param b true to set the button to "pressed"
     * @see #isPressed()
     */
    public function setPressed(b:Boolean):Void {
        if((pressed == b) || !isEnabled()) {
            return;
        }
        
        pressed = b;
        if(pressed){
        	released = false;
        }
        
        fireStateChanged();
    }   

    /**
     * Sets or clears the button's rollover state
     * 
     * @param b true to turn on rollover
     * @see #isRollOver()
     */
    public function setRollOver(b:Boolean):Void {
        if((rollOver == b) || !isEnabled()) {
            return;
        }
        
        rollOver = b;

        fireStateChanged();
    }
	
	/**
     * Identifies the group this button belongs to --
     * needed for radio buttons, which are mutually
     * exclusive within their group.
     *
     * @param group the <code>ButtonGroup</code> this button belongs to
     */
    public function setGroup(group:ButtonGroup):Void {
        this.group = group;
    }

    /**
     * Returns the group that this button belongs to.
     * Normally used with radio buttons, which are mutually
     * exclusive within their group.
     *
     * @return a <code>ButtonGroup</code> that this button belongs to
     */
    public function getGroup():ButtonGroup {
        return group;
    }
    
    /**
     * Sets if selected element can change its status to unselected
     * then in group. In other words, if no slected elements in group is
     * allowed.
     * 
     * @param allow true if no selected elements in group is allowed and false if not
     * @see #isAllowUnselectAllInGroup
     */
    public function setAllowUnselectAllInGroup(allow:Boolean):Void{
    	allowUnselectAllInGroup = allow;	
    }
    
    /**
     * Checks if selected element can change its status to unselected
     * then in group. In other words, if no slected elements in group is
     * allowed.
     * 
     * @return true if no selected elements in group is allowed
     * @see #setAllowUnselectAllInGroup
     */
    public function isAllowUnselectAllInGroup():Boolean{
    	return allowUnselectAllInGroup;	
    }
    
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(AbstractButton.ON_STATE_CHANGED, func, obj);
	}

	public function addSelectionListener(func:Function, obj:Object):Object{
		return addEventListener(AbstractButton.ON_SELECTION_CHANGED, func, obj);
	}
        
    private function fireStateChanged():Void{
    	dispatchEvent(createEventObj(AbstractButton.ON_STATE_CHANGED));
    }
	private function fireSelectionChanged() : Void {
		dispatchEvent(createEventObj(AbstractButton.ON_SELECTION_CHANGED));
	}
	
	public function toString():String{
		return "org.aswing.DefaultButtonModel[]";
	}
}
