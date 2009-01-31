/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractButton;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.basic.BasicToggleButtonUI;
import org.aswing.plaf.ComponentUI;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingToggleButtonUI extends BasicToggleButtonUI{
	/*shared instance*/
	private static var asWingToggleButtonUI:ASWingToggleButtonUI;
	
    public static function createInstance(c:Component):ComponentUI {
    	if(asWingToggleButtonUI == null){
    		asWingToggleButtonUI = new ASWingToggleButtonUI();
    	}
        return asWingToggleButtonUI;
    }
    
    public function ASWingToggleButtonUI(){
    	super();
    }
    
    /**
     * Paint gradient background for AsWing LAF Buttons.
     */
    private function paintBackGround(com:Component, g:Graphics, b:Rectangle):Void{
    	if(com.isOpaque()){
	    	var c:AbstractButton = AbstractButton(com);
			if(c.getModel().isSelected() || c.getModel().isPressed()){
	    		g.fillRectangle(new SolidBrush(com.getBackground().darker(0.9)), b.x, b.y, b.width, b.height);
			}else{
				org.aswing.plaf.asw.ASWingButtonUI.paintASWingLAFButtonBackGround(c, g, b);
			}
    	}
    }    
}
