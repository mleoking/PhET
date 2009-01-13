/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractButton;
import org.aswing.ButtonModel;
import org.aswing.util.ArrayUtils;
 
/**
 *
 * @author iiley
 */
class org.aswing.ButtonGroup{
    // the list of buttons participating in this group
    private var buttons:Array;

    /**
	 * The current selection.
	 */
    private var selection:ButtonModel = null;

    /**
	 * Creates a new <code>ButtonGroup</code>.
	 */
    public function ButtonGroup() {
    	buttons = new Array();
    }

    /**
	 * Adds the button to the group.
	 * 
	 * @param b the button to be added
	 */ 
    public function append(b:AbstractButton):Void {
        if(b == null) {
            return;
        }
        buttons.push(b);

        if (b.isSelected()) {
            if (selection == null) {
                selection = b.getModel();
            } else {
                b.setSelected(false);
            }
        }

        b.getModel().setGroup(this);
    }
 
    /**
	 * Removes the button from the group.
	 * 
	 * @param b the button to be removed
	 */ 
    public function remove(b:AbstractButton):Void {
        if(b == null) {
            return;
        }
        ArrayUtils.removeFromArray(buttons, b);
        if(b.getModel() == selection) {
            selection = null;
        }
        b.getModel().setGroup(null);
    }
    
    /**
     * Returns whether the group contains the button.
     * @return true if the group contains the button, false otherwise
     */
    public function contains(b:AbstractButton):Boolean {
    	for(var i:Number=0; i<buttons.length; i++){
    		if(buttons[i] == b){
    			return true;
    		}
    	}
    	return false;
    }

    /**
	 * Returns all the buttons that are participating in this group.
	 * 
	 * @return an <code>Array</code> of the buttons in this group
	 */
    public function getElements():Array {
        return ArrayUtils.cloneArray(buttons);
    }

    /**
	 * Returns the model of the selected button.
	 * 
	 * @return the selected button model
	 */
    public function getSelection():ButtonModel {
        return selection;
    }

    /**
	 * Sets the selected value for the <code>ButtonModel</code>. Only one
	 * button in the group may be selected at a time.
	 * 
	 * @param m the <code>ButtonModel</code>
	 * @param b <code>true</code> if this button is to be selected,
	 *            otherwise <code>false</code>
	 */
    public function setSelected(m:ButtonModel, b:Boolean):Void {
    	if (m != null) {
	        if (b && m != selection) {
	            var oldSelection:ButtonModel = selection;
	            selection = m;
	            if (oldSelection != null) {
	                oldSelection.setSelected(false);
	            }
	            m.setSelected(true);
	        } else if (!b && m == selection && selection.isAllowUnselectAllInGroup()) {
	        	selection = null;	
	        }
    	}
    }

    /**
	 * Returns whether a <code>ButtonModel</code> is selected.
	 * 
	 * @return <code>true</code> if the button is selected, otherwise returns
	 *         <code>false</code>
	 */
    public function isSelected(m:ButtonModel):Boolean {
        return (m == selection);
    }

    /**
	 * Returns the number of buttons in the group.
	 * 
	 * @return the button count
	 */
    public function getButtonCount():Number {
    	return buttons.length;
    }
}
