/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.JSpacer;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.SpacerUI;

/**
 * A Basic L&F implementation of SpacerUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.plaf.basic.BasicSpacerUI extends SpacerUI {
	
	
	private static var basicSpacerUI:BasicSpacerUI;
    // ********************************
    //          Create Shared PLAF
    // ********************************
    public static function createInstance(c:Component):ComponentUI {
    	if(basicSpacerUI == null){
    		basicSpacerUI = new BasicSpacerUI();
    	}
        return basicSpacerUI;
    }
	
	
	public function BasicSpacerUI(){
	}
	
	public function installUI(c:Component):Void{
		installDefaults(JSpacer(c));
		installListeners(JSpacer(c));
	}
	
	public function uninstallUI(c:Component):Void{
		uninstallDefaults(JSpacer(c));
		uninstallListeners(JSpacer(c));
	}
	
	public function installDefaults(s:JSpacer):Void{
		LookAndFeel.installColors(s, "Spacer.background", "Spacer.foreground");
		LookAndFeel.installBasicProperties(s, "Spacer.");
		LookAndFeel.installBorder(s, "Spacer.border");
	}
	
	public function uninstallDefaults(s:JSpacer):Void{
		LookAndFeel.uninstallBorder(s);
	}
	
	public function installListeners(s:JSpacer):Void{
	}
	
	public function uninstallListeners(s:JSpacer):Void{
	}
	
	public function getPreferredSize(c:Component):Dimension{
		return c.getInsets().getOutsideSize(new Dimension(0, 0));
	}
	
}
