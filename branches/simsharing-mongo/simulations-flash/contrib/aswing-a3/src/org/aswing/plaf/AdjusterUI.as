/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf{

import org.aswing.*;
import org.aswing.plaf.ComponentUI;

/**
 * Pluginable ui for JAdjuster.
 * @see org.aswing.JAdjuster
 * @author iiley
 */
public interface AdjusterUI extends ComponentUI{
	
	function getInputText():JTextField;
	
	function getPopupSlider():JSlider;
}
}