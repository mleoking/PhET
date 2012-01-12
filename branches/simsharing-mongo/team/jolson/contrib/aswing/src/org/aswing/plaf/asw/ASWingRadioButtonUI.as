/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.plaf.basic.BasicRadioButtonUI;
import org.aswing.plaf.ComponentUI;
/**
 *
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingRadioButtonUI extends BasicRadioButtonUI{
	/*shared instance*/
	private static var asWingRadioButtonUI:ASWingRadioButtonUI;
	
    public static function createInstance(c:Component):ComponentUI {
    	if(asWingRadioButtonUI == null){
    		asWingRadioButtonUI = new ASWingRadioButtonUI();
    	}
        return asWingRadioButtonUI;
    }
    
    public function ASWingRadioButtonUI(){
    	super();
    }	
}
