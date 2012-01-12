//general utilities

class Util{
	public static var STAGEH:Number = Stage.height;
	public static var STAGEW:Number = Stage.width;
	function Util(){
		//this is a static class 
	}
	
	public static function setXYPosition(myClip:MovieClip, xPos:Number, yPos:Number):Void{
		//trace("myClip:"+myClip+"    xPos:"+xPos);
		myClip._x = xPos;
		myClip._y = yPos;
	}//end of setXYPostion()
	
	//myRandNbr(max) returns a random integer between 0 and max
	public static function myRandNbr(max:Number):Number{
		var myRand = Math.floor((max+1)*Math.random());
		return myRand;
	}
	
	public static function makeParentClipDraggable(myClip:MovieClip, updateOnMove:Function, xMin:Number,xMax:Number,yMin:Number, yMax:Number):Void{
		myClip.onPress = function(){
			this._parent.startDrag(false, xMin, yMin, xMax, yMax); //left,top(yMin),right,bottom(yMax))
			this.onMouseMove = function(){
				updateOnMove(); //optional update function
				updateAfterEvent();
			}//end of onMouseMove()
		}//end of onPress()
		myClip.onRelease = function(){
			this._parent.stopDrag();
			this.onMouseMove = undefined;
		}
		myClip.onReleaseOutside = myClip.onRelease;
	}//end of makeParentClipDraggable
	
	public static function makeClipDraggable(myClip:MovieClip, updateOnMove:Function, xMin:Number,xMax:Number,yMin:Number, yMax:Number):Void{
		myClip.onPress = function(){
			this.startDrag(false, xMin, yMin, xMax, yMax); //left,top(yMin),right,bottom(yMax))
			this.onMouseMove = function(){
				updateOnMove(); //optional updating function
				updateAfterEvent();
			}//end of onMouseMove()
		}//end of onPress()
		myClip.onRelease = function(){
			this.stopDrag();
			this.onMouseMove = undefined;
		}
		myClip.onReleaseOutside = myClip.onRelease;
	}//end of makeClipDraggable
	
}//end of class Util