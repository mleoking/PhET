/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.dnd.DraggingImage;
import org.aswing.ElementCreater;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.overflow.JList;

/**
 * @author iiley
 */
class org.aswing.dnd.ListDragImage implements DraggingImage {
	
	private var list:JList;
	private var textField:TextField;
	private var width:Number;
	private var height:Number;
	private var target:MovieClip;
	private var number:Number;
	private var x:Number;
	private var y:Number;
	
	public function ListDragImage(list:JList, offsetY:Number){
		height = list.getCellFactory().getCellHeight();
		if(height == null){
			height = 18;
		}
		width = list.getInsets().getInsideSize(list.getSize()).width - 10;
		number = list.getSelectedIndices().length;
		x = 0;
		y = offsetY;
	}
	
	public function setCanvas(target : MovieClip) : Void {
		this.target = target;
		textField.removeTextField();
		textField = ElementCreater.getInstance().createTF(target, "n_of_draging_text");
	}

	public function switchToAcceptImage() : Void {
		target.clear();
		drawItems(new Graphics(target), true);
	}

	public function switchToRejectImage() : Void {
		target.clear();
		drawItems(new Graphics(target), false);
	}
	
	private function drawItems(g:Graphics, allow:Boolean):Void{
		var w:Number = width;
		var h:Number = height;
		var r:Number = Math.min(w, h) - 2;
		
		if(!allow){
			g.drawLine(new Pen(ASColor.RED, 2), x+1, y+1, x+1+r, y+1+r);
			g.drawLine(new Pen(ASColor.RED, 2), x+1+r, y+1, x+1, y+1+r);
		}
		textField._visible = false;
		if(number > 1){
			if(number > 2){
				g.drawRectangle(new Pen(ASColor.GRAY), x+4, y+4, w-3, h-2);
				textField._visible = true;
				textField._x = x+width+2;
				textField._y = y;
				textField.text = (number+"");
			}
			g.drawRectangle(new Pen(ASColor.GRAY), x+2, y+2, w-1, h-1);
		}
		g.drawRectangle(new Pen(ASColor.GRAY), x, y, w, h);
	}
}