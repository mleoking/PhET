/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{
	
import flash.display.DisplayObject;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.*;

import org.aswing.event.*;
import org.aswing.geom.*;

/**
 * Dispatched when the mouse released or released out side.
 * If you need a event like AS2 <code>onRelease</code> you can 
 * use <code>Event.CLICK</code>
 * 
 * @eventType org.aswing.event.ReleaseEvent.RELEASE
 */
[Event(name="release", type="org.aswing.event.ReleaseEvent")]

/**
 * Dispatched only when the mouse released out side.
 *
 * @eventType org.aswing.event.ReleaseEvent.RELEASE_OUT_SIDE
 */
[Event(name="releaseOutSide", type="org.aswing.event.ReleaseEvent")]

/**
 * AsWing component based Sprite.
 * <p>
 * The AsWing Component Assets structure:(Assets means flash player display objects)
 * <pre>
 *             | -- foreground decorator asset
 *             |
 *             |    [other assets, there is no depth restrict between them, see below ]
 * AWSprite -- | -- [icon, border, ui creation, children component assets ...]          
 *             |    [they are above background decorator and below foreground decorator]
 *             |
 *             | -- background decorator asset
 * </pre>
 */
public class AWSprite extends Sprite
{
	private var foregroundChild:DisplayObject;
	private var backgroundChild:DisplayObject;
	
	private var clipMasked:Boolean = false;
	private var clipMaskRect:IntRectangle;
	private var content:Sprite;
	private var maskShape:Shape;
	private var usingBitmap:Boolean;
	
	public function AWSprite(clipMasked:Boolean=false){
		super();
		focusRect = false;
		usingBitmap = false;
		clipMaskRect = new IntRectangle();
		setClipMasked(clipMasked);
		addEventListener(MouseEvent.MOUSE_DOWN, __awSpriteMouseDownListener);
	}
	
	protected function d_addChild(child:DisplayObject):DisplayObject{
		return super.addChild(child);
	}
	
	protected function d_addChildAt(child:DisplayObject, index:int):DisplayObject{
		return super.addChildAt(child, index);
	}
	
	override public function addChildAt(child:DisplayObject, index:int):DisplayObject{
		if(usingBitmap){
			return content.addChildAt(child, index);
		}else{
			return d_addChildAt(child, index);
		}
	}
	
	protected function d_removeChild(child:DisplayObject):DisplayObject{
		return super.removeChild(child);
	}
	
	/**
	 * Returns whether or not the child is this sprite's direct child.
	 */
	protected function isChild(child:DisplayObject):Boolean{
		if(usingBitmap){
			return child.parent == content;
		}else{
			return child.parent == this;
		}		
	}
	
	override public function removeChild(child:DisplayObject):DisplayObject{
		if(usingBitmap){
			return content.removeChild(child);
		}else{
			return d_removeChild(child);
		}
	}
	
	protected function d_removeChildAt(index:int):DisplayObject{
		return super.removeChildAt(index);
	}
	
	override public function removeChildAt(index:int):DisplayObject{
		if(usingBitmap){
			return content.removeChildAt(index);
		}else{
			return d_removeChildAt(index);
		}
	}
	
	protected function d_getChildAt(index:int):DisplayObject{
		return super.getChildAt(index);
	}
	
	override public function getChildAt(index:int):DisplayObject{
		if(usingBitmap){
			return content.getChildAt(index);
		}else{
			return d_getChildAt(index);
		}
	}
	
	protected function d_getChildByName(name:String):DisplayObject{
		return super.getChildByName(name);
	}
	
	override public function getChildByName(name:String):DisplayObject{
		if(usingBitmap){
			return content.getChildByName(name);
		}else{
			return d_getChildByName(name);
		}
	}
	
	protected function d_getChildIndex(child:DisplayObject):int{
		return super.getChildIndex(child);
	}
	
	override public function getChildIndex(child:DisplayObject):int{
		if(usingBitmap){
			return content.getChildIndex(child);
		}else{
			return d_getChildIndex(child);
		}
	}
	
	/**
	 * Returns whether child is directly child of this sprite, true only if getChildIndex(child) >= 0.
	 * @return true only if getChildIndex(child) >= 0.
	 */
	public function containsChild(child:DisplayObject):Boolean{
		if(usingBitmap){
			return child.parent.parent == this;
		}else{
			return child.parent == this;
		}		
	}
	
	protected function d_setChildIndex(child:DisplayObject, index:int):void{
		super.setChildIndex(child, index);
	}
	
	override public function setChildIndex(child:DisplayObject, index:int):void{
		if(usingBitmap){
			content.setChildIndex(child, index);
		}else{
			d_setChildIndex(child, index);
		}
	}
	
	protected function d_swapChildren(child1:DisplayObject, child2:DisplayObject):void{
		super.swapChildren(child1, child2);
	}
	
	override public function swapChildren(child1:DisplayObject, child2:DisplayObject):void{
		if(usingBitmap){
			content.swapChildren(child1, child2);
		}else{
			d_swapChildren(child1, child2);
		}
	}
	
	protected function d_swapChildrenAt(index1:int, index2:int):void{
		super.swapChildrenAt(index1, index2);
	}
	
	override public function swapChildrenAt(index1:int, index2:int):void{
		if(usingBitmap){
			content.swapChildrenAt(index1, index2);
		}else{
			d_swapChildrenAt(index1, index2);
		}
	}
	
	protected function get d_numChildren():int{
		return super.numChildren;
	}
	
	override public function get numChildren():int{
		if(usingBitmap){
			return content.numChildren;
		}else{
			return d_numChildren;
		}
	}
	
	/**
	 * Adds a child DisplayObject instance to this DisplayObjectContainer instance. 
	 * The child is added to the front (top) of all other children except foreground decorator child(It is topest)
	 *  in this DisplayObjectContainer instance. 
	 * (To avoid this restrict and add a child to a specific index position, use the <code>addChildAt()</code> method.)
	 * (<b>Note:</b> Generally if you don't want to break the component asset depth management, use 
	 * getHighestIndexUnderForeground() and getLowestIndexAboveBackground() to get the 
	 * right depth you can use. You can also refer to getChildIndex() to
	 * insert child after or before an existing child) 
	 * 
	 * @param dis The DisplayObject instance to add as a child of this DisplayObjectContainer instance. 
	 * @see #getLowestIndexAboveBackground()
	 * @see #getHighestIndexUnderForeground()
	 * @see #http://livedocs.macromedia.com/flex/2/langref/flash/display/DisplayObjectContainer.html#getChildIndex()
	 */
	public override function addChild(dis:DisplayObject):DisplayObject{
		if(foregroundChild != null){
			if(usingBitmap){
				return content.addChildAt(dis, content.getChildIndex(foregroundChild));
			}
			d_addChild(dis);
			d_swapChildren(dis, foregroundChild);
			return dis;
		}
		if(usingBitmap){
			return content.addChild(dis);
		}
		return d_addChild(dis);
	}
	
	/**
	 * Returns the current top index for a new child(none forground child).
	 * @return the current top index for a new child that is not a foreground child.
	 */
	public function getHighestIndexUnderForeground():int{
		if(foregroundChild == null){
			return numChildren;
		}else{
			return numChildren - 1;
		}
	}
	
	/**
	 * Returns the current bottom index for none background child.
	 * @return the current bottom index for child that is not a background child.
	 */		
	public function getLowestIndexAboveBackground():int{
		if(backgroundChild == null){
			return 0;
		}else{
			return 1;
		}
	}
	
	override public function hitTestPoint(x:Number, y:Number, shapeFlag:Boolean=false):Boolean{
		if(isClipMasked() && !shapeFlag){
			return maskShape.hitTestPoint(x, y, shapeFlag);
		}else{
			//TODO use bounds to test the x, y
			return super.hitTestPoint(x, y, shapeFlag);
		}
	}
	
	override public function hitTestObject(obj:DisplayObject):Boolean{
		if(isClipMasked()){
			return maskShape.hitTestObject(obj);
		}else{
			//TODO use bounds to test the obj
			return super.hitTestObject(obj);
		}
	}
	
	/**
	 * Brings a child to top.
	 * This method will keep foreground child on top, if you bring a other object 
	 * to top, this method will only bring it on top of other objects
	 * (mean on top of others but bellow the foreground child).
	 * @param child the child to be bringed to top.
	 */
	public function bringToTop(child:DisplayObject):void{
		var index:int = numChildren-1;
		if(foregroundChild != null){
			if(foregroundChild != child){
				index = numChildren-2;
			}
		}
		setChildIndex(child, index);
	}
	

	/**
	 * Brings a child to bottom.
	 * This method will keep background child on bottom, if you bring a other object 
	 * to bottom, this method will only bring it at bottom of other objects
	 * (mean at bottom of others but on top of the background child).
	 * @param child the child to be bringed to bottom.
	 */	
	public function bringToBottom(child:DisplayObject):void{
		var index:int = 0;
		if(backgroundChild != null){
			if(backgroundChild != child){
				index = 1;
			}
		}
		setChildIndex(child, index);
	}
	
	/**
	 * Sets the child to be the component background, it will be add to the bottom of all other children. 
	 * (old backgournd child will be removed). pass no paramter (null) to remove the background child.
	 * 
	 * @param child the background child to be added.
	 */
	protected function setBackgroundChild(child:DisplayObject = null):void{
		if(child != backgroundChild){
			if(backgroundChild != null){
				removeChild(backgroundChild);
			}
			backgroundChild = child;
			if(child != null){
				addChildAt(child, 0);
			}
		}
	}
	
	/**
	 * Returns the background child. 
	 * @return the background child.
	 * @see #setBackgroundChild()
	 */
	protected function getBackgroundChild():DisplayObject{
		return backgroundChild;
	}
	
	/**
	 * Sets the child to be the component foreground, it will be add to the top of all other children. 
	 * (old foregournd child will be removed), pass no paramter (null) to remove the foreground child.
	 * 
	 * @param child the foreground child to be added.
	 */
	protected function setForegroundChild(child:DisplayObject = null):void{
		if(child != foregroundChild){
			if(foregroundChild != null){
				removeChild(foregroundChild);
			}
			foregroundChild = child;
			if(child != null){
				addChild(child);
			}
		}
	}
	
	/**
	 * Returns the foreground child. 
	 * @return the foreground child.
	 * @see #setForegroundChild()
	 */
	protected function getForegroundChild():DisplayObject{
		return foregroundChild;
	}

	/**
	 * Sets whether the component clip should be masked by its bounds. By default it is true.
	 * <p>
	 * AsWing A3 use <code>scrollRect</code> property to do the clip mask.
	 * </p>
	 * @param m whether the component clip should be masked.
	 * @see #isClipMasked()
	 */
	public function setClipMasked(m:Boolean):void{
		if(m != clipMasked){
			clipMasked = m;
			setUsingBitmap(cacheAsBitmap && clipMasked);
			if(clipMasked){
				checkCreateMaskShape();
				if(maskShape.parent != this){
					d_addChild(maskShape);
					mask = maskShape;
				}
				setClipMaskRect(clipMaskRect);
			}else{
				if(maskShape != null && maskShape.parent == this){
					d_removeChild(maskShape);
				}
				mask = null;
			}
		}
	}
	
	protected function setClipMaskRect(b:IntRectangle):void{
		if(maskShape){
			maskShape.x = b.x;
			maskShape.y = b.y;
			maskShape.height = b.height;
			maskShape.width = b.width;
		}
		clipMaskRect.setRect(b);
	}
	
	private function setUsingBitmap(b:Boolean):void{
		if(usingBitmap != b){
			usingBitmap = b;
			usingBitmapChanged();
		}
	}
	
	private function usingBitmapChanged():void{
		var children:Array;
		var n:int;
		var i:int;
		if(usingBitmap){
			if(!content){
				content = new Sprite();
				content.tabEnabled = false;
				content.mouseEnabled = false;
			}
			//move children from this to content
			children = new Array();
			n = d_numChildren;
			for(i=0; i<n; i++){
				if(d_getChildAt(i) != maskShape){
					children.push(d_getChildAt(i));
				}
			}
			for(i=0; i<children.length; i++){
				content.addChild(children[i]);
			}
			
			d_addChild(content);
			if(clipMasked){
				super.mask = null;
				content.mask = maskShape;
			}
		}else{
			d_removeChild(content);
			
			//move children from content to this
			children = new Array();
			n = content.numChildren;
			for(i=0; i<n; i++){
				children.push(content.getChildAt(i));
			}
			for(i=0; i<children.length; i++){
				d_addChild(children[i]);
			}
			
			if(clipMasked){
				content.mask = null;
				super.mask = maskShape;
			}
		}
	}
	
	override public function set mask(value:DisplayObject):void{
		if(usingBitmap){
			content.mask = value;
		}else{
			super.mask = value;
		}
	}
	
	override public function get mask():DisplayObject{
		if(usingBitmap){
			return content.mask;
		}else{
			return super.mask;
		}
	}
	
	override public function set filters(value:Array):void{
		super.filters = value;
		setUsingBitmap(super.cacheAsBitmap && clipMasked);
	}
	
	override public function set cacheAsBitmap(value:Boolean):void{
		super.cacheAsBitmap = value;
		setUsingBitmap(value && clipMasked);
	}
	
	private function checkCreateMaskShape():void{
		if(!maskShape){
			maskShape = new Shape();
			maskShape.graphics.beginFill(0);
			maskShape.graphics.drawRect(0, 0, 1, 1);
			maskShape.graphics.endFill();
		}
	}
	
	/**
	 * Returns whether the component clip should be masked by its bounds. By default it is true.
	 * <p>
	 * AsWing A3 use <code>scrollRect</code> property to do the clip mask.
	 * </p>
	 * @return whether the component clip should be masked.
	 * @see #setClipMasked()
	 */
	public function isClipMasked():Boolean{
		return clipMasked;
	}
	
	private var pressedTarget:DisplayObject;
	private function __awSpriteMouseDownListener(e:MouseEvent):void{
		pressedTarget = e.target as DisplayObject;
		if(stage){
			stage.addEventListener(MouseEvent.MOUSE_UP, __awStageMouseUpListener, false, 0, true);
			addEventListener(Event.REMOVED_FROM_STAGE, __awStageRemovedFrom);
		}
	}
	private function __awStageRemovedFrom(e:Event):void{
		pressedTarget = null;
		stage.removeEventListener(MouseEvent.MOUSE_UP, __awStageMouseUpListener);
	}
	private function __awStageMouseUpListener(e:MouseEvent):void{
		if(stage) stage.removeEventListener(MouseEvent.MOUSE_UP, __awStageMouseUpListener);
		var isOutSide:Boolean = false;
		var target:DisplayObject = e.target as DisplayObject;
		if(!(this == target || AsWingUtils.isAncestorDisplayObject(this, target))){
			isOutSide = true;
		}
		dispatchEvent(new ReleaseEvent(ReleaseEvent.RELEASE, pressedTarget, isOutSide, e));
		if(isOutSide){
			dispatchEvent(new ReleaseEvent(ReleaseEvent.RELEASE_OUT_SIDE, pressedTarget, isOutSide, e));
		}

		pressedTarget = null;
	}
	
	override public function toString():String{
		var p:DisplayObject = this;
		var str:String = p.name;
		while(p.parent != null){
			var name:String = (p.parent == p.stage ? "Stage" : p.parent.name);
			p = p.parent;
			str = name + "." + str;
		}
		return str;
	}
}
}