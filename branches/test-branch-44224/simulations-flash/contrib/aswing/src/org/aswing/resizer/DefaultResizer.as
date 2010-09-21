/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.ElementCreater;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.UIResource;
import org.aswing.resizer.DefaultResizeBarHandler;
import org.aswing.resizer.ResizeStrategy;
import org.aswing.resizer.ResizeStrategyImp;
import org.aswing.util.Delegate;

/**
 * Resizer is a resizer for Components to make it resizable when user mouse on component's edge.
 *
 * @author iiley
 */
class org.aswing.resizer.DefaultResizer implements org.aswing.resizer.Resizer, UIResource{
	private static var RESIZE_MC_WIDTH:Number = 4;
	
	
	private var com:Component;

	//-----------resize equiments--------------
	private var resizeMC:MovieClip;
	
	private var resizeArrowMC:MovieClip;
	private var boundsMC:MovieClip;
	
	private var topResizeMC:MovieClip;
	private var leftResizeMC:MovieClip;
	private var bottomResizeMC:MovieClip;
	private var rightResizeMC:MovieClip;
	
	private var topLeftResizeMC:MovieClip;
	private var topRightResizeMC:MovieClip;
	private var bottomLeftResizeMC:MovieClip;
	private var bottomRightResizeMC:MovieClip;
	
	private var startX:Number;
	private var startY:Number;
	
	private var enabled:Boolean;
	private var resizeDirectly:Boolean;
	
	private var resizeArrowColor:ASColor;
	private var resizeArrowLightColor:ASColor;
	private var resizeArrowDarkColor:ASColor;
	
	private var listener:Object;
	
	/**
	 * Create a Resizer for specified component.
	 */
	public function DefaultResizer(){
		enabled = true;
		resizeDirectly = false;
		startX = 0;
		startY = 0;
		//Default colors
	    resizeArrowColor = new ASColor(0x808080);
	    resizeArrowLightColor = new ASColor(0xCCCCCC);
	    resizeArrowDarkColor = new ASColor(0x000000);
	}
	
	public function setResizeArrowColor(c:ASColor):Void{
		resizeArrowColor = c;
	}
	
	public function setResizeArrowLightColor(c:ASColor):Void{
		resizeArrowLightColor = c;
	}
	
	public function setResizeArrowDarkColor(c:ASColor):Void{
		resizeArrowDarkColor = c;
	}
	
	public function setOwner(c:Component):Void{
		com.removeEventListener(listener);
		com = c;
		listener = new Object();
		listener[Component.ON_PAINT]= Delegate.create(this, locate);
		listener[Component.ON_CREATED]= Delegate.create(this, __ownerCreated);
		com.addEventListener(listener);
		if(com.isDisplayable()){
			__ownerCreated();
		}
	}
	
	private function __ownerCreated():Void{
		createResizeMCs();
	}
	
	/**
	 * <p>Indicate whether need resize component directly when drag the resizer arrow.
	 * <p>if set to false, there will be a rectange to represent then size what will be resized to.
	 * <p>if set to true, the component will be resize directly when drag, but this is need more cpu counting.
	 * <p>Default is false.
	 * @see org.aswing.JFrame
	 */	
	public function setResizeDirectly(r:Boolean):Void{
		resizeDirectly = r;
	}
	
	/**
	 * Returns whether need resize component directly when drag the resizer arrow.
	 * @see #setResizeDirectly
	 */
	public function isResizeDirectly():Boolean{
		return resizeDirectly;
	}
	
	//-----------------------For Handlers-------------------------
	
	public function setArrowRotation(r:Number):Void{
		resizeArrowMC._rotation = r;
	}
	
	public function hideArrow():Void{
		resizeArrowMC._visible = false;
	}
	
	public function showArrowToMousePos():Void{
		resizeArrowMC._visible = true;
		resizeArrowMC._x = resizeArrowMC._parent._xmouse;
		resizeArrowMC._y = resizeArrowMC._parent._ymouse;		
	}
	
	public function startDragArrow():Void{
		resizeArrowMC.startDrag(true);
	}
	
	public function stopDragArrow():Void{
		resizeArrowMC.stopDrag();
	}
	
	public function startResize(strategy:ResizeStrategy):Void{
		if(!resizeDirectly){
			boundsMC._visible = true;
			representRect(com.getBounds());
		}
		startX = resizeMC._xmouse;
		startY = resizeMC._ymouse;
	}
	
	public function resizing(strategy:ResizeStrategy):Void{
		var bounds:Rectangle = strategy.getBounds(com, resizeMC._xmouse - startX, resizeMC._ymouse - startY);
		if(resizeDirectly){
			com.setBounds(bounds);
			com.validate();
			updateAfterEvent();
			startX = resizeMC._xmouse;//restart every time
			startY = resizeMC._ymouse;//restart every time
		}else{
			representRect(bounds);
		}
	}
	
	public function finishResize(strategy:ResizeStrategy):Void{
		if(!resizeDirectly){
			com.setBounds(lastRepresentedBounds);
			boundsMC._visible = false;
			com.revalidate();
		}
	}
	
	private var lastRepresentedBounds:Rectangle;
	
	private function representRect(bounds:Rectangle):Void{
		if(!resizeDirectly){
			var currentPos:Point = com.getLocation();
			var x:Number = bounds.x - currentPos.x;
			var y:Number = bounds.y - currentPos.y;
			var w:Number = bounds.width;
			var h:Number = bounds.height;
			var g:Graphics = new Graphics(boundsMC);
			boundsMC.clear();
			g.drawRectangle(new Pen(resizeArrowLightColor), x-1,y-1,w+2,h+2);
			g.drawRectangle(new Pen(resizeArrowColor), x,y,w,h);
			g.drawRectangle(new Pen(resizeArrowDarkColor), x+1,y+1,w-2,h-2);
			lastRepresentedBounds = bounds;
		}
	}
	
	private function createResizeMCs():Void{
		var r:Number = RESIZE_MC_WIDTH;
		var creater:ElementCreater = ElementCreater.getInstance();
		
		resizeMC.removeMovieClip();
		resizeMC = com.createMovieClipOnRoot();
		resizeArrowMC = creater.createMC(resizeMC, "resizeArrowMC");
		var w:Number = 1; //arrowAxisHalfWidth
		var arrowPoints:Array = [{x:-r*2, y:0}, {x:-r, y:-r}, {x:-r, y:-w},
								 {x:r, y:-w}, {x:r, y:-r}, {x:r*2, y:0},
								 {x:r, y:r}, {x:r, y:w}, {x:-r, y:w},
								 {x:-r, y:r}];
								 
		var gdi:Graphics;
		gdi = new Graphics(resizeArrowMC);
		gdi.drawPolygon(new Pen(resizeArrowColor.getRGB(), 4, 40), arrowPoints);
		gdi.fillPolygon(new SolidBrush(resizeArrowLightColor), arrowPoints);
		gdi.drawPolygon(new Pen(resizeArrowDarkColor, 1), arrowPoints);
		resizeArrowMC._visible = false;
		
		boundsMC = creater.createMC(resizeMC, "boundsMC");
		boundsMC._visible = false;
		
		topResizeMC = creater.createMC(resizeMC, "topResizeMC");
		leftResizeMC = creater.createMC(resizeMC, "leftResizeMC");
		rightResizeMC = creater.createMC(resizeMC, "rightResizeMC");
		bottomResizeMC = creater.createMC(resizeMC, "bottomResizeMC");
		
		topLeftResizeMC = creater.createMC(resizeMC, "topLeftResizeMC");
		topRightResizeMC = creater.createMC(resizeMC, "topRightResizeMC");
		bottomLeftResizeMC = creater.createMC(resizeMC, "bottomLeftResizeMC");
		bottomRightResizeMC = creater.createMC(resizeMC, "bottomRightResizeMC");	
		
		DefaultResizeBarHandler.createHandler(this, topResizeMC, 90, createResizeStrategy(0, -1));
		DefaultResizeBarHandler.createHandler(this, leftResizeMC, 0, createResizeStrategy(-1, 0));
		DefaultResizeBarHandler.createHandler(this, rightResizeMC, 0, createResizeStrategy(1, 0));
		DefaultResizeBarHandler.createHandler(this, bottomResizeMC, 90, createResizeStrategy(0, 1));
		
		DefaultResizeBarHandler.createHandler(this, topLeftResizeMC, 45, createResizeStrategy(-1, -1));
		DefaultResizeBarHandler.createHandler(this, topRightResizeMC, -45, createResizeStrategy(1, -1));
		DefaultResizeBarHandler.createHandler(this, bottomLeftResizeMC, -45, createResizeStrategy(-1, 1));
		DefaultResizeBarHandler.createHandler(this, bottomRightResizeMC, 45, createResizeStrategy(1, 1));
		
		var brush:SolidBrush = new SolidBrush(new ASColor(0, 0));
		gdi = new Graphics(topResizeMC);
		gdi.fillRectangle(brush, 0, 0, r, r);
		gdi = new Graphics(leftResizeMC);
		gdi.fillRectangle(brush, 0, 0, r, r);
		gdi = new Graphics(rightResizeMC);
		gdi.fillRectangle(brush, -r, 0, r, r);	
		gdi = new Graphics(bottomResizeMC);
		gdi.fillRectangle(brush, 0, -r, r, r);	
		
		gdi = new Graphics(topLeftResizeMC);
		gdi.fillRectangle(brush, 0, 0, r*2, r);
		gdi.fillRectangle(brush, 0, 0, r, r*2);
		gdi = new Graphics(topRightResizeMC);
		gdi.fillRectangle(brush, -r*2, 0, r*2, r);
		gdi.fillRectangle(brush, -r, 0, r, r*2);
		gdi = new Graphics(bottomLeftResizeMC);
		gdi.fillRectangle(brush, 0, -r, r*2, r);
		gdi.fillRectangle(brush, 0, -r*2, r, r*2);
		gdi = new Graphics(bottomRightResizeMC);
		gdi.fillRectangle(brush, -r*2, -r, r*2, r);
		gdi.fillRectangle(brush, -r, -r*2, r, r*2);
		
		resizeMC._visible = enabled;
	}
	
	/**
	 * Override this method if you want to use another resize strategy.
	 */
	private function createResizeStrategy(wSign:Number, hSign:Number):ResizeStrategy{
		return new ResizeStrategyImp(wSign, hSign); 
	}
	
	public function setEnabled(e:Boolean):Void{
		enabled = e;
		resizeMC._visible = enabled;
	}
	
	public function isEnabled():Boolean{
		return enabled;
	}
	
	public function destroy( ) : Void{
		com.removeEventListener(listener);
		com = null;
		resizeMC.removeMovieClip();
		resizeMC = null;
	}	
	
	/**
	 * Locate the resizer mcs to fit the component.
	 */
	private function locate():Void{
		//var x:Number = 0;
		//var y:Number = 0;
		var w:Number = com.getWidth();
		var h:Number = com.getHeight();
		var r:Number = RESIZE_MC_WIDTH;
		
		topResizeMC._width = Math.max(0, w-r*2);
		topResizeMC._x = r;
		topResizeMC._y = 0;
		leftResizeMC._height = Math.max(0, h-r*2);
		leftResizeMC._x = 0;
		leftResizeMC._y = r;
		rightResizeMC._height = Math.max(0, h-r*2);
		rightResizeMC._x = w;
		rightResizeMC._y = r;
		bottomResizeMC._width = Math.max(0, w-r*2);
		bottomResizeMC._x = r;
		bottomResizeMC._y = h;
		
		topLeftResizeMC._x = 0;
		topLeftResizeMC._y = 0;
		topRightResizeMC._x = w;
		topRightResizeMC._y = 0;
		bottomLeftResizeMC._x = 0;
		bottomLeftResizeMC._y = h;
		bottomRightResizeMC._x = w;
		bottomRightResizeMC._y = h;
	}
}
