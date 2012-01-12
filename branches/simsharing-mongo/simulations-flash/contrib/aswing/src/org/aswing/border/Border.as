/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;

/**
 * Interface describing an object capable of rendering a border around the edges of a component.
 * 
 * <p>Note that one Border most just can paint to one Component.
 * Because the paintBorder method most should clear the last paintings.
 * 
 * @author iiley
 */
interface org.aswing.border.Border{
	
	/**
	 * Paints the border on component in a bounds with the graphics.
	 * If this if the first time to paint on the specified component, there 
	 * may need some install operation, for example create MCs on the
	 * specified mc if needed.
	 * @param c the component for which this border is being painted 
	 * @param g the paint graphics
	 * @param bounds the bounds of border
	 * @see #uninstallBorder
	 */
    public function paintBorder(c:Component, g:Graphics, bounds:Rectangle):Void;

	/**
	 * Removes things in the border object related to the component.
	 * For example remove the MCs created at the first paint time if needed.
	 * <p>
	 * There is not installIcon method, so you must install icon related things
	 * at the first time of <code>paintIcon</code>, for example for example attach a MC if needed.
	 * @param c the component where the border whould uninstall from
	 * @see #paintBorder
	 */
	public function uninstallBorder(c:Component):Void;
	
    /**
     * Returns the insets of the border.  
     * @param c the component for which this border insets value applies
     * @param bounds the bounds of the border would paint in.
     * @return the insets of the border
     */
    public function getBorderInsets(c:Component, bounds:Rectangle):Insets;	
}
