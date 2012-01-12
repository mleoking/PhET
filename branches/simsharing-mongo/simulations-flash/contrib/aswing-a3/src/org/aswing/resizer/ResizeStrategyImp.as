/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.resizer{
	
import org.aswing.geom.*;
import org.aswing.Component;

/**
 * A basic implementation of ResizeStrategy.
 * 
 * <p>It will return the resized rectangle, the rectangle is not min than 
 * getResizableMinSize and not max than getResizableMaxSize if these method are defineded
 * in the resized comopoent.
 * </p>
 * @author iiley
 */
public class ResizeStrategyImp implements ResizeStrategy{
	
	private var wSign:Number;
	private var hSign:Number;
	
	public function ResizeStrategyImp(wSign:Number, hSign:Number){
		this.wSign = wSign;
		this.hSign = hSign;
	}
	
	/**
	 * Count and return the new bounds what the component would be resized to.<br>
	 * 
 	 * It will return the resized rectangle, the rectangle is not min than 
 	 * getResizableMinSize and not max than getResizableMaxSize if these method are defineded
 	 * in the resized comopoent.
	 */
	public function getBounds(origBounds:IntRectangle, minSize:IntDimension, maxSize:IntDimension, movedX:int, movedY:int):IntRectangle{
		var currentBounds:IntRectangle = origBounds.clone();
		if(minSize == null){
			minSize = new IntDimension(0, 0);
		}
		if(maxSize == null){
			maxSize = IntDimension.createBigDimension();
		}		
		var newX:int;
		var newY:int;
		var newW:int;
		var newH:int;
		if(wSign == 0){
			newW = currentBounds.width;
		}else{
			newW = currentBounds.width + wSign*movedX;
			newW = Math.min(maxSize.width, Math.max(minSize.width, newW));
		}
		if(wSign < 0){
			newX = currentBounds.x + (currentBounds.width - newW);
		}else{
			newX = currentBounds.x;
		}
		
		if(hSign == 0){
			newH = currentBounds.height;
		}else{
			newH = currentBounds.height + hSign*movedY;
			newH = Math.min(maxSize.height, Math.max(minSize.height, newH));
		}
		if(hSign < 0){
			newY = currentBounds.y + (currentBounds.height - newH);
		}else{
			newY = currentBounds.y;
		}
		newX = Math.round(newX);
		newY = Math.round(newY);
		newW = Math.round(newW);
		newH = Math.round(newH);
		return new IntRectangle(newX, newY, newW, newH);
	}
		
}
}