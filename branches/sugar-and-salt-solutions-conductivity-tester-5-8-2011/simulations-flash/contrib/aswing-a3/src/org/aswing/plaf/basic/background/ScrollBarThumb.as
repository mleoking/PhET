/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.background
{
	
import org.aswing.graphics.*;
import org.aswing.*;
import org.aswing.geom.IntRectangle;
import flash.display.DisplayObject;
import org.aswing.plaf.*;
import flash.display.Sprite;
import org.aswing.geom.IntDimension;
import flash.events.*;
import org.aswing.event.*;
import org.aswing.plaf.basic.BasicGraphicsUtils;

/**
 * The thumb decorator for JScrollBar.
 * @author iiley
 * @private
 */
public class ScrollBarThumb implements GroundDecorator, UIResource
{
	private var thumbHighlightColor:ASColor;
    private var thumbLightHighlightColor:ASColor;
    private var thumbLightShadowColor:ASColor;
    private var thumbDarkShadowColor:ASColor;
    private var thumbColor:ASColor;
    private var thumb:AWSprite;
    private var size:IntDimension;
    private var verticle:Boolean;
        
	protected var rollover:Boolean;
	protected var pressed:Boolean;
    
	public function ScrollBarThumb(){
		thumb = new AWSprite();
		rollover = false;
		pressed = false;
		initSelfHandlers();
	}
	
	private function reloadColors(ui:ComponentUI):void{
		thumbHighlightColor = ui.getColor("ScrollBar.thumbHighlight");
		thumbLightHighlightColor = ui.getColor("ScrollBar.thumbLightHighlight");
		thumbLightShadowColor = ui.getColor("ScrollBar.thumbShadow");
		thumbDarkShadowColor = ui.getColor("ScrollBar.thumbDarkShadow");
		thumbColor = ui.getColor("ScrollBar.thumbBackground");
	}
	
	public function updateDecorator(c:Component, g:Graphics2D, bounds:IntRectangle):void{
		if(thumbColor == null){
			reloadColors(c.getUI());
		}
		thumb.x = bounds.x;
		thumb.y = bounds.y;
		size = bounds.getSize();
		var sb:JScrollBar = JScrollBar(c);
		verticle = (sb.getOrientation() == JScrollBar.VERTICAL);
		paint();
	}
	
	private function paint():void{
    	var w:Number = size.width;
    	var h:Number = size.height;
    	thumb.graphics.clear();
    	var g:Graphics2D = new Graphics2D(thumb.graphics);
    	var rect:IntRectangle = new IntRectangle(0, 0, w, h);
    	
    	if(pressed){
    		g.fillRectangle(new SolidBrush(thumbColor), rect.x, rect.y, rect.width, rect.height);
    	}else{
	    	BasicGraphicsUtils.drawControlBackground(g, rect, thumbColor, 
	    		verticle ? 0 : Math.PI/2);
    	}
    	
    	BasicGraphicsUtils.drawBezel(g, rect, pressed, 
    		thumbLightShadowColor, 
    		thumbDarkShadowColor, 
    		thumbHighlightColor, 
    		thumbLightHighlightColor
    		);
    		
    		
    	var p:Pen = new Pen(thumbDarkShadowColor, 0);
    	if(verticle){
	    	var ch:Number = h/2.0;
	    	g.drawLine(p, 4, ch, w-4, ch);
	    	g.drawLine(p, 4, ch+2, w-4, ch+2);
	    	g.drawLine(p, 4, ch-2, w-4, ch-2);
    	}else{
	    	var cw:Number = w/2.0;
	    	g.drawLine(p, cw, 4, cw, h-4);
	    	g.drawLine(p, cw+2, 4, cw+2, h-4);
	    	g.drawLine(p, cw-2, 4, cw-2, h-4);
    	}		
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return thumb;
	}

	private function initSelfHandlers():void{
		thumb.addEventListener(MouseEvent.ROLL_OUT, __rollOutListener);
		thumb.addEventListener(MouseEvent.ROLL_OVER, __rollOverListener);
		thumb.addEventListener(MouseEvent.MOUSE_DOWN, __mouseDownListener);
		thumb.addEventListener(ReleaseEvent.RELEASE, __mouseUpListener);
	}
	
	private function __rollOverListener(e:Event):void{
		rollover = true;
		paint();
	}
	private function __rollOutListener(e:Event):void{
		rollover = false;
		if(!pressed){
			paint();
		}
	}
	private function __mouseDownListener(e:Event):void{
		pressed = true;
		paint();
	}
	private function __mouseUpListener(e:Event):void{
		if(pressed){
			pressed = false;
			paint();
		}
	}
}

}