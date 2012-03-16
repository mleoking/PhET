/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.dnd{

import flash.display.*;
import org.aswing.*;
import org.aswing.graphics.*;

/**
 * The default drag image.
 * @author iiley
 */
public class DefaultDragImage implements DraggingImage{
	
	private var image:Shape;
	private var width:int;
	private var height:int;
	
	public function DefaultDragImage(dragInitiator:Component){
		width = dragInitiator.width;
		height = dragInitiator.height;
		
		image = new Shape();
	}
	
	public function getDisplay():DisplayObject
	{
		return image;
	}
	
	public function switchToRejectImage():void
	{
		image.graphics.clear();
		var r:Number = Math.min(width, height) - 2;
		var x:Number = 0;
		var y:Number = 0;
		var w:Number = width;
		var h:Number = height;
		var g:Graphics2D = new Graphics2D(image.graphics);
		g.drawLine(new Pen(ASColor.RED, 2), x+1, y+1, x+1+r, y+1+r);
		g.drawLine(new Pen(ASColor.RED, 2), x+1+r, y+1, x+1, y+1+r);
		g.drawRectangle(new Pen(ASColor.GRAY), x, y, w, h);
	}
	
	public function switchToAcceptImage():void
	{
		image.graphics.clear();
		var g:Graphics2D = new Graphics2D(image.graphics);
		g.drawRectangle(new Pen(ASColor.GRAY), 0, 0, width, height);
	}
	
}
}