/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.adjuster{

import org.aswing.plaf.basic.BasicSliderUI;
import org.aswing.*;
import org.aswing.geom.IntRectangle;

/**
 * SliderUI for JAdjuster popup slider.
 * @author iiley
 * @private
 */
public class PopupSliderUI extends BasicSliderUI{
	
	public function PopupSliderUI()
	{
		super();
	}
	
	override protected function getPropertyPrefix():String {
		return "Adjuster.";
	}
	
	override protected function installDefaults():void{	
		super.installDefaults();
		slider.setOpaque(true);
	}
	
	override protected function getPrefferedLength():int{
		return 100;
	}
}
}