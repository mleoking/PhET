/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Container;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;

/**
 * The container with a mask mc to mask all of its children, make the border 
 * visible right when the container can't manage to handle the children's bounds.
 * <p>
 * Some complex component like JList, JTable have no time/temper to handler the out 
 * bounds(here is meaning out into border's area, normal out component bounds are 
 * handled by Component defaultly) children(the cell components), so there is a mask 
 * to make the children will not visible the out bounds area.
 * @author iiley
 */
class org.aswing.ChildrenMaskedContainer extends Container {
	
	private var children_mc:MovieClip;
	private var children_mask_mc:MovieClip;
	
	private function ChildrenMaskedContainer() {
		super();
	}
	
	///////
	/**
	 * create others after target_mc created.
	 */
	private function create():Void{
		super.create();
		children_mc = creater.createMC(target_mc, "children_mc");
		children_mask_mc = creater.createMC(target_mc, "children_mask_mc");
	}
	
	///////////
	/**
	 * initialize after create
	 */
	private function initialize():Void{
		super.initialize();
		//paint the clip rect content
		var g:Graphics = new Graphics(children_mask_mc);
		g.fillRectangle(new SolidBrush(0, 100), 0, 0, 1, 1);
		children_mc.setMask(children_mask_mc);
		setClipMasked(clipMasked);
	}
	
	private function paint(b:Rectangle):Void{
		children_mask_mc._x = b.x;
		children_mask_mc._y = b.y;
		children_mask_mc._width = b.width;
		children_mask_mc._height = b.height;
		super.paint(b);
	}
	
	/**
	 * Clears the graphics that in children masked bounds.
	 */
	public function clearChildrenGraphics():Void{
		children_mc.clear();
	}
	
	/**
	 * Returns the graphics that in children masked bounds.
	 */
	public function getChildrenGraphics():Graphics{
		return new Graphics(children_mc);
	}
	
	public function createChildMC(nameStart:String):MovieClip{
		return creater.createMC(children_mc, nameStart);
	}
	
	public function setClipMasked(m:Boolean):Void{
		clipMasked = m;
		clip_mc._visible = clipMasked;
		children_mask_mc._visible = clipMasked;
		if(clipMasked){
			target_mc.setMask(clip_mc);
			children_mc.setMask(children_mask_mc);
		}else{
			target_mc.setMask(null);
			children_mc.setMask(null);
		}
	}	
	
}