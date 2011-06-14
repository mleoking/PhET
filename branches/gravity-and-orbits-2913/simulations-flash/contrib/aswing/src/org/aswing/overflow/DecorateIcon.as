/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;

/**
 * DecorateIcon let you can make Decorator Patten Icon.
 * <p>
 * <b>Note:You should only need to override:</b>
 * <ul>
 * <li><code>getIconWidthImp</code>
 * <li><code>getIconHeightImp</code>
 * <li><code>paintIconImp</code>
 * <li><code>uninstallIconImp</code>
 * </ul>
 * methods in sub-class generally.
 * 
 * @author iiley
 */
class org.aswing.overflow.DecorateIcon implements Icon{
	
	private var bottomIcon:Icon;
	
	/**
	 * DecorateIcon(icon:Icon)<br>
	 * DecorateIcon()<br>
	 */
	public function DecorateIcon(bottomIcon:Icon){
		this.bottomIcon = bottomIcon;
	}
	
	/**
	 * You should override this method to count icon width in sub-class.
	 */
	public function getIconWidthImp():Number{
		return 0;
	}
	
	/**
	 * You should override this method to count icon height in sub-class.
	 */
	public function getIconHeightImp():Number{
		return 0;
	}
	
	/**
	 * Override this method in sub-class to draw icon on the iconMC at the 
	 * specified location.
	 * @param com the component where the icon is on.
	 * @param g the paint graphics
	 * @param x the icon's x coordinate
	 * @param y the icon's x coordinate
	 */
	public function paintIconImp(com:Component, g:Graphics, x:Number, y:Number):Void{
	}
	
	/**
	 * Override this method in sub-class to clear and remove the icon things you 
	 * created for this component.
	 * @see #uninstallIcon()
	 */
	public function uninstallIconImp(com:Component):Void{
	}

	/**
	 * Return the icon's width.
	 * You should not override this method, should override <code>getIconWidthImp</code>
	 * @see getIconWidthImp()
	 */
	public function getIconWidth():Number{
		var bottomWidth:Number = (bottomIcon == null ? 0 : bottomIcon.getIconWidth());
		return Math.max(bottomWidth, getIconWidthImp());
	}
	
	/**
	 * Return the icon's height.
	 * You should not override this method, should override <code>getIconHeightImp</code>
	 * @see getIconHeightImp()
	 */
	public function getIconHeight():Number{
		var bottomHeight:Number = (bottomIcon == null ? 0 : bottomIcon.getIconHeight());
		return Math.max(bottomHeight, getIconHeightImp());
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		bottomIcon.paintIcon(com, g, x, y);
		paintIconImp(com, g, x, y);
	}
	
	/**
	 * Clear the things the icon painted.
	 */
	public function uninstallIcon(com:Component):Void{
		bottomIcon.uninstallIcon(com);
		uninstallIconImp(com);
	}
}
