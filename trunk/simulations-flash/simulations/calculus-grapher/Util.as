//General utilities.  Useful for all sims
//test comment for merge
package{
	import flash.events.*;
	import flash.display.Sprite;
	import flash.geom.*;
	
	public class Util extends Sprite{
		
		//public static var ORIGINX:Number = 0.10*stage.stageWidth;
		public static var PINGRABBED:Boolean = false;
		
		public function Util(){
			//this is a static class
		}//end of constructor
		
		public static function setXYPosition(mySprite:Sprite, xPos:Number, yPos:Number):void{
			mySprite.x = xPos;
			mySprite.y = yPos;
		}//end of setXYPostion()
	
		public static function makeClipDraggable(target:Sprite):void{
			target.buttonMode = true;
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			//var localRef:Object = this;
			var theStage:Object = target.parent;
			var clickOffset:Point;
			function startTargetDrag(evt:MouseEvent):void{	
				clickOffset = new Point(evt.localX, evt.localY);
				//trace("evt.localY: "+evt.localY);
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				clickOffset = null;
			}
			
			function dragTarget(evt:MouseEvent):void{
				
				if(clickOffset != null){  //if dragging
					target.x = theStage.mouseX - clickOffset.x;
					target.y = theStage.mouseY - clickOffset.y;
					evt.updateAfterEvent();
				}
			}

		}//end of makeClipDraggable
		
	}//end of class
	
}//end of package