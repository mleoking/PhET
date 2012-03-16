/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.background
{
	
import flash.display.*;

import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;

/**
 * The barIcon decorator for ProgressBar.
 * @author senkay
 * @private
 */
public class ProgressBarIcon implements GroundDecorator, UIResource
{
	private var indeterminatePercent:Number;
	private var color:ASColor;
	
	public function ProgressBarIcon(){
		indeterminatePercent = 0;
	}
	
	private function reloadColors(ui:ComponentUI):void{
		color = ui.getColor("ProgressBar.progressColor");
	}
	
	public function updateDecorator(com:Component, g:Graphics2D, b:IntRectangle):void{
		if(color == null){
			reloadColors(com.getUI());
		}
		var pb:JProgressBar = JProgressBar(com);
		if(pb == null){
			return;
		}
		
		var box:IntRectangle = b.clone();
		var percent:Number;
		if(pb.isIndeterminate()){
			percent = indeterminatePercent;
			indeterminatePercent += 0.1;
			if(indeterminatePercent > 1){
				indeterminatePercent = 0;
			}
		}else{
			percent = pb.getPercentComplete();
		}
		
		var boxWidth:Number = 5;
		var gap:Number = 1;
		g.beginFill(new SolidBrush(color));
		
		if(pb.getOrientation() == JProgressBar.VERTICAL){
			box.height = boxWidth;
			var minY:Number = b.y + b.height - b.height * percent;
			for(box.y = b.y+b.height-boxWidth; box.y >= minY; box.y -= (boxWidth+gap)){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
			if(box.y < minY && box.y + boxWidth > minY){
				box.height = boxWidth - (minY - box.y);
				box.y = minY;
				g.rectangle(box.x, box.y, box.width, box.height);
			}
		}else{
			box.width = boxWidth;
			var maxX:Number = b.x + b.width * percent;
			for(;box.x <= maxX - boxWidth; box.x += (boxWidth+gap)){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
			box.width = maxX - box.x;
			if(box.width > 0){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
		}
		g.endFill();
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}	
}
}