package org.aswing.plaf.basic.background
{
	
import flash.display.DisplayObject;

import org.aswing.*;
import org.aswing.geom.IntRectangle;
import org.aswing.graphics.*;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.*;

/**
 * @private
 */
public class ToggleButtonBackground implements GroundDecorator, UIResource
{
    private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;
    
	public function ToggleButtonBackground(){
	} 
	

	private function reloadColors(ui:ComponentUI):void{
		shadow = ui.getColor("ToggleButton.shadow");
		darkShadow = ui.getColor("ToggleButton.darkShadow");
		highlight = ui.getColor("ToggleButton.light");
		lightHighlight = ui.getColor("ToggleButton.highlight");
	}
	
	public function updateDecorator(c:Component, g:Graphics2D, bounds:IntRectangle):void{
		if(shadow == null){
			reloadColors(c.getUI());
		}
		var b:AbstractButton = c as AbstractButton;
		bounds = bounds.clone();
		if(b == null) return;
		if(c.isOpaque()){
			var model:ButtonModel = b.getModel();
	    	var isPressing:Boolean = model.isArmed() || model.isSelected();
			BasicGraphicsUtils.drawBezel(g, bounds, isPressing, shadow, darkShadow, highlight, lightHighlight);
			bounds.grow(-2, -2);
			
			var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
			if(model.isArmed() || model.isSelected()){
				g.fillRectangle(new SolidBrush(bgColor.darker(0.9)), bounds.x, bounds.y, bounds.width, bounds.height);
			}else{
				BasicGraphicsUtils.paintButtonBackGround(b, g, bounds);
			}
		}
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}
		
}
}