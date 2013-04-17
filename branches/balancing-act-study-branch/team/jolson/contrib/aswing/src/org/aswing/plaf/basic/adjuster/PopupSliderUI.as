/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Insets;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.basic.BasicSliderUI;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.adjuster.PopupSliderUI extends BasicSliderUI {
	
	public function PopupSliderUI() {
		super();
	}
	
	private function installDefaults():Void{
		super.installDefaults();
		slider.setOpaque(true);
	}
	
	//do not paint progress
    private function paintTrackProgress(g:Graphics, trackDrawRect:Rectangle):Void{
    }
    
    public function getTrackMargin():Insets{
    	calculateThumbSize(thumbRect);
    	calculateTrackBuffer();
    	var insets:Insets = slider.getInsets();
    	if(isSliderVertical()){
    		insets.top += trackBuffer;
    		insets.bottom += trackBuffer;
    	}else{
    		insets.left += trackBuffer;
    		insets.right += trackBuffer;
    	}
    	return insets;
    }
    
    private function paintTrack(g:Graphics, trackRect:Rectangle):Void{
    	var drawRect:Rectangle = getTrackDrawRect(trackRect);
    	if(slider.isEnabled()){
    		progressMC.clear();
    		paintTrackProgress(new Graphics(progressMC), drawRect);
    		BasicGraphicsUtils.paintLoweredBevel(g, drawRect, shadowColor, darkShadowColor, lightColor, highlightColor);
    	}else{
	    	g.beginDraw(new Pen(lightColor, 1));
	    	//g.beginFill(new SolidBrush(darkShadowColor));
	    	drawRect.grow(-1, -1);
	    	g.rectangle(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
	    	g.endDraw();
	    	//g.endFill();
    	}
    }
    
    private function getTrackDrawRect(trackRect:Rectangle):Rectangle{
    	var tx:Number = trackRect.x;
    	var ty:Number = trackRect.y;
    	var tw:Number = trackRect.width;
    	var th:Number = trackRect.height;
    	var halfTrackLength:Number = 3;
    	
    	var drawRect:Rectangle;
    	if(isSliderVertical()){
    		tx += Math.floor(tw/2-halfTrackLength);
    		drawRect = new Rectangle(tx, ty, halfTrackLength*2, th);
    	}else{
    		ty += Math.floor(th/2-halfTrackLength);
    		drawRect = new Rectangle(tx, ty, tw, halfTrackLength*2);
    	}
    	return drawRect;
    }    
    
    private function calculateThumbSize(thumbRect:Rectangle):Void{
        if (isSliderVertical()) {
		    thumbRect.width = 12;
		    thumbRect.height = 8;
		}else{
		    thumbRect.width = 8;
		    thumbRect.height = 12;
		}
    }
     //--------------------------Dimensions----------------------------

    public function getPreferredSize(c:Component):Dimension{
    	var size:Dimension;
    	if(isSliderVertical()){
    		size = new Dimension(12, 100);
    	}else{
    		size = new Dimension(100, 12);
    	}
    	addTickSize(size);
    	return c.getInsets().getOutsideSize(size);
    }

    public function getMinimumSize(c:Component):Dimension{
    	var size:Dimension;
    	if(isSliderVertical()){
    		size = new Dimension(12, 36);
    	}else{
    		size = new Dimension(36, 12);
    	}
    	addTickSize(size);
    	return c.getInsets().getOutsideSize(size);
    }        
}