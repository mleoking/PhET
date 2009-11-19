//general class for arrow graphic, used as momentum arrow
package{
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	
	
	public class Arrow extends Sprite{
		var index:int; 		//text label for arrow
		var canvas:Sprite;	//canvas holds arrow graphic, but not text label
		var scale:Number;		//adjustable scale for graphic, pixels per unit lengh of arrow
		var shaftW:int;			//width of shaft in pixels
		var shaftL:int;			//length of shaft in pixels
		var headL:int;			//length of head in pixels
		var lengthInPix:Number;	//length of arrow (shaft+head) in pixels
		var angleInDeg:Number;	//current angle in Degrees, from +x direction, CCW is +
		var angleInRad:Number;
		var tField:TextField;	//textFields cannot be rotated.
		var fillColor:uint;		//color of arrow
		var lineColor:uint;		//color of arrow border
		
		public function Arrow(indx:Number){
			this.index = indx+1;		//arrows are labeled 1, 2, ...
			this.canvas = new Sprite();
			this.scale = 50;			//adjustable
			this.shaftW = 6;
			this.headL = 12;
			this.tField = new TextField();
			var str:String = new String(this.index);
			this.tField.text = str;
			this.tField.selectable = false;
			this.tField.autoSize = TextFieldAutoSize.LEFT;
			fillColor = 0xffff00;
			lineColor = 0x0000ff;
			this.drawHorizArrow();
			this.addChild(this.canvas);
			this.addChild(tField);
		}//end of constructor
		
		public function drawHorizArrow():void{
			this.shaftL = this.lengthInPix - this.headL;
			with(this.canvas.graphics){
				clear();
				lineStyle(1, lineColor);
				beginFill(fillColor);
				moveTo(0,-shaftW/2);
				lineTo(shaftL, -shaftW/2);
				lineTo(shaftL, -shaftW);
				lineTo(shaftL + headL, 0);
				lineTo(shaftL, shaftW);
				lineTo(shaftL, shaftW/2);
				lineTo(0, shaftW/2);
				lineTo(0, -shaftW/2)
				endFill();
			}
		}//end of drawHorizArrow()
		
		public function setColor(fillColor:uint):void{
			this.fillColor = fillColor;
		}
		
		public function setArrow(vector:TwoVector):void{
			var L:Number = vector.getLength();
			this.lengthInPix = this.scale*L;
			this.drawHorizArrow();
			var angle:Number = vector.getAngle();
			this.angleInDeg = vector.getAngle();
			this.angleInRad = angleInDeg*Math.PI/180;
			this.canvas.rotation = -angleInDeg;
			this.tField.x = this.scale*L/2*Math.cos(angleInRad);
			this.tField.y = this.scale*L/2*Math.sin(-angleInRad);
		}
		
		//x coordinate in pixels of tip of arrow
		public function getTipX():Number{
			var LX:Number = this.lengthInPix*Math.cos(this.angleInRad);
			return LX;
		}
		//x coordinate in pixels of tip of arrow
		public function getTipY():Number{
			var LY:Number = this.lengthInPix*Math.sin(this.angleInRad);
			return LY;
		}
		
		public function setText(myText:String){
			this.tField.text = myText;
		}
		
		public function setScale(scale:Number):void{
			this.scale = scale;
		}
		
	}//end of class
}//end of package