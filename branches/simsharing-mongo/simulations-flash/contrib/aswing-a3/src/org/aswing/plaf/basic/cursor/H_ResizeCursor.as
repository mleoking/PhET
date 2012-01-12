/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.cursor{

import flash.display.Shape;
import org.aswing.graphics.*;
import org.aswing.UIManager;
import org.aswing.ASColor;

/**
 * @private
 */
public class H_ResizeCursor extends Shape{
	
	private var resizeArrowColor:ASColor;
	private var resizeArrowLightColor:ASColor;
	private var resizeArrowDarkColor:ASColor;
	
	public function H_ResizeCursor(){
		super();
		
	    resizeArrowColor = UIManager.getColor("Frame.resizeArrow");
	    resizeArrowLightColor = UIManager.getColor("Frame.resizeArrowLight");
	    resizeArrowDarkColor = UIManager.getColor("Frame.resizeArrowDark");

		var w:Number = 1; //arrowAxisHalfWidth
		var r:Number = 4;
		var arrowPoints:Array;
		
		arrowPoints = [{x:-r*2, y:0}, {x:-r, y:-r}, {x:-r, y:-w},
						 {x:r, y:-w}, {x:r, y:-r}, {x:r*2, y:0},
						 {x:r, y:r}, {x:r, y:w}, {x:-r, y:w},
						 {x:-r, y:r}];
		var gdi:Graphics2D = new Graphics2D(graphics);
		gdi.drawPolygon(new Pen(resizeArrowColor.changeAlpha(0.4), 4), arrowPoints);
		gdi.fillPolygon(new SolidBrush(resizeArrowLightColor), arrowPoints);
		gdi.drawPolygon(new Pen(resizeArrowDarkColor, 1), arrowPoints);			
	}
	
}
}