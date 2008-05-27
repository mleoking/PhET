﻿//General utilities.  Useful for all sims

class Util{
	public static var STAGEH:Number = Stage.height;
	public static var STAGEW:Number = Stage.width;
	public static var ORIGINX:Number = 0.45*Stage.width;
	public static var ORIGINY:Number = 0.6*Stage.height;
	public static var BOTTOMY:Number = 0.90*Stage.height;
	public static var DEPTH:Number = 0;
	
	//colors for the bodies: 0xRRGGBB 
	public static var TRACECOLORS:Array = new Array(0xFFFF44, 0xFF44FF, 0x44FFFF, 0x44FF44);
	public static var BODYCOLORS:Array = new Array(0xFFFF00, 0xFF00FF, 0x00FFFF, 0x00FF00);
	
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
	
	public static function getNextDepth():Number{
		DEPTH += 1;
		return DEPTH;
	}
	
	
	//binomial coefficient
	public static function BC(N:Number, m:Number):Number{
		var result1:Number = 1;
		var result2:Number = 1;
		for(var i:Number = N; i > (N-m); i--){
			result1 *= i;
		}
		for(var j:Number = m; j >= 1; j--){
			result2 *= j;
		}	
		return result1/result2;
	}//end of BC()
	
}//end of class Util