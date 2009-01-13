/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for JMenuBar.
 * 
 * @author iiley
 */
class org.aswing.plaf.MenuBarUI extends ComponentUI {
	
	public function MenuBarUI() {
		super();
	}

	/**
	 * Subclass override this to process key event.
	 */
	public function processKeyEvent(code : Number) : Void {
	}
}