/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.border{

import flash.display.*;
import flash.text.*;

import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.util.HashMap;

/**
 * CaveBorder, a border with a cave line rectangle(or roundrect).
 * It is like a TitledBorder with no title. :)
 */
public class CaveBorder extends DecorateBorder{
	
	public static function get DEFAULT_LINE_COLOR():ASColor{
		return ASColor.GRAY;
	}
	public static function get DEFAULT_LINE_LIGHT_COLOR():ASColor{
		return ASColor.WHITE;
	}
	public static const DEFAULT_LINE_THICKNESS:int = 1;
				
	private var round:Number;
	private var lineColor:ASColor;
	private var lineLightColor:ASColor;
	private var lineThickness:Number;
	private var beveled:Boolean;
	
	/**
	 * Create a cave border.
	 * @param interior the interior border.
	 * @param round round rect radius, default is 0 means normal rectangle, not rect.
	 * @see org.aswing.border.TitledBorder
	 * @see #setLineColor()
	 * @see #setLineThickness()
	 * @see #setBeveled()
	 */
	public function CaveBorder(interior:Border=null, round:Number=0){
		super(interior);
		this.round = round;
		
		lineColor = DEFAULT_LINE_COLOR;
		lineLightColor = DEFAULT_LINE_LIGHT_COLOR;
		lineThickness = DEFAULT_LINE_THICKNESS;
		beveled = true;
	}
	
	override public function updateBorderImp(c:Component, g:Graphics2D, bounds:IntRectangle):void{
    	var x1:Number = bounds.x + lineThickness*0.5;
    	var y1:Number = bounds.y + lineThickness*0.5;
    	var w:Number = bounds.width - lineThickness;
    	var h:Number = bounds.height - lineThickness;
    	if(beveled){
    		w -= lineThickness;
    		h -= lineThickness;
    	}
    	var x2:Number = x1 + w;
    	var y2:Number = y1 + h;
    	
    	var textR:IntRectangle = new IntRectangle(bounds.x+bounds.width/2, bounds.y, 0, 0);
    	
    	var pen:Pen = new Pen(lineColor, lineThickness);
    	if(round <= 0){
			//draw dark rect
			g.beginDraw(pen);
			g.moveTo(x1, y1);
			g.lineTo(x1, y2);
			g.lineTo(x2, y2);
			g.lineTo(x2, y1);
			g.lineTo(x1, y1);
			g.endDraw();
			if(beveled){
    			//draw hightlight
    			pen.setColor(lineLightColor);
    			g.beginDraw(pen);
    			g.moveTo(x1+lineThickness, y1+lineThickness);
    			g.lineTo(x1+lineThickness, y2-lineThickness);
    			g.moveTo(x1, y2+lineThickness);
    			g.lineTo(x2+lineThickness, y2+lineThickness);
    			g.lineTo(x2+lineThickness, y1);
    			g.moveTo(x2-lineThickness, y1+lineThickness);
    			g.lineTo(x1+lineThickness, y1+lineThickness);
    			g.endDraw();
			}
    	}else{
			var r:Number = round;
			if(beveled){
				pen.setColor(lineLightColor);
    			g.beginDraw(pen);
    			var t:Number = lineThickness;
				x1+=t;
				x2+=t;
				y1+=t;
				y2+=t;
	    		g.moveTo(textR.x, y1);
				//Top left
				g.lineTo (x1+r, y1);
				g.curveTo(x1, y1, x1, y1+r);
				//Bottom left
				g.lineTo (x1, y2-r );
				g.curveTo(x1, y2, x1+r, y2);
				//bottom right
				g.lineTo(x2-r, y2);
				g.curveTo(x2, y2, x2, y2-r);
				//Top right
				g.lineTo (x2, y1+r);
				g.curveTo(x2, y1, x2-r, y1);
				g.lineTo(textR.x + textR.width, y1);
    			g.endDraw();  
				x1-=t;
				x2-=t;
				y1-=t;
				y2-=t;  				
			}		
			pen.setColor(lineColor);		
			g.beginDraw(pen);
    		g.moveTo(textR.x, y1);
			//Top left
			g.lineTo (x1+r, y1);
			g.curveTo(x1, y1, x1, y1+r);
			//Bottom left
			g.lineTo (x1, y2-r );
			g.curveTo(x1, y2, x1+r, y2);
			//bottom right
			g.lineTo(x2-r, y2);
			g.curveTo(x2, y2, x2, y2-r);
			//Top right
			g.lineTo (x2, y1+r);
			g.curveTo(x2, y1, x2-r, y1);
			g.lineTo(textR.x + textR.width, y1);
			g.endDraw();
		}	
    }
    	   
   override public function getBorderInsetsImp(c:Component, bounds:IntRectangle):Insets{
    	var cornerW:Number = Math.ceil(lineThickness*2 + round - round*0.707106781186547);
    	var insets:Insets = new Insets(cornerW, cornerW, cornerW, cornerW);
    	return insets;
    }
	
	override public function getDisplayImp():DisplayObject{
		return null;
	}		
	
	//-----------------------------------------------------------------
	public function getLineColor():ASColor {
		return lineColor;
	}

	public function setLineColor(lineColor:ASColor):void {
		if (lineColor != null){
			this.lineColor = lineColor;
		}
	}
	
	public function getLineLightColor():ASColor{
		return lineLightColor;
	}
	
	public function setLineLightColor(lineLightColor:ASColor):void{
		if (lineLightColor != null){
			this.lineLightColor = lineLightColor;
		}
	}
	
	public function isBeveled():Boolean{
		return beveled;
	}
	
	public function setBeveled(b:Boolean):void{
		beveled = b;
	}

	public function getRound():Number {
		return round;
	}

	public function setRound(round:Number):void {
		this.round = round;
	}
	
	public function getLineThickness():int {
		return lineThickness;
	}

	public function setLineThickness(lineThickness:Number):void {
		this.lineThickness = lineThickness;
	}
}
	
}