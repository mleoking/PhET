/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;

/**
 * PreviewColorIcon represent two color rectangle, on previous, on current.
 * @author iiley
 */
class org.aswing.colorchooser.PreviewColorIcon implements Icon{
	/** 
     * Horizontal orientation.
     */
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
    
	private var previousColor:ASColor;
	private var currentColor:ASColor;
	private var width:Number;
	private var height:Number;
	private var orientation:Number;
	
	public function PreviewColorIcon(width:Number, height:Number, orientation:Number){
		this.width = width;
		this.height = height;
		this.orientation = (orientation == undefined ? VERTICAL : orientation);
		previousColor = currentColor = ASColor.WHITE;
	}
	
	public function setColor(c:ASColor):Void{
		setCurrentColor(c);
		setPreviousColor(c);
	}
	
	public function setOrientation(o:Number):Void{
		orientation = o;
	}
	
	public function getOrientation():Number{
		return orientation;
	}
	
	public function setPreviousColor(c:ASColor):Void{
		previousColor = c;
	}
	
	public function getPreviousColor():ASColor{
		return previousColor;
	}
	
	public function setCurrentColor(c:ASColor):Void{
		currentColor = c;
	}
	
	public function getCurrentColor():ASColor{
		return currentColor;
	}
	
	public function getIconWidth() : Number {
		return width;
	}

	public function getIconHeight() : Number {
		return height;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
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
	
	private function paintNoColor(g:Graphics, x:Number, y:Number, w:Number, h:Number):Void{
		g.fillRectangle(new SolidBrush(ASColor.WHITE), x, y, w, h);
		g.drawLine(new Pen(ASColor.RED, 2), x+1, y+h-1, x+w-1, y+1);
	}

	public function uninstallIcon(com : Component) : Void {
	}

}