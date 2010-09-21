/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.graphics.Graphics;

/**
 * A small fixed size picture, typically used to decorate components.
 * 
 * @see BaseIcon
 * @author iiley
 */
interface org.aswing.Icon{
	
	/**
	 * Return the icon's width.
	 */
	public function getIconWidth():Number;
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight():Number;
	
	/**
	 * Draw the icon at the specified component's specified location with the graphics.
	 * If this if the first time to paint on the specified component, there 
	 * may need some install operation, for example create MC on the
	 * specified mc if needed.
	 * @param com component for which this border is being painted 
	 * @param g the paint graphics
	 * @param x the x corrdinate of the icon(top left of bounds)
	 * @param y the y corrdinate of the icon(top left of bounds)
	 * @see #uninstallIcon()
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void;
	
	/**
	 * Remove things in the icon object related to the component.
	 * For example remove the MCs created at the first paint time.
	 * <p>
	 * There is not installIcon method, so you must install icon related things
	 * at the first time of <code>paintIcon</code>, for example attach a MC if needed.
	 * @see #paintIcon()
	 */
	public function uninstallIcon(com:Component):Void;
}
