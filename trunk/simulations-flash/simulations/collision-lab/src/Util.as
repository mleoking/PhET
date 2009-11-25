﻿
//General utilities.  Useful for all sims

package{
	import flash.events.*;
	import flash.display.Sprite;
	import flash.geom.*;
	
	public class Util extends Sprite{
		
		//public static var ORIGINX:Number = 0.10*stage.stageWidth;
		public static var PINGRABBED:Boolean = false;
		public static var BACKGROUNDCOLOR:uint = 0xFFFF99;
		
		public function Util(){
			//this is a static class
		}//end of constructor
		
		public static function setXYPosition(mySprite:Sprite, xPos:Number, yPos:Number):void{
			mySprite.x = xPos;
			mySprite.y = yPos;
		}//end of setXYPostion()
		
		public static function testFunction():void{
			trace("Util.testFunction() called.");
		}
		//Will not work with rotated Sprites
		public static function makeClipDraggable(target:Sprite):void{
			target.buttonMode = true;
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			var theStage:Object = target.parent;
			var clickOffset:Point;
			function startTargetDrag(evt:MouseEvent):void{	
				//problem with localX, localY if sprite is rotated.
				clickOffset = new Point(evt.localX, evt.localY);
				trace("evt.localX: "+evt.localX);
				trace("evt.localY: "+evt.localY);
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				clickOffset = null;
			}
			function dragTarget(evt:MouseEvent):void{
				if(clickOffset != null){  //if dragging
				
					//trace("theStage.mouseX: "+theStage.mouseX);
					//trace("theStage.mouseY: "+theStage.mouseY);
					target.x = theStage.mouseX - clickOffset.x;
					target.y = theStage.mouseY - clickOffset.y;
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
		}//end of makeClipDraggable
		
		public static function round(input:Number, nPlaces:int):Number{
			var result:Number;
			var factor:Number = Math.pow(10, nPlaces);
			result = Math.round(factor*input)/factor;
			return result
		}
		
	}//end of class
	
	
	
}//end of package