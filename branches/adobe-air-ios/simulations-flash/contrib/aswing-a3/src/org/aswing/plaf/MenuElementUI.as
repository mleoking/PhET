/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf{

/**
 * Pluginable ui for MenuItem.
 * @see org.aswing.JMenuItem
 * @author iiley
 */
public interface MenuElementUI extends ComponentUI{
	
	/**
	 * Subclass override this to process key event.
	 */
	function processKeyEvent(code:uint):void;
}
}