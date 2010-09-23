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
public class V_ResizeCursor extends Shape{
	
	private var resizeArrowColor:ASColor;
	private var resizeArrowLightColor:ASColor;
	private var resizeArrowDarkColor:ASColor;
	
	public function V_ResizeCursor(){
		super();
		
	    resizeArrowColor = UIManager.getColor("Frame.resizeArrow");
	    resizeArrowLightColor = UIManager.getColor("Frame.resizeArrowLight");
	    resizeArrowDarkColor = UIManager.getColor("Frame.resizeArrowDark");

		var w:Number = 1; //arrowAxisHalfWidth
		var r:Number = 4;
		var arrowPoints:Array;
		
		arrowPoints = [{y:-r*2, x:0}, {y:-r, x:-r}, {y:-r, x:-w},
						 {y:r, x:-w}, {y:r, x:-r}, {y:r*2, x:0},
						 {y:r, x:r}, {y:r, x:w}, {y:-r, x:w},
						 {y:-r, x:r}];
		var gdi:Graphics2D = new Graphics2D(graphics);
		gdi.drawPolygon(new Pen(resizeArrowColor.changeAlpha(0.4), 4), arrowPoints);
		gdi.fillPolygon(new SolidBrush(resizeArrowLightColor), arrowPoints);
		gdi.drawPolygon(new Pen(resizeArrowDarkColor, 1), arrowPoints);			
	}
}
}