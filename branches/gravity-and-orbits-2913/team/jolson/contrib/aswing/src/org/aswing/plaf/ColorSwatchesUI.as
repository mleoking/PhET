/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for JColorSwatchs.
 * 
 * @author iiley
 */
class org.aswing.plaf.ColorSwatchesUI extends ComponentUI {
	
	/**
	 * Adds a component to this panel's sections bar.
	 * Subclass must override this method
	 * @param com the component to be added
	 */
	public function addComponentColorSectionBar(com:Component):Void{
		trace("Subclass must override this method");
		throw new Error("Subclass must override this method");
	}
	
}