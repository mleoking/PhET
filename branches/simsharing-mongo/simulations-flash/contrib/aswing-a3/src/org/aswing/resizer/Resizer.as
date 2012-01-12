/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.resizer{

import org.aswing.Component;

/**
 * Component Resizer interface.
 * @author iiley
 */
public interface Resizer{
	
	/**
	 * Sets the owner of this resizer. 
	 * If the owner is changed, the last owner will be off-dressed the resizer, the new 
	 * owner will be on-dressed.
	 * Use null to off-dress the current owner.
	 * @param c the new owner or null.
	 */
	function setOwner(c:Component):void;
				
	/**
	 * <p>Indicate whether need resize component directly when drag the resizer arrow.
	 * <p>if set to false, there will be a rectange to represent then size what will be resized to.
	 * <p>if set to true, the component will be resize directly when drag, but this is need more cpu counting.
	 * <p>Default is false.
	 * @see org.aswing.JFrame
	 */	
	function setResizeDirectly(r:Boolean):void;
	
	/**
	 * Returns whether need resize component directly when drag the resizer arrow.
	 * @see #setResizeDirectly
	 */
	function isResizeDirectly():Boolean;
	
	/**
	 * Returns whether this resizer is enabled.
	 */
	function isEnabled():Boolean;
	
	/**
	 * Sets the resizer to enabled or not.
	 */
	function setEnabled(b:Boolean):void;
}
}