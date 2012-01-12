/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.resizer{
	
import flash.events.Event;
import flash.events.MouseEvent;

import org.aswing.AWSprite;
import org.aswing.event.*;
	
/**
 * The Handler for Resizer's mc bars.
 * @author iiley
 */
public class DefaultResizeBarHandler{
	
	private var resizer:DefaultResizer;
	private var mc:AWSprite;
	private var arrowRotation:Number;
	private var strategy:ResizeStrategy;
	
	public function DefaultResizeBarHandler(resizer:DefaultResizer, barMC:AWSprite, arrowRotation:Number, strategy:ResizeStrategy){
		this.resizer = resizer;
		mc = barMC;
		this.arrowRotation = arrowRotation;
		this.strategy = strategy;
		handle();
	}
	
	public static function createHandler(resizer:DefaultResizer, barMC:AWSprite, arrowRotation:Number, strategy:ResizeStrategy):DefaultResizeBarHandler{
		return new DefaultResizeBarHandler(resizer, barMC, arrowRotation, strategy);
	}
	
	private function handle():void{
		mc.addEventListener(MouseEvent.ROLL_OVER, __onRollOver);
		mc.addEventListener(MouseEvent.ROLL_OUT, __onRollOut);
		mc.addEventListener(MouseEvent.MOUSE_DOWN, __onPress);
		mc.addEventListener(MouseEvent.MOUSE_UP, __onUp);
		mc.addEventListener(MouseEvent.CLICK, __onRelease);
		mc.addEventListener(ReleaseEvent.RELEASE_OUT_SIDE, __onReleaseOutside);
		mc.addEventListener(Event.REMOVED_FROM_STAGE, __onDestroy);
	}
	
	private function __onRollOver(e:MouseEvent):void{
		if(!resizer.isResizing() && (e ==null || !e.buttonDown)){
			resizer.startArrowCursor();
			__rotateArrow();
			if(mc.stage){
				mc.stage.addEventListener(MouseEvent.MOUSE_MOVE, __rotateArrow, false, 0, true);
			}
		}
	}
	
	private function __onRollOut(e:MouseEvent):void{
		if(!resizer.isResizing() && !e.buttonDown){
			if(mc.stage){
				mc.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __rotateArrow);
			}
			resizer.stopArrowCursor();
		}
	}
	
	private function __onPress(e:MouseEvent):void{
		resizer.setResizing(true);
		startResize(e);
		if(mc.stage){
			mc.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __rotateArrow);
			mc.stage.addEventListener(MouseEvent.MOUSE_MOVE, resizing, false, 0, true);
		}
	}
	
	private function __onUp(e:MouseEvent):void{
		__onRollOver(null);
	}
	
	private function __onRelease(e:Event):void{
		resizer.setResizing(false);
		resizer.stopArrowCursor();
		if(mc.stage){
			mc.stage.removeEventListener(MouseEvent.MOUSE_MOVE, resizing);
		}
		finishResize();
	}
	
	private function __onReleaseOutside(e:Event):void{
		__onRelease(e);
	}
	
	private function __onDestroy(e:Event):void{
		mc.stage.removeEventListener(MouseEvent.MOUSE_MOVE, resizing);
		mc.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __rotateArrow);
	}
	
	private function __rotateArrow(e:Event=null):void{
		resizer.setArrowRotation(arrowRotation);
	}
	
	private function startResize(e:MouseEvent):void{
		resizer.startResize(strategy, e);
	}
	
	private function resizing(e:MouseEvent):void{
		resizer.resizing(strategy, e);
	}
	
	private function finishResize():void{
		resizer.finishResize(strategy);
	}
}
}