/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.overflow.DecorateIcon;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;

/**
 * A icon that make it inner icon paint to a offset position.
 * @author iiley
 */
class org.aswing.overflow.OffsetIcon extends DecorateIcon {
	
	private var offsetX:Number;
	private var offsetY:Number;
	
	/**
	 * Create a offset icon.
	 * @param innerIcon the icon to paint offsetly
	 * @param offsetX the x offset
	 * @param offsetY the y offset
	 */
	public function OffsetIcon(innerIcon : Icon, offsetX:Number, offsetY:Number) {
		super(innerIcon);
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}
	
	/**
	 * Sets the offset x.
	 * <p>
	 * If the icon's owner component was already painted, you should call the owner 
	 * revalidate and repaint method to make it taking effet.
	 * @param offsetX the offset x
	 */
	public function setOffsetX(offsetX:Number):Void{
		this.offsetX = (offsetX == undefined ? 0 : offsetX);
	}
	
	/**
	 * Sets the offset y.
	 * <p>
	 * If the icon's owner component was already painted, you should call the owner 
	 * revalidate and repaint method to make it taking effet.
	 * @param offsetY the offset y
	 */	
	public function setOffsetY(offsetY:Number):Void{
		this.offsetY = (offsetY == undefined ? 0 : offsetY);
	}
	
	/**
	 * Returns the offset x
	 * @return the offset x
	 */
	public function getOffsetX():Number{
		return offsetX;
	}
	
	/**
	 * Returns the offset y
	 * @return the offset y
	 */	
	public function getOffsetY():Number{
		return offsetY;
	}
	
	/**
	 * Return the icon width.
	 */
	public function getIconWidth():Number{
		if(bottomIcon != null){
			return bottomIcon.getIconWidth() + offsetX;
		}else{
			return 0;
		}
	}
	
	/**
	 * Return the icon height.
	 */
	public function getIconHeight():Number{
		if(bottomIcon != null){
			return bottomIcon.getIconHeight() + offsetY;
		}else{
			return 0;
		}
	}
	
	/**
	 * Draw the icon at the specified location and the inner icon at the offseted position.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		bottomIcon.paintIcon(com, g, x + offsetX, y + offsetY);
		paintIconImp(com, g, x, y);
	}
}