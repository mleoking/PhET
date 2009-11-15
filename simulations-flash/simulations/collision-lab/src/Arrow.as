//general class for arrow graphic, used as momentum arrow
package{
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	
	
	public class Arrow extends Sprite{
		var index:int; //integer labels arrow
		//var canvas:Sprite;
		var shaft:Sprite;
		var head:Sprite;
		var scale:Number;	//adjustable scale for graphic
		var shaftW:int;		//width of shaft in pixels
		var shaftL:int;		//length of shaft in pixels
		
		public function Arrow(indx:Number){
			this.index = indx;
			//this.canvas = new Sprite();
			this.scale = 40;
			this.shaftW = 8;
			this.shaftL = this.scale*50;
			this.shaft = new Sprite();
			this.head = new Sprite();
			//this.addChild(this.canvas);
			this.initialize();
			this.addChild(this.shaft);
			this.addChild(this.head);
		}//end of constructor
		
		public function initialize():void{
			//draw shaft
			var fillColor:uint = 0xffff00;
			var lineColor:uint = 0x0000ff;
			with(this.shaft.graphics){
				clear();
				lineStyle(1, lineColor);
				beginFill(fillColor);
				moveTo(0,-shaftW/2);
				lineTo(shaftL, -shaftW/2);
				lineTo(shaftL, shaftW/2);
				lineTo(0, shaftW/2);
				lineTo(0, -shaftW/2)
				endFill();
			}
			with(this.head.graphics){
				clear();
				lineStyle(1, fillColor);
				beginFill(fillColor);
				moveTo(0,-shaftW);
				lineTo(2*shaftW, 0);
				lineTo(0, shaftW);
				lineTo(0, -shaftW);
				endFill();
				lineStyle(1, lineColor);
				moveTo(0,-shaftW/2);
				lineTo(0, -shaftW);
				lineTo(2*shaftW, 0);
				lineTo(0, shaftW);
				lineTo(0, shaftW/2);
			}
			this.shaft.x = 0;
			this.shaft.y = 0;
			this.head.x = shaftL;
			this.head.y = 0;
		}//end of initialize
		
		public function setArrow(vector:TwoVector):void{
			var L:Number = vector.getLength();
			var angle:Number = vector.getAngle();
			this.shaft.width = this.scale*L - this.head.width;
			this.head.x = this.shaft.width;
			this.rotation = -vector.getAngle();
		}
		
		public function getX():Number{
			return 1;
		}
		
	}//end of class
}//end of package