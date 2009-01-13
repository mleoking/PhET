/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.asw.ASWingGraphicsUtils;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.basic.BasicSliderUI;

/**
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingSliderUI extends BasicSliderUI {
	
	public function ASWingSliderUI(){
		super();
	}
	
    private function paintThumb(g:Graphics, size:Dimension):Void{
    	var x:Number = 2;
    	var y:Number = 2;
    	var rw:Number = size.width - 4;
    	var rh:Number = size.height - 4;
    	var rect:Rectangle = new Rectangle(x, y, rw, rh);
    	var enabled:Boolean = slider.isEnabled();
    	
    	var borderC:ASColor = enabled ? thumbDarkShadowColor : thumbColor;
    	var fillC:ASColor = enabled ? thumbColor : thumbColor;
    	if(!enabled){
	    	g.beginDraw(new Pen(borderC));
	    	g.beginFill(new SolidBrush(fillC));
	    	g.rectangle(x, y, rw, rh);
	    	g.endFill();
	    	g.endDraw();
    	}else{
    		ASWingGraphicsUtils.drawControlBackground(
    			g, 
    			rect, 
    			fillC,
    			!isSliderVertical() ? 0 : Math.PI/2);
			g.drawRectangle(new Pen(borderC), x, y, rw, rh);
    	}
    }	
}