/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ButtonGroup;
import org.aswing.DefaultButtonModel;
 
/**
 * The ToggleButton model
 * @author iiley
 */
class org.aswing.overflow.ToggleButtonModel extends DefaultButtonModel{

    /**
     * Creates a new ToggleButton Model
     */
    public function ToggleButtonModel() {
    	super();
    }

    /**
     * Sets the selected state of the button.
     * @param b true selects the toggle button,
     *          false deselects the toggle button.
     */
    public function setSelected(b:Boolean):Void {
        var group:ButtonGroup = getGroup();
        if (group != null) {
            // use the group model instead
            group.setSelected(this, b);
            b = group.isSelected(this);
        }
        super.setSelected(b);
    }
    
    /**
     * Sets the button to released or unreleased.
     */
	public function setReleased(b:Boolean):Void{
        if((released == b) || !isEnabled()) {
            return;
        }
        
        if (b && isRollOver()) {
            setSelected(!isSelected());
        }
        
        released = b;
        if(released){
        	pressed = false;
        }
            
        fireStateChanged();
	}
}
