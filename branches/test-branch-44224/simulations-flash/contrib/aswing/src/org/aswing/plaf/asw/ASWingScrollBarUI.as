/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.asw.ASWingGraphicsUtils;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.basic.BasicScrollBarUI;

/**
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingScrollBarUI extends BasicScrollBarUI {
	
	public function ASWingScrollBarUI(){
	}
	
    /**
     * LAF notice.
     * 
     * Override this method to paint diff thumb in your LAF.
     */
    private function paintThumb(thumMC:MovieClip, size:Dimension, isPressed:Boolean):Void{
    	var w:Number = size.width;
    	var h:Number = size.height;
    	thumMC.clear();
    	var g:Graphics = new Graphics(thumMC);
    	var rect:Rectangle = new Rectangle(0, 0, w, h);
    	
    	if(isPressed){
    		g.fillRectangle(new SolidBrush(thumbColor), rect.x, rect.y, rect.width, rect.height);
    	}else{
	    	ASWingGraphicsUtils.drawControlBackground(g, rect, thumbColor, 
	    		isVertical() ? 0 : Math.PI/2);
    	}
    	
    	BasicGraphicsUtils.drawBezel(g, rect, isPressed, 
    		thumbLightShadowColor, 
    		thumbDarkShadowColor, 
    		thumbHighlightColor, 
    		thumbLightHighlightColor
    		);
    		
    		
    	var p:Pen = new Pen(thumbDarkShadowColor, 0);
    	if(isVertical()){
	    	var ch:Number = h/2;
	    	g.drawLine(p, 4, ch, w-4, ch);
	    	g.drawLine(p, 4, ch+2, w-4, ch+2);
	    	g.drawLine(p, 4, ch-2, w-4, ch-2);
    	}else{
	    	var cw:Number = w/2;
	    	g.drawLine(p, cw, 4, cw, h-4);
	    	g.drawLine(p, cw+2, 4, cw+2, h-4);
	    	g.drawLine(p, cw-2, 4, cw-2, h-4);
    	}
    }	
}