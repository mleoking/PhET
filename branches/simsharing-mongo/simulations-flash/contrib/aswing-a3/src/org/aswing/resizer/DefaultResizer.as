/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.resizer{

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;

import org.aswing.*;
import org.aswing.event.AWEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.UIResource;
import org.aswing.util.DepthManager;

/**
 * Resizer is a resizer for Components to make it resizable when user mouse on 
 * component's edge.
 * @author iiley
 */
public class DefaultResizer implements Resizer, UIResource{
	
	protected static var RESIZE_MC_WIDTH:Number = 4;
		
	protected var owner:Component;

	//-----------resize equiments--------------
	protected var resizeMC:Sprite;
	
	protected var resizeArrowCursor:DisplayObject;
	protected var boundsShape:Shape;
	
	protected var topResizeMC:AWSprite;
	protected var leftResizeMC:AWSprite;
	protected var bottomResizeMC:AWSprite;
	protected var rightResizeMC:AWSprite;
	
	protected var topLeftResizeMC:AWSprite;
	protected var topRightResizeMC:AWSprite;
	protected var bottomLeftResizeMC:AWSprite;
	protected var bottomRightResizeMC:AWSprite;
	
	private var startX:Number;
	private var startY:Number;
	private var startBounds:IntRectangle;
	
	protected var enabled:Boolean;
	protected var resizeDirectly:Boolean;
	
	protected var resizeArrowColor:ASColor;
	protected var resizeArrowLightColor:ASColor;
	protected var resizeArrowDarkColor:ASColor;
	
	/**
	 * Create a Resizer for specified component.
	 */
	public function DefaultResizer(){
		enabled = true;
		resizeDirectly = false;
		startX = 0;
		startY = 0;
		startBounds = new IntRectangle();
		//Default colors
	    resizeArrowColor = UIManager.getColor("resizeArrow");
	    resizeArrowLightColor = UIManager.getColor("resizeArrowLight");
	    resizeArrowDarkColor = UIManager.getColor("resizeArrowDark");
	}
	
	public function setResizeArrowColor(c:ASColor):void{
		resizeArrowColor = c;
	}
	
	public function setResizeArrowLightColor(c:ASColor):void{
		resizeArrowLightColor = c;
	}
	
	public function setResizeArrowDarkColor(c:ASColor):void{
		resizeArrowDarkColor = c;
	}
	
	public function setOwner(c:Component):void{
		if(owner != null){
			owner.removeEventListener(AWEvent.PAINT, locate);
			if(resizeMC != null){
				owner.removeChild(resizeMC);
			}
			hideBoundsMC();
		}
		owner = c;
		if(owner != null){
			owner.addEventListener(AWEvent.PAINT, locate);
			if(resizeMC == null){
				createResizeMCs();
			}
			owner.addChildAt(resizeMC, owner.numChildren);
		}
		locate();
	}
		
	/**
	 * <p>Indicate whether need resize component directly when drag the resizer arrow.
	 * <p>if set to false, there will be a rectange to represent then size what will be resized to.
	 * <p>if set to true, the component will be resize directly when drag, but this is need more cpu counting.
	 * <p>Default is false.
	 * @see org.aswing.JFrame
	 */	
	public function setResizeDirectly(r:Boolean):void{
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
	
	public function setArrowRotation(r:Number):void{
		resizeArrowCursor.rotation = r;
	}
	
	public function startArrowCursor():void{
		if(resizeMC && resizeMC.stage){
			CursorManager.getManager(resizeMC.stage).showCustomCursor(resizeArrowCursor);
		}
	}
	
	public function stopArrowCursor():void{
		if(resizeMC && resizeMC.stage){
			CursorManager.getManager(resizeMC.stage).hideCustomCursor(resizeArrowCursor);
		}
	}
	
	private var resizingNow:Boolean = false;
	public function setResizing(b:Boolean):void{
		resizingNow = b;
	}
	
	public function isResizing():Boolean{
		return resizingNow;
	}
	
	public function startResize(strategy:ResizeStrategy, e:MouseEvent):void{
		if(!resizeDirectly){
			representRect(owner.getComBounds());
		}
		startX = e.stageX;
		startY = e.stageY;
		startBounds = owner.getComBounds();
	}
	
	public function resizing(strategy:ResizeStrategy, e:MouseEvent):void{
		var bounds:IntRectangle = strategy.getBounds(
			startBounds, 
			owner.getMinimumSize(), 
			owner.getMaximumSize(), 
			e.stageX - startX, e.stageY - startY);
		if(resizeDirectly){
			owner.setBounds(bounds);
			owner.revalidate();
			e.updateAfterEvent();
		}else{
			representRect(bounds);
		}
	}
	
	public function finishResize(strategy:ResizeStrategy):void{
		if(!resizeDirectly){
			owner.setComBounds(lastRepresentedBounds);
			hideBoundsMC();
			owner.revalidate();
		}
	}
	
	
	private function hideBoundsMC():void{
		var par:DisplayObjectContainer = owner.parent;
		if(boundsShape != null && par != null && par.contains(boundsShape)){
			par.removeChild(boundsShape);
		}
	}
	
	private var lastRepresentedBounds:IntRectangle;
	
	private function representRect(bounds:IntRectangle):void{
		if(!resizeDirectly){
			var par:DisplayObjectContainer = owner.parent;
			if(!par.contains(boundsShape)){
				par.addChild(boundsShape);
			}
			DepthManager.bringToTop(boundsShape);
			var x:Number = bounds.x;
			var y:Number = bounds.y;
			var w:Number = bounds.width;
			var h:Number = bounds.height;
			var g:Graphics2D = new Graphics2D(boundsShape.graphics);
			boundsShape.graphics.clear();
			g.drawRectangle(new Pen(resizeArrowLightColor), x-1,y-1,w+2,h+2);
			g.drawRectangle(new Pen(resizeArrowColor), x,y,w,h);
			g.drawRectangle(new Pen(resizeArrowDarkColor), x+1,y+1,w-2,h-2);
			lastRepresentedBounds = bounds;
		}
	}
	
	protected function createResizeMCs():void{
		var r:Number = RESIZE_MC_WIDTH;
		resizeMC = new Sprite();
		resizeMC.name = "resizer";
		resizeArrowCursor = Cursor.createCursor(Cursor.H_RESIZE_CURSOR);
		resizeArrowCursor.name = "resizeCursor";
		boundsShape = new Shape();
		boundsShape.name = "bounds";
		
		topResizeMC = new AWSprite();
		leftResizeMC = new AWSprite();
		rightResizeMC = new AWSprite();
		bottomResizeMC = new AWSprite();
		
		topLeftResizeMC = new AWSprite();
		topRightResizeMC = new AWSprite();
		bottomLeftResizeMC = new AWSprite();
		bottomRightResizeMC = new AWSprite();
		
		resizeMC.addChild(topResizeMC);
		resizeMC.addChild(leftResizeMC);
		resizeMC.addChild(rightResizeMC);
		resizeMC.addChild(bottomResizeMC);
		
		resizeMC.addChild(topLeftResizeMC);
		resizeMC.addChild(topRightResizeMC);
		resizeMC.addChild(bottomLeftResizeMC);
		resizeMC.addChild(bottomRightResizeMC);
		
		DefaultResizeBarHandler.createHandler(this, topResizeMC, 90, createResizeStrategy(0, -1));
		DefaultResizeBarHandler.createHandler(this, leftResizeMC, 0, createResizeStrategy(-1, 0));
		DefaultResizeBarHandler.createHandler(this, rightResizeMC, 0, createResizeStrategy(1, 0));
		DefaultResizeBarHandler.createHandler(this, bottomResizeMC, 90, createResizeStrategy(0, 1));
		
		DefaultResizeBarHandler.createHandler(this, topLeftResizeMC, 45, createResizeStrategy(-1, -1));
		DefaultResizeBarHandler.createHandler(this, topRightResizeMC, -45, createResizeStrategy(1, -1));
		DefaultResizeBarHandler.createHandler(this, bottomLeftResizeMC, -45, createResizeStrategy(-1, 1));
		DefaultResizeBarHandler.createHandler(this, bottomRightResizeMC, 45, createResizeStrategy(1, 1));
		
		var brush:SolidBrush = new SolidBrush(new ASColor(0, 0));
		var gdi:Graphics2D = new Graphics2D(topResizeMC.graphics);
		gdi.fillRectangle(brush, 0, 0, r, r);
		gdi = new Graphics2D(leftResizeMC.graphics);
		gdi.fillRectangle(brush, 0, 0, r, r);
		gdi = new Graphics2D(rightResizeMC.graphics);
		gdi.fillRectangle(brush, -r, 0, r, r);	
		gdi = new Graphics2D(bottomResizeMC.graphics);
		gdi.fillRectangle(brush, 0, -r, r, r);	
		
		gdi = new Graphics2D(topLeftResizeMC.graphics);
		gdi.fillRectangle(brush, 0, 0, r*2, r);
		gdi.fillRectangle(brush, 0, 0, r, r*2);
		gdi = new Graphics2D(topRightResizeMC.graphics);
		gdi.fillRectangle(brush, -r*2, 0, r*2, r);
		gdi.fillRectangle(brush, -r, 0, r, r*2);
		gdi = new Graphics2D(bottomLeftResizeMC.graphics);
		gdi.fillRectangle(brush, 0, -r, r*2, r);
		gdi.fillRectangle(brush, 0, -r*2, r, r*2);
		gdi = new Graphics2D(bottomRightResizeMC.graphics);
		gdi.fillRectangle(brush, -r*2, -r, r*2, r);
		gdi.fillRectangle(brush, -r, -r*2, r, r*2);
		
		resizeMC.visible = enabled;
	}
	
	/**
	 * Override this method if you want to use another resize strategy.
	 */
	private function createResizeStrategy(wSign:Number, hSign:Number):ResizeStrategy{
		return new ResizeStrategyImp(wSign, hSign); 
	}
	
	public function setEnabled(e:Boolean):void{
		enabled = e;
		resizeMC.visible = enabled;
	}
	
	public function isEnabled():Boolean{
		return enabled;
	}
		
	/**
	 * Locate the resizer mcs to fit the component.
	 */
	private function locate(e:Event=null):void{
		//var x:Number = 0;
		//var y:Number = 0;
		if(owner == null){
			return;
		}
		var w:Number = owner.getWidth();
		var h:Number = owner.getHeight();
		var r:Number = RESIZE_MC_WIDTH;
		
		topResizeMC.width = Math.max(0, w-r*2);
		topResizeMC.x = r;
		topResizeMC.y = 0;
		leftResizeMC.height = Math.max(0, h-r*2);
		leftResizeMC.x = 0;
		leftResizeMC.y = r;
		rightResizeMC.height = Math.max(0, h-r*2);
		rightResizeMC.x = w;
		rightResizeMC.y = r;
		bottomResizeMC.width = Math.max(0, w-r*2);
		bottomResizeMC.x = r;
		bottomResizeMC.y = h;
		
		topLeftResizeMC.x = 0;
		topLeftResizeMC.y = 0;
		topRightResizeMC.x = w;
		topRightResizeMC.y = 0;
		bottomLeftResizeMC.x = 0;
		bottomLeftResizeMC.y = h;
		bottomRightResizeMC.x = w;
		bottomRightResizeMC.y = h;
	}
}
}