/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.overflow.ToggleButtonModel;
 
/**
 * The RadioButton model
 * @author Igor Sadovskiy
 */
class org.aswing.RadioButtonModel extends ToggleButtonModel{

    /**
     * Creates a new RadioButton Model
     */
    public function RadioButtonModel() {
    	super();
    	allowUnselectAllInGroup = false;
    }

}
