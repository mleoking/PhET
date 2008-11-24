/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.asw.ASWingGraphicsUtils;
import org.aswing.plaf.basic.BasicButtonUI;
import org.aswing.plaf.ComponentUI;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingButtonUI extends BasicButtonUI{
	/*shared instance*/
	private static var asWingButtonUI:ASWingButtonUI;
	
    public static function createInstance(c:Component):ComponentUI {
    	if(asWingButtonUI == null){
    		asWingButtonUI = new ASWingButtonUI();
    	}
        return asWingButtonUI;
    }
    
    public function ASWingButtonUI(){
    	super();
    }
    
    /**
     * Paint gradient background for AsWing LAF Buttons.
     */
    private function paintBackGround(com:Component, g:Graphics, b:Rectangle):Void{
    	var c:AbstractButton = AbstractButton(com);
    	paintASWingLAFButtonBackGround(c, g, b);
    }
    
    public static function paintASWingLAFButtonBackGround(c:AbstractButton, g:Graphics, b:Rectangle):Void{
		var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());

		if(c.isOpaque()){
			if(c.getModel().isPressed() || c.getModel().isSelected()){
				g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
				return;
			}
			ASWingGraphicsUtils.drawControlBackground(g, b, bgColor, Math.PI/2);
		}    	
    }
	
}
