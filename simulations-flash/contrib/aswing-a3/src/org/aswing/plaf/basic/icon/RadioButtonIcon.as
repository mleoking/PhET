/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon
{

import flash.display.DisplayObject;

import org.aswing.*;
import org.aswing.graphics.*;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.UIResource;
import flash.geom.Matrix;

/**
 * @private
 */
public class RadioButtonIcon implements Icon, UIResource
{
	private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;
	
	public function RadioButtonIcon(){
	}
	
	private function reloadColors(ui:ComponentUI):void{
		shadow = ui.getColor("RadioButton.shadow");
		darkShadow = ui.getColor("RadioButton.darkShadow");
		highlight = ui.getColor("RadioButton.light");
		lightHighlight = ui.getColor("RadioButton.highlight");
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		if(shadow == null){
			reloadColors(c.getUI());
		}
		var rb:AbstractButton = AbstractButton(c);
		var model:ButtonModel = rb.getModel();
		var drawDot:Boolean = model.isSelected();
		
		var periphery:ASColor = darkShadow;
		var middle:ASColor = highlight;
		var inner:ASColor = shadow;
		var dot:ASColor = rb.getForeground();
		
		// Set up colors per RadioButtonModel condition
		if (!model.isEnabled()) {
			periphery = middle = inner = rb.getBackground();
			dot = darkShadow;
		} else if (model.isPressed()) {
			periphery = shadow;
			inner = darkShadow;
		}
		
		var w:Number = getIconWidth(c);
		var h:Number = getIconHeight(c);
		var cx:Number = x + w/2;
		var cy:Number = y + h/2;
		var xr:Number = w/2;
		var yr:Number = h/2;
		
		var brush:SolidBrush = new SolidBrush(darkShadow);
		g.fillEllipse(brush, x, y, w, h);
		brush.setColor(highlight);
		g.fillEllipse(brush, x+1, y+1, w-2, h-2);
        
        var colors:Array = [rb.getBackground().getRGB(), 0xffffff];
		var alphas:Array = [1, 1];
		var ratios:Array = [0, 255];
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w-3, h-3, (45/180)*Math.PI, x+2, y+2);    
	    var gbrush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
	    g.fillEllipse(gbrush, x+2, y+2, w-4, h-4);
		
		if(drawDot){
			xr = w/5;
			yr = h/5;
			brush = new SolidBrush(dot);
			g.fillEllipse(brush, cx-xr, cy-yr, xr*2, yr*2);			
		}
	}
	
	public function getIconHeight(c:Component):int{
		return 13;
	}
	
	public function getIconWidth(c:Component):int{
		return 13;
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return null;
	}
	
}
}