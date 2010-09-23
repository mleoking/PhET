/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.colorchooser { 

import org.aswing.*;
import org.aswing.graphics.*;
import flash.display.DisplayObject;
/**
 * PreviewColorIcon represent two color rect, on previous, on current.
 * @author iiley
 */
public class PreviewColorIcon implements Icon{
	/** 
     * Horizontal orientation.
     */
    public static const HORIZONTAL:int = AsWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static const VERTICAL:int   = AsWingConstants.VERTICAL;
    
	private var previousColor:ASColor;
	private var currentColor:ASColor;
	private var width:int;
	private var height:int;
	private var orientation:int;
	
	public function PreviewColorIcon(width:int, height:int, orientation:int=AsWingConstants.VERTICAL){
		this.width = width;
		this.height = height;
		this.orientation = orientation;
		previousColor = currentColor = ASColor.WHITE;
	}
	
	public function setColor(c:ASColor):void{
		setCurrentColor(c);
		setPreviousColor(c);
	}
	
	public function setOrientation(o:int):void{
		orientation = o;
	}
	
	public function getOrientation():int{
		return orientation;
	}
	
	public function setPreviousColor(c:ASColor):void{
		previousColor = c;
	}
	
	public function getPreviousColor():ASColor{
		return previousColor;
	}
	
	public function setCurrentColor(c:ASColor):void{
		currentColor = c;
	}
	
	public function getCurrentColor():ASColor{
		return currentColor;
	}
	
	public function getIconWidth(c:Component) : int {
		return width;
	}

	public function getIconHeight(c:Component) : int {
		return height;
	}

	public function updateIcon(com:Component, g:Graphics2D, x:int, y:int):void{
		var w:Number = width;
		var h:Number = height;
		g.fillRectangle(new SolidBrush(ASColor.WHITE), x, y, w, h);

		var t:Number = 5;
		for(var c:Number=0; c<w; c+=t){
			g.drawLine(new Pen(ASColor.GRAY, 1), x+c, y, x+c, y+h);
		}
		for(var r:Number=0; r<h; r+=t){
			g.drawLine(new Pen(ASColor.GRAY, 1), x, y+r, x+w, y+r);
		}
			
		if(previousColor == null && currentColor == null){
			paintNoColor(g, x, y, w, h);
			return;
		}
		
		if(orientation == HORIZONTAL){
			w = width/2;
			h = height;
			if(previousColor == null){
				paintNoColor(g, x, y, w, h);
			}else{
				g.fillRectangle(new SolidBrush(previousColor), x, y, w, h);
			}
			x += w;
			if(currentColor == null){
				paintNoColor(g, x, y, w, h);
			}else{
				g.fillRectangle(new SolidBrush(currentColor), x, y, w, h);
			}
		}else{
			w = width;
			h = height/2;
			if(previousColor == null){
				paintNoColor(g, x, y, w, h);
			}else{
				g.fillRectangle(new SolidBrush(previousColor), x, y, w, h);
			}
			y += h;
			if(currentColor == null){
				paintNoColor(g, x, y, w, h);
			}else{
				g.fillRectangle(new SolidBrush(currentColor), x, y, w, h);
			}
		}
	}
	
	private function paintNoColor(g:Graphics2D, x:Number, y:Number, w:Number, h:Number):void{
		g.fillRectangle(new SolidBrush(ASColor.WHITE), x, y, w, h);
		g.drawLine(new Pen(ASColor.RED, 2), x+1, y+h-1, x+w-1, y+1);
	}

	public function getDisplay(c:Component):DisplayObject{
		return null;
	}

}
}