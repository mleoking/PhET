/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.resizer.ResizeStrategy;

/**
 * A basic implementation of ResizeStrategy.
 * 
 * <p>It will return the resized rectangle, the rectangle is not min than 
 * getResizableMinSize and not max than getResizableMaxSize if these method are defineded
 * in the resized comopoent.
 * 
 * @author iiley
 */
class org.aswing.resizer.ResizeStrategyImp implements ResizeStrategy{
	
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
	public function getBounds(com:Component, movedX:Number, movedY:Number):Rectangle{
		var currentBounds:Rectangle = com.getBounds();
		var minSize:Dimension = com.getMinimumSize();
		var maxSize:Dimension = com.getMaximumSize();
		if(minSize == undefined){
			minSize = new Dimension(0, 0);
		}
		if(maxSize == undefined){
			maxSize = new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
		}		
		var newX:Number;
		var newY:Number;
		var newW:Number;
		var newH:Number;
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
		return new Rectangle(newX, newY, newW, newH);
	}
}
