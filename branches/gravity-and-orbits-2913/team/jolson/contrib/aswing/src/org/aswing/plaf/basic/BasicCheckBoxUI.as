/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.plaf.basic.BasicRadioButtonUI;
import org.aswing.plaf.ComponentUI;
 
/**
 * Basic CheckBox implementation.
 * To implement a Diff CheckBox UI, generally you should:
 * <ul>
 * 	<li>extends your RadioButtonUI to inherite diff features of buttons.
 * 	<li>implement a diff Icon for it, and put it into UI defaults with "CheckBox.icon" as key.
 * 	<li>initialize differnt UI defaults for buttons in your LAF class, for example: Button.border, 
 * 	Button.font, ToggleButton.backgound XxxButton.foreground...
 * </ul>
 * @author iiley
 */
class org.aswing.plaf.basic.BasicCheckBoxUI extends BasicRadioButtonUI{

    private static var propertyPrefix:String = "CheckBox" + ".";
    private static var checkBoxUI:ComponentUI;

    // ********************************
    //          Create PLAF
    // ********************************
    public static function createInstance(c:Component):ComponentUI {
    	if(checkBoxUI == null){
    		checkBoxUI = new BasicCheckBoxUI();
    	}
        return checkBoxUI;
    }

    private function getPropertyPrefix():String {
        return propertyPrefix;
    }
    
    public function BasicCheckBoxUI() {
    	super();
    }	
}
