/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Image;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.Cursor implements Image{
	/**
	 * Horizontal resize cursor
	 */
	public static var H_RESIZE_CURSOR:Number = 0;

	/**
	 * Vertical resize cursor<br>
	 * Credit to Kristof Neirynck for added V_RESIZE_CURSOR implementation.
	 */
	public static var V_RESIZE_CURSOR:Number = 1;
	
	private var type:Number;
	
	private var resizeArrowColor:ASColor;
	private var resizeArrowLightColor:ASColor;
	private var resizeArrowDarkColor:ASColor;
	
	public function Cursor(type:Number){
		this.type = type;

	    resizeArrowColor = UIManager.getColor("Frame.resizeArrow");
	    resizeArrowLightColor = UIManager.getColor("Frame.resizeArrowLight");
	    resizeArrowDarkColor = UIManager.getColor("Frame.resizeArrowDark");	
	}
	
	public function getImageWidth() : Number {
		return 0;
	}

	public function getImageHeight() : Number {
		return 0;
	}

	public function paintImage(target : MovieClip, x : Number, y : Number) : Void {
		var w:Number = 1; //arrowAxisHalfWidth
		var r:Number = 4;
		var arrowPoints:Array;
		
		switch (type) {
			case H_RESIZE_CURSOR:
				arrowPoints = [{x:-r*2, y:0}, {x:-r, y:-r}, {x:-r, y:-w},
								 {x:r, y:-w}, {x:r, y:-r}, {x:r*2, y:0},
								 {x:r, y:r}, {x:r, y:w}, {x:-r, y:w},
								 {x:-r, y:r}];
				break;
			case V_RESIZE_CURSOR:
				arrowPoints = [{y:-r*2, x:0}, {y:-r, x:-r}, {y:-r, x:-w},
								 {y:r, x:-w}, {y:r, x:-r}, {y:r*2, x:0},
								 {y:r, x:r}, {y:r, x:w}, {y:-r, x:w},
								 {y:-r, x:r}];
				break;			
		}
		
		for(var i:Number=arrowPoints.length-1; i>=0; i--){
			arrowPoints[i].x += x;
			arrowPoints[i].y += y;
		}
		
		var gdi:Graphics = new Graphics(target);
		gdi.drawPolygon(new Pen(resizeArrowColor.getRGB(), 4, 40), arrowPoints);
		gdi.fillPolygon(new SolidBrush(resizeArrowLightColor), arrowPoints);
		gdi.drawPolygon(new Pen(resizeArrowDarkColor, 1), arrowPoints);		
	}

}