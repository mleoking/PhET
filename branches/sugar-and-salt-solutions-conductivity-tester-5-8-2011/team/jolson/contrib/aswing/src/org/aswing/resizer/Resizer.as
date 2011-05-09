/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
 
/**
 * Component Resizer interface.
 * @author iiley
 */
interface org.aswing.resizer.Resizer {
	/**
	 * Sets the owner of this resizer.
	 */
	public function setOwner(c:Component):Void;
		
	/**
	 * Destroys the resizer, remove all listeners to owner and remove all assets.
	 */
	public function destroy():Void;
		
	/**
	 * <p>Indicate whether need resize component directly when drag the resizer arrow.
	 * <p>if set to false, there will be a rectange to represent then size what will be resized to.
	 * <p>if set to true, the component will be resize directly when drag, but this is need more cpu counting.
	 * <p>Default is false.
	 * @see org.aswing.JFrame
	 */	
	public function setResizeDirectly(r:Boolean):Void;
	
	/**
	 * Returns whether need resize component directly when drag the resizer arrow.
	 * @see #setResizeDirectly
	 */
	public function isResizeDirectly():Boolean;
	
	/**
	 * Returns whether this resizer is enabled.
	 */
	public function isEnabled():Boolean;
	
	/**
	 * Sets the resizer to enabled or not.
	 */
	public function setEnabled(b:Boolean):Void;
}