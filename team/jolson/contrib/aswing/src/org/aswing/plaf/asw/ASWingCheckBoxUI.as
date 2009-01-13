/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.plaf.basic.BasicCheckBoxUI;
import org.aswing.plaf.ComponentUI;
/**
 *
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingCheckBoxUI extends BasicCheckBoxUI{
	/*shared instance*/
	private static var asWingCheckBoxUI:ASWingCheckBoxUI;
	
    public static function createInstance(c:Component):ComponentUI {
    	if(asWingCheckBoxUI == null){
    		asWingCheckBoxUI = new ASWingCheckBoxUI();
    	}
        return asWingCheckBoxUI;
    }
    
    public function ASWingCheckBoxUI(){
    	super();
    }	
}
