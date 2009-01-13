/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Rectangle;

/**
 * The strategy for Resizer to count the new bounds of component would be resized to.
 * @author iiley
 */
interface org.aswing.resizer.ResizeStrategy{
	/**
	 * Count and return the new bounds what the component would be resized to.
	 */
	public function getBounds(com:Component, movedX:Number, movedY:Number):Rectangle;
}
