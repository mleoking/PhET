/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.dnd.DraggingImage;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;

/**
 * The default drag image.
 * @author iiley
 */
class org.aswing.dnd.DefaultDragImage implements DraggingImage {
	
	private var initiator:Component;
	private var target:MovieClip;
	private var x:Number;
	private var y:Number;
	
	public function DefaultDragImage(dragInitiator:Component){
		initiator = dragInitiator;
		x = 0;
		y = 0;
	}
	
	private function getImageWidth() : Number {
		return initiator.getWidth();
	}
	
	private function getImageHeight() : Number {
		return initiator.getHeight();
	}
	
	public function setCanvas(target : MovieClip) : Void {
		this.target = target;
	}

	public function switchToAcceptImage() : Void {
		target.clear();
		var w:Number = getImageWidth();
		var h:Number = getImageHeight();
		var g:Graphics = new Graphics(target);
		g.drawRectangle(new Pen(ASColor.GRAY), x, y, w, h);
	}

	public function switchToRejectImage() : Void {
		target.clear();
		var w:Number = getImageWidth();
		var h:Number = getImageHeight();
		var r:Number = Math.min(w, h) - 2;
		var g:Graphics = new Graphics(target);
		g.drawLine(new Pen(ASColor.RED, 2), x+1, y+1, x+1+r, y+1+r);
		g.drawLine(new Pen(ASColor.RED, 2), x+1+r, y+1, x+1, y+1+r);
		g.drawRectangle(new Pen(ASColor.GRAY), x, y, w, h);
	}
}
